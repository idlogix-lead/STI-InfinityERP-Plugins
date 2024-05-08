package com.idlogix.starlet.issueRM.factories;

import org.adempiere.base.IProcessFactory;
import org.compiere.process.ProcessCall;

import com.idlogix.starlet.issueRM.processes.IssueRM_Cutting;
import com.idlogix.starlet.issueRM.processes.IssueRM_Lasting;
import com.idlogix.starlet.issueRM.processes.IssueRM_Packing;
import com.idlogix.starlet.issueRM.processes.IssueRM_Stitching;
import com.idlogix.starlet.issueRM.processes.IssueRM_Strobel;


public class MyProcessFactory implements IProcessFactory{

	@Override
	public ProcessCall newProcessInstance(String className) {
		// TODO Auto-generated method stub
		

		if(className.equals("com.idlogix.starlet.issueRM.IssueRM_Cutting"))
			return new IssueRM_Cutting();
		if(className.equals("com.idlogix.starlet.issueRM.IssueRM_Stitching"))
			return new IssueRM_Stitching();
		if(className.equals("com.idlogix.starlet.issueRM.IssueRM_Strobel"))
			return new IssueRM_Strobel();
		if(className.equals("com.idlogix.starlet.issueRM.IssueRM_Lasting"))
			return new IssueRM_Lasting();
		if(className.equals("com.idlogix.starlet.issueRM.IssueRM_Packing"))
			return new IssueRM_Packing();
		
		return null;
	}

}
