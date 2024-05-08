package org.eri.factories;

import org.adempiere.base.ITaxProviderFactory;
import org.adempiere.model.ITaxProvider;
import org.eri.taxprovider.DefaultTaxProvider;

public class TaxProviderFactory implements ITaxProviderFactory {

	@Override
	public ITaxProvider newTaxProviderInstance(String className) {
		// TODO Auto-generated method stub
		
		if(className.equalsIgnoreCase("org.eri.taxprovider.DefaultTaxProvider"))
			return new DefaultTaxProvider();
		
		return null;
	}

}
