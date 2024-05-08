/******************************************************************************
 * Copyright (C) 2019 Martin Sch�nbeck Beratungen GmbH  					  *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.eri.processes;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.eri.model.MCutting;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class GetArticleSizesForActivity extends SvrProcess {
	
	private int copyFrom, copyTo;

	@Override
	protected void prepare() {
		//get Parameters 
		ProcessInfoParameter[] paras = getParameter();
		for (ProcessInfoParameter p : paras) {
			String name = p.getParameterName();
			if (name.equalsIgnoreCase("m_parent_product_id")) {
				copyFrom = p.getParameterAsInt();
			}else {
				log.severe("Unknown Parameter: " + name);
			}
		}
		copyTo = getRecord_ID();
	}

	@Override
	protected String doIt() throws Exception {
		if (!(copyTo > 0)) {
			throw new AdempiereSystemError("No target product. Start process from product window.");
		}

		
		String strSQL ="";
//		strSQL ="select * from  M_Production where  ER_DailyProduction_ID=" + A_Tab.getValue("M_Production_ID")  + ";"  
				
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			pstmt = DB.prepareStatement (strSQL.toString(), get_TrxName());
			rs = pstmt.executeQuery ();
			
			while (rs.next ())		//	Order
			{
								
				MCutting pline = new MCutting(Env.getCtx(), 0, get_TrxName());
				pline.setAD_Org_ID(rs.getInt("AD_Org_ID"));
				
				pline.setM_Production_ID(rs.getInt("ER_DailyProduction_ID"));
				pline.setQty(new BigDecimal(rs.getInt("CuttingPlanQty")));
//				pline.set_ValueNoCheck("ER_CuttingHdr_ID", A_PO.getID());
				pline.set_ValueOfColumn(strSQL, pline);
				pline.save();
								
			}
			
			

		}
		catch (Exception e)
		{
			throw new AdempiereException(e.getMessage());
		}


		return "Copied ";
	}
}
