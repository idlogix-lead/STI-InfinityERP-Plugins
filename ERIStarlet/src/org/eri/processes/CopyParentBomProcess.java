/******************************************************************************
 * Copyright (C) 2019 Martin Schnbeck Beratungen GmbH  					  *
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.eri.model.MParentBOM;
import org.compiere.model.MOrder;
import org.compiere.model.MProduct;
import org.compiere.model.MProductBOM;
//import org.compiere.process.M_Production_Run;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereSystemError;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class CopyParentBomProcess extends SvrProcess {
	
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
//		if (!(new MProduct(getCtx(), copyTo, get_TrxName())).isBOM()) {
//			throw new AdempiereSystemError("Target product is not a BOM");
//		}
//		if (!(new MProduct(getCtx(), copyFrom, get_TrxName())).isBOM()) {
//			throw new AdempiereSystemError("The selected product is not a BOM");
//		}
		
//		MProductBOM lines[] = MProductBOM.getBOMLines(getCtx(), copyFrom, get_TrxName());
//		MProductBOM target;
//		int count = 0;
//		for (MProductBOM l :lines) {
//			target = new MProductBOM(getCtx(), 0, get_TrxName());
//			MProductBOM.copyValues(l, target);
//			target.setM_Product_ID(copyTo);
//			target.saveEx(get_TrxName());
//			count++;
//		}

		MParentBOM[] lines = MParentBOM.getBOMLines(getCtx(), copyFrom, get_TrxName());
		MProductBOM target;
		int count = 0;
		for (MParentBOM l :lines) {
			target = new MProductBOM(getCtx(), 0, get_TrxName());
			MProductBOM.copyValues(l, target);
			target.setM_Product_ID(copyTo);
			target.saveEx(get_TrxName());
			count++;
		}

		return "Copied "+count+" lines.";
	}
}
