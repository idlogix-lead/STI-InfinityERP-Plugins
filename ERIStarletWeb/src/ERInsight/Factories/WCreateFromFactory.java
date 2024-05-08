package ERInsight.Factories;

import org.compiere.grid.ICreateFrom;
import org.compiere.grid.ICreateFromFactory;
import org.compiere.model.GridTab;
import org.eri.model.MCoupon;
import org.eri.model.X_ER_LaborActivity;
import org.erinsight.webui.apps.form.WCreateFromCouponUI;

public class WCreateFromFactory implements ICreateFromFactory {

	@Override
	public ICreateFrom create(GridTab mTab) {
		// TODO Auto-generated method stub
		String tableName = mTab.getTableName();
		if (tableName.equals(X_ER_LaborActivity.Table_Name))
			return new WCreateFromCouponUI(mTab);

		return null;	
		
	}

}
