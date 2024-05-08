package org.eri.processes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.eri.model.MDailyProduction;
import org.eri.model.MProduct;
import org.eri.model.MProduction;

public class CreateSingleProductionOrder extends SvrProcess {

	int p_Instance_ID = 0;
	
	int M_Locator_ID = 0;

	@Override
	protected void prepare() {
		p_Instance_ID = getAD_PInstance_ID();

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("M_Locator_ID"))
				M_Locator_ID = (int)para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}

	}

	@Override
	protected String doIt() throws Exception {
		

		int OrgID = 0;
		
		String strSQL = "select  line.ad_org_id, line.er_masterproductionline_id, line.m_product_id, line.c_orderline_id, line.qty, \n" + 
				"        (select coalesce(sum(ps.productionqty), 0) from adempiere.m_production ps \n" + 
				"                where ps.er_masterproductionline_id = line.er_masterproductionline_id ) as totalqtyinproduction\n" + 
				"          from adempiere.er_masterproductionline line\n" + 
				"	where line.er_masterproductionline_id in (select t_selection_id from t_selection where t_selection.ad_pinstance_id = " +  p_Instance_ID + ")";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			
			
			MProduction mp = null;
			long millis=System.currentTimeMillis();  

			MDailyProduction dp = new MDailyProduction(Env.getCtx(), 0, get_TrxName());
			dp.setDateDoc(new Timestamp(millis));
			dp.save();
			addBufferLog(dp.get_ID(), dp.getDateDoc(), null, "New Daily Production", dp.get_Table_ID(),dp.get_ID());
			
			pstmt = DB.prepareStatement (strSQL.toString(), get_TrxName());
			rs = pstmt.executeQuery ();
			while (rs.next ())		//	Order
			{
				
				MProduct p = new MProduct(Env.getCtx(), rs.getInt("M_Product_ID"), get_TrxName());
				
				if (!p.isBOM())
				{
					if (p.bomLines() == 0)
					{
						throw new AdempiereUserError ("Bill Of Materials with no BOM Products");
					}
				}
				
				if (OrgID != rs.getInt("AD_Org_ID"))
				{
					OrgID = rs.getInt("AD_Org_ID");
				}
			
				mp = new MProduction(Env.getCtx(), 0, get_TrxName());
				mp.setAD_Org_ID(OrgID);
				mp.setM_Product_ID(rs.getInt("M_Product_ID"));
				mp.setMovementDate(new Timestamp(millis));
				mp.setProductionQty(rs.getBigDecimal("qty"));
				mp.setC_OrderLine_ID(rs.getInt("C_OrderLine_ID"));
				mp.setER_DailyProduction_ID(dp.get_ID());
				mp.setM_Locator_ID(M_Locator_ID);
				mp.setER_MasterProductionLine_ID(rs.getInt("ER_MasterProductionLine_ID"));
				
				
				//CuttingPlanQty, StitchingPlanQty, StrobelPlanQty, LastingPlanQty and PackingPlanQty
				mp.setCuttingPlanQty(mp.getProductionQty());
				mp.setStitchingPlanQty(mp.getProductionQty());
				mp.setStrobelPlanQty(mp.getProductionQty());
				mp.setLastingPlanQty(mp.getProductionQty());
				mp.setPackingPlanQty(mp.getProductionQty());
				
				mp.save();
				commit();
				ProcessInfoParameter pi1 = new ProcessInfoParameter("Record_ID", 1000024,"","","");
				ProcessInfo pi = new ProcessInfo("", 53226,0,0);
				pi.setParameter(new ProcessInfoParameter[] {pi1});
				MProcess pr = new Query(Env.getCtx(), MProcess.Table_Name, "value=?", null)
				                        .setParameters(new Object[]{"M_Production_Create"})
				                        .first();
				if (pr==null) {
				      return "Error";
				}
		
				ProductionCreate process = new ProductionCreate();
		
				MPInstance mpi = new MPInstance(Env.getCtx(), 0, null);
				mpi.setAD_Process_ID(pr.get_ID()); 
				mpi.setRecord_ID(mp.get_ID());
				mpi.save();
		
				pi.setAD_PInstance_ID(mpi.get_ID());
				pi.setRecord_ID(mp.get_ID());
				boolean result = process.startProcess(Env.getCtx(), pi, null);


			}
		}
		catch (Exception e)
		{
			throw new AdempiereException(e);
		}
		
		return "Work Done";	}

}
