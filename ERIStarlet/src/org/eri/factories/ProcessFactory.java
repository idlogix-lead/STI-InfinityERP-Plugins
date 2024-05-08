package org.eri.factories;

import org.adempiere.base.IProcessFactory;
import org.adempiere.base.equinox.EquinoxExtensionLocator;
import org.compiere.process.ProcessCall;

public class ProcessFactory implements IProcessFactory {
	
	/**
	 * default constructor
	 */
	public ProcessFactory() {
	}

	/* (non-Javadoc)
	 * @see org.adempiere.base.IProcessFactory#newProcessInstance(java.lang.String)
	 */
	@Override
	public ProcessCall newProcessInstance(String className) {
		ProcessCall process = null;
		process = EquinoxExtensionLocator.instance().locate(ProcessCall.class, "org.adempiere.base.Process", className, null).getExtension();
		if (process == null) {
			//Get Class
			Class<?> processClass = null;
			//use context classloader if available
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if (classLoader != null)
			{
				try
				{
					processClass = classLoader.loadClass(className);
				}
				catch (ClassNotFoundException ex)
				{
				}
			}
			if (processClass == null)
			{
				classLoader = this.getClass().getClassLoader();
				try
				{
					processClass = classLoader.loadClass(className);
				}
				catch (ClassNotFoundException ex)
				{
					return null;
				}
			}

			if (processClass == null) {
				return null;
			}

			//Get Process
			try
			{
				process = (ProcessCall)processClass.getDeclaredConstructor().newInstance();
			}
			catch (Exception ex)
			{
				return null;
			}
		}
		return process;
	}
}
