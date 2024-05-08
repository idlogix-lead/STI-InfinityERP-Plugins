package org.eri.processes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.eri.model.MMasterProduction;
import org.eri.model.MMasterProductionLine;

public class CreateMasterProductionPlan extends SvrProcess {

	int p_Instance_ID = 0;

	@Override
	protected void prepare() {
		p_Instance_ID = getAD_PInstance_ID();
		
	}

	@Override
	protected String doIt() throws Exception {
		
		int OrgID = 0;
		
		String strSQL = "select ad_org_id, C_OrderLine_ID, C_Order_ID, M_Product_ID, qtyEntered, " +
						"(select coalesce(sum(qty), 0) from er_masterproductionline where c_orderline_id = c_orderline.c_orderline_id) as qtyplanning " +
						"from c_orderline where c_orderline_id in " +
						"(SELECT C_OrderLine_ID From C_OrderLine line where line.c_order_id in (select T_Selection_ID From T_Selection WHERE AD_PInstance_ID = " + p_Instance_ID + ")) order by ad_org_id, C_order_id";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			
			MMasterProduction mp = null;
			long millis=System.currentTimeMillis();  
			
			pstmt = DB.prepareStatement (strSQL.toString(), get_TrxName());
			rs = pstmt.executeQuery ();
			while (rs.next ())		//	Order
			{
				
				if (OrgID != rs.getInt("AD_Org_ID"))
				{
					OrgID = rs.getInt("AD_Org_ID");
					mp = null;
				}
				
				if (mp == null)
				{
					mp = new MMasterProduction(Env.getCtx(), 0, get_TrxName());
					mp.setAD_Org_ID(OrgID);
					mp.setDateDoc(new Timestamp(millis));
					mp.setC_Order_ID(rs.getInt("C_Order_ID"));
					mp.save();

					addBufferLog(mp.get_ID(), mp.getDateDoc(), null, "New Production Plan", mp.get_Table_ID(),mp.get_ID());
				
				}
				
				MMasterProductionLine pline = new MMasterProductionLine(Env.getCtx(), 0, get_TrxName());
				pline.setAD_Org_ID(mp.getAD_Org_ID());
				pline.setM_Product_ID(rs.getInt("M_Product_ID"));
				pline.setC_OrderLine_ID(rs.getInt("C_OrderLine_ID"));
				pline.setQty(rs.getBigDecimal("qtyEntered").subtract(rs.getBigDecimal("qtyplanning")));
				pline.setER_MasterProduction_ID(mp.getER_MasterProduction_ID());
				pline.save();
//				
//				strSQL = "select * from adempiere.er_orderlineattribute where c_orderline_id = " + rs.getInt("C_OrderLine_ID");
//				
//				PreparedStatement pstmtchild = null;
//				ResultSet rschild = null;
//				pstmtchild = DB.prepareStatement (strSQL.toString(), get_TrxName());
//				rschild = pstmtchild.executeQuery ();
//				while (rschild.next ())		//	Order
//				{
//					X_ER_ProductionLineAttribute aline = new X_ER_ProductionLineAttribute(Env.getCtx(), 0, get_TrxName());
//					aline.setAD_Org_ID(pline.getAD_Org_ID());
//					aline.setER_MasterProductionLine_ID(pline.getER_MasterProductionLine_ID());
//					aline.setM_Product_ID(pline.getM_Product_ID());
//					aline.setM_AttributeValue_ID(rschild.getInt("M_AttributeValue_ID"));
//					aline.setQty(rschild.getBigDecimal("qty"));
//					aline.save();
//				}
			
			}
		}
		catch (Exception e)
		{
			throw new AdempiereException(e);
		}
		
		return "Work Done";
	}

}
