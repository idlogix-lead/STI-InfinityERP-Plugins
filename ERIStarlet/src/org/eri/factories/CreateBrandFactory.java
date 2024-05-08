package org.eri.factories;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;
import org.eri.processes.CreateBrandProcess;


public class CreateBrandFactory implements IProcessFactory {

	@Override
	public ProcessCall newProcessInstance(String className) {
		// TODO Auto-generated method stub
		if ("org.idlogix.Model.CreateBrandProcess".equals(className)) {
			return new CreateBrandProcess();
			
		}
		return null;
	}

}
