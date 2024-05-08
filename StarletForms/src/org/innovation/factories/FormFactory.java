package org.innovation.factories;



import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.innovation.forms.SOPForm;


public class FormFactory implements IFormFactory {

	@Override
	public ADForm newFormInstance(String formName) {
		// TODO Auto-generated method stub
		if(formName.equalsIgnoreCase("SOPForm"))
			return new SOPForm();
		
		return null;
	}

}

