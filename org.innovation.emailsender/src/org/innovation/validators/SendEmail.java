package org.innovation.validators;

import org.compiere.model.MClient;
import org.compiere.model.MOrder;
import org.compiere.model.MPayment;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.innovation.templates.*;

public class SendEmail implements ModelValidator{

	@Override
	public void initialize(ModelValidationEngine engine, MClient client) {
		// TODO Auto-generated method stub
		engine.addDocValidate(MOrder.Table_Name, this);
		engine.addDocValidate(MPayment.Table_Name, this);
	}

	@Override
	public int getAD_Client_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String modelChange(PO po, int type) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String docValidate(PO po, int timing) {
		// TODO Auto-generated method stub
		if(timing!=TIMING_AFTER_COMPLETE)
			return null;
		if(po.get_Table_ID()==MOrder.Table_ID)
		{
			POEmail poEmail = new POEmail();
			poEmail.sendEmail(po);
		}
		if(po.get_Table_ID()==MPayment.Table_ID)
		{
			PaymentEmail pEmail = new PaymentEmail();
			pEmail.sendEmail(po);
		}
		return null;
	}

}
