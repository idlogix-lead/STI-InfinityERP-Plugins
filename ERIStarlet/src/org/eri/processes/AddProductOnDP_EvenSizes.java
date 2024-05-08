package org.eri.processes;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.eri.model.MDailyProduction;
import org.eri.model.MProduction;
import org.eri.model.X_ER_Cutting_Plan;
import org.eri.model.X_ER_Lasting_Plan;
import org.eri.model.X_ER_Packing_Plan;
import org.eri.model.X_ER_Stitching_Plan;
import org.eri.model.X_ER_Strobel_Plan;

public class AddProductOnDP_EvenSizes extends SvrProcess {
	int StitchingLine_ID = 0;
	int LastingLine_ID = 0;
	int CuttingLine_ID = 0;
	int StrobelLine_ID = 0;
	int PackingLine_ID = 0;
	int SalesOrderID=0;
	int id = 0;
	int C_Order_ParentProduct_ID=0;	
	int M_Locator_ID = 0;
	BigDecimal totalQty = Env.ZERO;
	BigDecimal currentTicketQty = Env.ZERO;
	BigDecimal pack_size = Env.ZERO;
	Timestamp DateTrx = null;
	List<Double> pending_qty    = new ArrayList<Double>();
	List<Integer> packing_qty    = new ArrayList<Integer>();
	List<Integer> prod_ids      = new ArrayList<Integer>();
	List<Integer> order_lines      = new ArrayList<Integer>();
	double ppGeneratedQty;
	double total_pp_qty=0.0;
	@Override
	protected void prepare() {
		id = getRecord_ID();
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("M_Locator_ID"))
				M_Locator_ID = para[i].getParameterAsInt();			
			else if (name.equals("MovementQty"))
				{
				totalQty = para[i].getParameterAsBigDecimal();
				currentTicketQty = para[i].getParameterAsBigDecimal();				
				}
			else if (name.equals("PackingSize"))
			{
				pack_size = para[i].getParameterAsBigDecimal();
			}
			else if (name.equals("StitchingLine_ID"))
				StitchingLine_ID = para[i].getParameterAsInt();

			else if (name.equals("LastingLine_ID"))
				LastingLine_ID = para[i].getParameterAsInt();

			else if (name.equals("CuttingLine_ID"))
				CuttingLine_ID = para[i].getParameterAsInt();

			else if (name.equals("StrobelLine_ID"))
				StrobelLine_ID = para[i].getParameterAsInt();

			else if (name.equals("PackingLine_ID"))
				PackingLine_ID = para[i].getParameterAsInt();

			else if (name.equals("DateTrx"))
				DateTrx = para[i].getParameterAsTimestamp();
			else if (name.equals("C_Order_ID"))
				SalesOrderID = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}

	@Override
	protected String doIt() throws Exception {
		String strSQL = "select od.c_order_id,mp.er_masterproduction_id,odpp.c_order_parentproduct_id,odpp.m_parent_product_id,SUM(qtyordered)::integer qty\n"
				+ "from adempiere.c_order od\n"
				+ "join adempiere.er_masterproduction mp ON od.c_order_id = mp.c_order_id\n"
				+ "join adempiere.c_order_parentproduct odpp ON od.c_order_id = odpp.c_order_id\n"
				+ "join adempiere.c_orderline ol ON od.c_order_id =ol.c_order_id and odpp.c_order_parentproduct_id = ol.c_order_parentproduct_id\n"
				+ "where od.c_order_id = "+ SalesOrderID+" \n"
				+ "group by od.c_order_id,mp.er_masterproduction_id,odpp.c_order_parentproduct_id,odpp.m_parent_product_id\n"
				+ "order by c_order_parentproduct_id";
				PreparedStatement pstmt = null;
				ResultSet rs = null;		
				try
				{			
					pstmt = DB.prepareStatement (strSQL.toString(), null);
					rs = pstmt.executeQuery ();
					while (rs.next ())		
					{				
						int c_order_id = rs.getInt("c_order_id");
						int er_masterproduction_id = rs.getInt("er_masterproduction_id");
						int c_order_parentproduct_id = rs.getInt("c_order_parentproduct_id");
						int m_parent_product_id = rs.getInt("m_parent_product_id");
						int totalQty = rs.getInt("qty");
						createTickets(c_order_id,er_masterproduction_id,c_order_parentproduct_id,m_parent_product_id,totalQty);
						
					}
				}
				catch (Exception e)
				{
					throw new AdempiereException(e);
				}
	return "";
	}
	public void createTickets(int C_Order_ID,int ER_MasterProduction_ID,int C_Order_ParentProduct_ID,int M_parent_Product_ID,int qty)
	{
		
		ppGeneratedQty=0.0;
		while(ppGeneratedQty<qty) {
			initializeSizesData(C_Order_ID, ER_MasterProduction_ID, C_Order_ParentProduct_ID, M_parent_Product_ID);
			MDailyProduction ticket = createTciket(C_Order_ID, ER_MasterProduction_ID, C_Order_ParentProduct_ID, M_parent_Product_ID);
			ticketAlgo(ticket);
		}
	}
	public void initializeSizesData(int C_Order_ID,int ERMasterProductionID,int COrderParentProductID,int MParent_Product_ID) {
		//flush old data out
		pending_qty.clear();
		packing_qty.clear();
		prod_ids.clear();
		order_lines.clear();
		total_pp_qty=0.0;
		// Query Data
		C_Order_ParentProduct_ID = COrderParentProductID;	
		if(  C_Order_ParentProduct_ID == -1)
			 C_Order_ParentProduct_ID =MParent_Product_ID;
		String strSQL = "select ol.c_orderline_id,ppv.m_product_id,SUM(planqty-ticketqty)::integer pending_qty,p.PairsPerCarton::integer  \n"
		+ "FROM adempiere.m_plan_vs_production_v ppv\n"
	    + "LEFT JOIN adempiere.m_product p ON ppv.m_product_id =  p.m_product_id\n"
		+ "left join adempiere.er_masterproductionline mpl ON ppv.er_masterproductionline_id = mpl.er_masterproductionline_id\n"
		+ "left join adempiere.c_orderline ol ON mpl.c_orderline_id = ol.c_orderline_id\n"
		+ "where  p.PairsPerCarton is not null and p.PairsPerCarton >0 and ppv.er_masterproduction_id = "+ERMasterProductionID+"\n"
		+ " and ppv.c_order_parentproduct_id = "+ C_Order_ParentProduct_ID +"\n"
		+ "group by ol.c_orderline_id,ppv.m_product_id,p.PairsPerCarton\n"
		+ "having SUM(planqty-ticketqty) > 0 \n"
		+ "order by ppv.m_product_id";
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		try
		{			
			pstmt = DB.prepareStatement (strSQL.toString(), null);
			rs = pstmt.executeQuery ();
			while (rs.next ())		
			{				
					packing_qty.add(rs.getInt("PairsPerCarton"));
					prod_ids.add(rs.getInt("m_product_id"));
					order_lines.add(rs.getInt("c_orderline_id"));
					pending_qty.add(rs.getDouble("pending_qty"));
					total_pp_qty+=rs.getDouble("pending_qty");
			}
		}
		catch (Exception e)
		{
			throw new AdempiereException(e);
		}
	}
	public MDailyProduction createTciket(int C_Order_ID,int ER_MasterProduction_ID,int C_Order_ParentProduct_ID,int M_parent_Product_ID) {
		MDailyProduction  ticket = new MDailyProduction(Env.getCtx(), 0, get_TrxName());
		ticket.set_ValueOfColumn("C_Order_ID", C_Order_ID);
		ticket.setER_MasterProduction_ID(ER_MasterProduction_ID);
		ticket.setM_Parent_Product_ID(M_parent_Product_ID);
		ticket.setC_Order_ParentProduct_ID(C_Order_ParentProduct_ID);
		Date currentdate = new Date();
	    Timestamp docDate = new Timestamp(currentdate.getTime());
	    ticket.set_ValueOfColumn("DateDoc", docDate);
	    ticket.save();
	    return ticket;		
	}
	public void ticketAlgo(MDailyProduction ticket) {
		 double ticket_qty=currentTicketQty.doubleValue();		
		 boolean makeAll = total_pp_qty<=ticket_qty?true:false;
		 ticket_qty = ticket_qty>total_pp_qty?total_pp_qty:ticket_qty; 
		 List<Double> actual_qty = new ArrayList<Double>();
		 for(int i=0;i<pending_qty.size();i++) 
			 actual_qty.add(0.0);
	 	if(!makeAll) {
	 		 for(int i=0;i<pending_qty.size();i++) {
				 double wrt_totalPPQty = (pending_qty.get(i)/total_pp_qty) * 100.0;
				 double wrt_ticketQty  = (wrt_totalPPQty/100.0)*ticket_qty;
				 double qty  = wrt_ticketQty - (wrt_ticketQty % packing_qty.get(i));
				 if(qty<=pending_qty.get(i))
					 actual_qty.set(i,qty);
			 }
	 	}
	 	else {
	 		for(int i=0;i<pending_qty.size();i++) 
	 		{
				 actual_qty.set(i, pending_qty.get(i));
			 }
	 	}
	 	ppGeneratedQty+=sum(actual_qty);
		for(int i=0;i<pending_qty.size();i++) 
		{
			if(actual_qty.get(i)> 0.0) {
				MProduction pline = new MProduction(Env.getCtx(), 0, get_TrxName());
				pline.setAD_Org_ID(ticket.getAD_Org_ID());
				pline.setM_Product_ID(prod_ids.get(i));
				pline.setC_OrderLine_ID(order_lines.get(i));
				pline.setProductionQty(new BigDecimal(actual_qty.get(i)));
				pline.setMovementDate(ticket.getDateDoc());
				pline.setER_DailyProduction_ID(ticket.get_ID());
				pline.setM_Locator_ID(M_Locator_ID);			
				pline.setER_MasterProductionLine_ID(ticket.getER_MasterProduction_ID());
				boolean ret = false;
				ret = pline.save();
				pline.saveEx();
				if (ret)
				{
					commit();
					ProcessInfoParameter pi1 = new ProcessInfoParameter("Record_ID", 1000024,"","","");
					ProcessInfo pi = new ProcessInfo("", 53226,0,0);
					pi.setParameter(new ProcessInfoParameter[] {pi1});
					MProcess pr = new Query(Env.getCtx(), MProcess.Table_Name, "value=?", null)
					                        .setParameters(new Object[]{"M_Production_Create"})
					                        .first();
					if (pr==null) 
					{
				      return ;
					}
					ProductionCreate process = new ProductionCreate();
					MPInstance mpi = new MPInstance(Env.getCtx(), 0, null);
					mpi.setAD_Process_ID(pr.get_ID()); 
					mpi.setRecord_ID(ticket.get_ID());
					mpi.save();
					pi.setAD_PInstance_ID(mpi.get_ID());
					pi.setRecord_ID(pline.get_ID());
					boolean result = process.startProcess(Env.getCtx(), pi, null);
					if (result == false)
					{
						return ;
					}
				}
			}
		}
		
	}
	public static double sum(List<Double> list) {
		double sum=0.0;
		for(double d:list)
		sum+=d;
		return sum;
	}
}

	


// if in future ticketQty will be taken as input from user
// below validation would be necessary in case user enters
// a ticketQty that is just not enough to produce any combination of cartons
// i.e. floor(% of size w.r.t ticket) == 0 for all sizes

//int position = 0;
//for(int i=0;i<pending_qty.size();i++) {
//	if(pending_qty.get(i)/packing_qty.get(i)>cartons) {
//		cartons = (double)pending_qty.get(i)/(double)packing_qty.get(i);
//		position = i;
//	}
//}
//double minCapacity = ((double)pending_qty.get(position)*(double)ticket_qty)/(double)total_pp_qty;
//if(minCapacity<cartons)
//	return;
