package com.idlogix.starlet.issueRM.processes;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.eri.model.MDailyProduction;
import org.eri.model.MMasterProduction;
import org.eri.model.MMasterProductionLine;
import org.eri.model.MMovement;
import org.eri.model.MMovementLine;
import org.eri.model.MProduct;
import org.eri.model.MProductBOM;
import org.eri.model.MProduction;

public class IssueRM_Lasting extends SvrProcess {
	
	int ER_MasterProduction_ID = 0;
	int C_Activity_ID=0;
	int ER_DailyProduction_ID=0;
	int M_Product_ID=0;

	int M_Locator_ID = 0;
	int M_LocatorTo_ID=0;
	private int p_Instance_ID;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("ER_MasterProduction_ID"))
				ER_MasterProduction_ID = para[i].getParameterAsInt();

			else if (name.equals("M_Product_ID"))
				M_Product_ID = para[i].getParameterAsInt();

			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
		 
		 p_Instance_ID = getAD_PInstance_ID();
		 M_Locator_ID = 1000000;
		 M_LocatorTo_ID=1000002;
		 C_Activity_ID=1000001;
		 
		 String strSQL = "select T_Selection_ID::integer From T_Selection WHERE AD_PInstance_ID = " + p_Instance_ID ;
		 PreparedStatement pstmt = null;
		 ResultSet rs = null;	
		 try
		 {
		 pstmt = DB.prepareStatement (strSQL.toString(), get_TrxName());
		 rs = pstmt.executeQuery ();
		 rs.next();
		 ER_DailyProduction_ID=rs.getInt("t_selection_id");	
		 }
		 catch (Exception e)
		 {
		 	throw new AdempiereException(e);
		 }
	}
	protected String IssueViaDP()
	{
		
		MDailyProduction dp = new MDailyProduction(Env.getCtx(), ER_DailyProduction_ID, get_TrxName());
		if (dp==null)
			return "Invalid Daily Production";
		
		MProduction[] mplines = null;
		
//		if (M_Product_ID != 0)
//		{
//			mplines = dp.getLines(" AND M_Product_ID = " + M_Product_ID, "");	
//		}
//		else
//		{
//		}
		System.out.println("Before dp.getLines");
		mplines = dp.getLines("", "");
		MMovement mov = new MMovement(Env.getCtx(), 0, get_TrxName());
		mov.setAD_Org_ID(dp.getAD_Org_ID());
//		mov.setMovementDate(dp.getDateDoc());
		mov.setMovementDate(new Timestamp(System.currentTimeMillis()));
		mov.setDescription("RM Issue for MP:" +  dp.getDocumentNo());
//		mov.setER_DailyProduction_ID(dp.get_ID());
		
		if (C_Activity_ID!= 0)
			mov.setC_Activity_ID(C_Activity_ID);
		
		mov.save();
		
		StringBuilder sb = new StringBuilder("select pro.m_product_id, \n" +  
				"        sum(pro.qtyUsed) as requiredqty,\n" + 
				"                \n" + 
				"                (select \n" + 
				"                        coalesce(sum(movementqty), 0)\n" + 
				"                        from m_movementline line join m_movement mv on line.m_movement_id = mv.m_movement_id \n" + 
				"                        where line.m_product_id = pro.m_product_id\n" + 
				"                        and line.ER_DailyProduction_ID=m_pro.ER_DailyProduction_ID\n" + 
				"							and mv.c_activity_id=" + C_Activity_ID + " \n" +
				"                ) as issuedqty,\n" + 
				"                \n" + 
				"                coalesce(sum(pro.qtyUsed), 0) - \n" + 
				"                (select \n" + 
				"                        coalesce(sum(movementqty), 0)\n" + 
				"                        from m_movementline line join m_movement mv on line.m_movement_id = mv.m_movement_id \n" + 
				"                        where line.m_product_id = pro.m_product_id\n" + 
				"                        and line.ER_DailyProduction_ID=m_pro.ER_DailyProduction_ID\n" +
				"							and mv.c_activity_id=" + C_Activity_ID + " \n" +
				"                ) as balanceqty\n" + 
				"\n" + 
				"                from M_ProductionLine pro\n" + 
				"                join M_Production m_pro on pro.M_Production_ID=m_pro.M_Production_ID\n" + 
				"         where  m_pro.ER_DailyProduction_ID=" + ER_DailyProduction_ID + "\n" + 
				"                and pro.C_Activity_ID=" + C_Activity_ID + "\n");
		
		if (M_Product_ID != 0)
		{
			sb.append(" AND pro.m_product_id=" + M_Product_ID );
		}
		
		sb.append("         group by m_pro.ER_DailyProduction_ID, pro.m_product_id\n" + 
				"         having \n" + 
				"                coalesce(sum(pro.qtyUsed), 0) - \n" + 
				"                (select \n" + 
				"                        coalesce(sum(movementqty), 0)\n" + 
				"                        from m_movementline line join m_movement mv on line.m_movement_id = mv.m_movement_id \n" + 
				"                        where line.m_product_id = pro.m_product_id\n" + 
				"                        and line.ER_DailyProduction_ID=m_pro.ER_DailyProduction_ID\n" +
				"							and mv.c_activity_id=" + C_Activity_ID + " \n" +
				"                ) > 0"); 

		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement (sb.toString(), get_TrxName());
			ResultSet rs = null;
			
			rs = pstmt.executeQuery ();
			while (rs.next ())
			{
				
				MMovementLine line = new MMovementLine(mov);
				line.setM_Locator_ID(M_Locator_ID);
				line.setM_LocatorTo_ID(M_LocatorTo_ID);
				line.setM_Product_ID(rs.getInt("m_product_id"));
				line.setMovementQty(rs.getBigDecimal("balanceqty"));
				line.set_ValueOfColumn("CurTicketQty", rs.getBigDecimal("balanceqty"));
				line.setER_DailyProduction_ID(ER_DailyProduction_ID);
				if (!line.save())
				{
					rollback();
					return "Error while saving inventory move line, please check available stock for product (" + rs.getInt("m_product_id") + ")";
				}

			}
			
		}
			catch (Exception e)
		{
			throw new AdempiereException(e);
		}
		
//		
//		for (MProduction mpline : mplines) {
//			MProduct p = (MProduct) mpline.getM_Product();
//			
//			int bomlines = p.bomLines();
//			if (bomlines==0)
//			{
//				return "BOM For Product : " + p.getName() + " is not defined";
//			}
//		
//			MProductBOM[] productsBOMs = null;
//			
//			if (C_Activity_ID != 0)
//			{
//			
//				if (M_Product_ID != 0)
//				{
//					productsBOMs = MProductBOM.getBOMLines(Env.getCtx(), p.get_ID(), get_TrxName(), C_Activity_ID, M_Product_ID);
//				}
//				else
//				{
//					productsBOMs = MProductBOM.getBOMLines(Env.getCtx(), p.get_ID(), get_TrxName(), C_Activity_ID);	
//				}
//			
//			}
//			else
//			{
//				productsBOMs = MProductBOM.getBOMLines(Env.getCtx(), p.get_ID(), get_TrxName());
//			}
//			
//			for (MProductBOM bomline : productsBOMs) {
//				if (C_Activity_ID!=0)
//				{
//				}
//				MMovementLine line = new MMovementLine(mov);
//				line.setM_Locator_ID(M_Locator_ID);
//				line.setM_LocatorTo_ID(M_LocatorTo_ID);
//				line.setM_Product_ID(bomline.getM_ProductBOM_ID());
//				line.setMovementQty(bomline.getBOMQty().multiply(mpline.getProductionQty()));
//				line.setM_Production_ID(mpline.get_ID());
//				line.setER_DailyProduction_ID(ER_DailyProduction_ID);
//				if (!line.save())
//				{
//					rollback();
//					return "Error while saving inventory move line, please check available stock for product (" + bomline.getProduct().getName() + ")";
//				}
//			}
//		}
//		
		addBufferLog(mov.get_ID(), mov.getMovementDate(), null, "Inventory Move # " + mov.getDocumentNo(), mov.get_Table_ID(), mov.get_ID());
		return "OK";	
	}

	@Override
	protected String doIt() throws Exception {
		
		if (ER_DailyProduction_ID!= 0)
		{
			return IssueViaDP();
		}
		
		MMasterProduction mp = new MMasterProduction(Env.getCtx(), ER_MasterProduction_ID, get_TrxName());
		if (mp==null)
			return "Invalid Master Production";
		
		MMasterProductionLine[] mplines = mp.getLines("", "");
		
		MMovement mov = new MMovement(Env.getCtx(), 0, get_TrxName());
		mov.setAD_Org_ID(mp.getAD_Org_ID());
		mov.setMovementDate(mp.getDateDoc());
		mov.setDescription("RM Issue for MP:" +  mp.getDocumentNo());
		mov.setER_MasterProduction_ID(mp.get_ID());
		
		if (C_Activity_ID!= 0)
			mov.setC_Activity_ID(C_Activity_ID);

		mov.save();
		
		for (MMasterProductionLine mpline : mplines) {
			MProduct p = mpline.getM_Product();
			
			int bomlines = p.bomLines();
			if (bomlines==0)
			{
				return "BOM For Product : " + p.getName() + " is not defined";
			}
		
			MProductBOM[] productsBOMs = null;
			
			if (C_Activity_ID != 0)
			{
				productsBOMs = MProductBOM.getBOMLines(Env.getCtx(), p.get_ID(), get_TrxName(), C_Activity_ID);	
			}
			else
			{
				productsBOMs = MProductBOM.getBOMLines(Env.getCtx(), p.get_ID(), get_TrxName());
			}
			
			for (MProductBOM bomline : productsBOMs) {
				
				BigDecimal reqQty = bomline.getBOMQty().multiply(mpline.getQty());
				
				if (C_Activity_ID!=0)
				{
				}
				
				MMovementLine line = new MMovementLine(mov);
				line.setM_Locator_ID(M_Locator_ID);
				line.setM_LocatorTo_ID(M_LocatorTo_ID);
				line.setM_Product_ID(bomline.getM_ProductBOM_ID());
				line.setMovementQty(reqQty);
				line.set_ValueOfColumn("CurTicketQty", reqQty);
				
				if (!line.save())
				{
					rollback();
					return "Error while saving inventory move line, please check available stock for product (" + bomline.getProduct().getName() + ")";
				}
				
			}
		}
		
		addBufferLog(mov.get_ID(), mov.getMovementDate(), null, "Inventory Move # " + mov.getDocumentNo(), mov.get_Table_ID(), mov.get_ID());
		return "OK";
	}

}
