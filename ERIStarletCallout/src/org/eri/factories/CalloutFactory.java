package org.eri.factories;

import java.util.ArrayList;
import java.util.List;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;
import org.eri.callouts.SetProductName;
import org.eri.model.MProduct;

public class CalloutFactory implements IColumnCalloutFactory {

	@Override
	public IColumnCallout[] getColumnCallouts(String tableName, String columnName) {

		List<IColumnCallout> list = new ArrayList<IColumnCallout>();
		
//		if (tableName.equals(MProduct.Table_Name)) 
//		{
//			if (columnName.equals(MProduct.COLUMNNAME_ER_Article_ID) ||
//					columnName.equals(MProduct.COLUMNNAME_ER_Color_ID) ||
//					columnName.equals(MProduct.COLUMNNAME_ER_Size_ID) ||
//					columnName.equals(MProduct.COLUMNNAME_M_Parent_Product_ID)
//					)
//			{
//				list.add(new SetProductName());
//			}
//		}

		return list != null ? list.toArray(new  IColumnCallout[0]) : new IColumnCallout[0];
	}

}