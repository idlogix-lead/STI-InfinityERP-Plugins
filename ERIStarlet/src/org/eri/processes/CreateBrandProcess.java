package org.eri.processes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.eri.model.X_M_Brand;
public class CreateBrandProcess extends SvrProcess  {

	String name="";
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		ProcessInfoParameter[] paras = getParameter();
		for (ProcessInfoParameter p : paras) {
			String name = p.getParameterName();
			if (name.equalsIgnoreCase("Brand_Name")) {
				name = p.getParameterAsString();
			}else {
				log.severe("Unknown Parameter: " + name);
			}
		}
		
	}

	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub
		int brand_id=0;
		
		String strSQL = "select (coalesce(max(m_brand_id),999999))::Integer as id \n" + 
				"		from adempiere.er_article";

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			pstmt = DB.prepareStatement (strSQL.toString(), null);
			rs = pstmt.executeQuery ();
			
			while (rs.next ())		//	Order
			{
				brand_id = rs.getInt("id")+1;
			}
		}
		catch (Exception e)
		{
			throw new AdempiereException(e);
		}
		System.out.println("Name = "+name+" Brand_id = "+Integer.toString(brand_id));
		X_M_Brand brand = new X_M_Brand ( getCtx(), brand_id, null);
		brand.setM_Brand_ID(brand_id);
		brand.setName(name);
		brand.saveEx(get_TrxName());
		return null;
	}

}
