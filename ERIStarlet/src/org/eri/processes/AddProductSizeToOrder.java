package org.eri.processes;

import java.math.BigDecimal;
import java.util.logging.Level;

import org.compiere.model.MOrderLine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.eri.model.MOrderLineAttribute;

public class AddProductSizeToOrder extends SvrProcess {

	int M_Product_ID = 0;
	int C_OrderLine_ID = 0;
	int M_AttributeValue_ID = 0;
	BigDecimal Qty = Env.ZERO;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("M_AttributeValue_ID"))
				M_AttributeValue_ID = para[i].getParameterAsInt();

			else if (name.equals("M_Product_ID"))
				M_Product_ID = para[i].getParameterAsInt();
			else if (name.equals("Qty"))
				Qty = para[i].getParameterAsBigDecimal();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}

	@Override
	protected String doIt() throws Exception {
		C_OrderLine_ID = getRecord_ID();
		MOrderLine oline = new MOrderLine(Env.getCtx(), C_OrderLine_ID, get_TrxName());
		
		MOrderLineAttribute x = new MOrderLineAttribute(Env.getCtx(), 0, get_TrxName());
		x.setAD_Org_ID(oline.getAD_Org_ID());
		x.setC_OrderLine_ID(C_OrderLine_ID);
		x.setM_AttributeValue_ID(M_AttributeValue_ID);
		x.setQty(Qty);
		x.setM_Product_ID(M_Product_ID);
		x.save();
		
		return String.valueOf(C_OrderLine_ID);
	}

}
