package org.eri.processes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.eri.model.MCoupon;
import org.eri.model.MDailyProduction;
import org.eri.model.MProduction;

public class GenerateCoupons extends SvrProcess {

	int ER_DailyProduction_ID=0;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("ER_DailyProduction_ID"))
				ER_DailyProduction_ID = para[i].getParameterAsInt();

			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}		
	}

	@Override
	protected String doIt() throws Exception {
		
		MDailyProduction dp = new MDailyProduction(Env.getCtx(), ER_DailyProduction_ID, get_TrxName());
		
		MProduction[] productionlines = dp.getLines();
		for (MProduction pro : productionlines) {
			
			String strSQL = "select distinct bom.C_Activity_ID, op.ER_Operation_ID, op.name from M_Product_BOM bom\n" + 
					"        inner join adempiere.er_operation op on op.c_activity_id=bom.c_activity_id\n" + 
					"        where m_product_id=" + pro.getM_Product_ID() ;
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = DB.prepareStatement (strSQL.toString(), get_TrxName());
				rs = pstmt.executeQuery ();
				while (rs.next ())
				{
					MCoupon cp = new MCoupon(Env.getCtx(), 0, get_TrxName());
					cp.setAD_Org_ID(dp.getAD_Client_ID());
					cp.setM_Production_ID(pro.get_ID());
					cp.setER_Operation_ID(rs.getInt("ER_Operation_ID"));
					cp.setQty(pro.getProductionQty());
					cp.save();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		
		return "Work Done";
	}

}
