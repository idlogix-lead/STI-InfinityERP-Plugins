package ERInsight.Factories;

import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.erinsight.webui.apps.form.AddActivityProduction;

public class STIFormFactory implements IFormFactory{

	@Override
	public ADForm newFormInstance(String formName) {
		// TODO Auto-generated method stub
		if(formName.equalsIgnoreCase("AddActivityProduction"))
			return new AddActivityProduction();
		return null;
	}

}
