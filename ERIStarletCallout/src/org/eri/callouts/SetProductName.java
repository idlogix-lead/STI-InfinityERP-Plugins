package org.eri.callouts;

import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.Env;
import org.eri.model.MProduct;
import org.eri.model.X_ER_Color;
import org.eri.model.X_ER_Size;
import org.eri.model.X_M_Parent_Product;

public class SetProductName implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value, Object oldValue) {
		
		try {

			String strCode = "";
			
			int PID = (int) mTab.getValue(MProduct.COLUMNNAME_M_Parent_Product_ID);
			int SizeID = (int) mTab.getValue(MProduct.COLUMNNAME_ER_Size_ID);
			int ColorID = (int) mTab.getValue(MProduct.COLUMNNAME_ER_Color_ID);

			X_M_Parent_Product pproudct = null;
			X_ER_Size size = null;
			X_ER_Color color =null;
			
			if (PID > 0)
			{
				pproudct = new X_M_Parent_Product(Env.getCtx(), PID, "noname");
			}

			if ( SizeID > 0)
			{
				size = new X_ER_Size(Env.getCtx(), SizeID, "noname");
			}
			
			if ( ColorID > 0)
			{
				color = new X_ER_Color(Env.getCtx(), ColorID, "noname");
			}
			
			if (pproudct == null) { return ""; };
			if (size == null) { return ""; };
			if (color == null) { return ""; };
			
			strCode = pproudct.getName() + " " + color.getName() + " " + size.getName();

			mTab.setValue(MProduct.COLUMNNAME_Name , strCode);
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		return "";
	}

}
