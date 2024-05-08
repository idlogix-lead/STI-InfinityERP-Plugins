package org.innovation.factories;

import org.adempiere.base.IModelValidatorFactory;
import org.compiere.model.ModelValidator;
import org.innovation.validators.SendEmail;



public class EmailValidatorFactory implements IModelValidatorFactory {

	@Override
	public ModelValidator newModelValidatorInstance(String className) {
		// TODO Auto-generated method stub
		if(className.equalsIgnoreCase("org.innovation.validators.SendEmail"))
			return new SendEmail();
		return null;
	}

}