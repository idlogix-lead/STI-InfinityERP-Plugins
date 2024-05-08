package org.innovation.templates;

import java.io.File;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MClient;
import org.compiere.model.MPInstance;
import org.compiere.model.PO;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.ServerProcessCtl;
import org.compiere.util.CLogger;
import org.compiere.util.EMail;
import org.compiere.util.Env;

public class POEmail {
	StringBuilder email_body;
    protected transient CLogger log;
    public POEmail() {
    	this.log = CLogger.getCLogger((Class)this.getClass());
    }
    public void sendEmail(PO obj) {
    	final PO po = obj;
        
        if(po.get_ValueAsBoolean("issotrx"))
        	return;
        
        final int bPartnerID = po.get_ValueAsInt("C_BPartner_ID");
        final int clientID = po.get_ValueAsInt("AD_Client_ID");
        final MBPartner bp = new MBPartner(Env.getCtx(), bPartnerID, (String)null);
        final MBPartnerLocation bpl = new MBPartnerLocation(Env.getCtx(), bp.getPrimaryC_BPartner_Location_ID(), (String)null);
        String email_add = bpl.get_ValueAsString("Email_ID");
        if (this.isValidEmail(email_add)) {
            email_add = email_add.trim();
            this.setEmailBody();
            final MClient client = new MClient(Env.getCtx(), clientID, (String)null);
            final EMail email = new EMail(client, client.getRequestEMail(), email_add, "Purchase Order Acknowledgement!", this.email_body.toString(), true);
            email.createAuthenticator(client.getRequestUser(), client.getRequestUserPW());
            email.addAttachment(getAttachment(po));
            if (email == null) {
                new StringBuilder("Could not create EMail: ");
                return;
            }
            try {
            	email.addCc("erp@groupstarlet.com");
                email.addCc("mgr.procurement@groupstarlet.com");
                email.addCc("momen@groupstarlet.com");
                final String msg = email.send();
                if ("OK".equals(msg)) {
                    if (this.log.isLoggable(Level.INFO)) {
                        this.log.info("Sent Test EMail to " + client.getRequestEMail());
                    }
                    return;
                }
                this.log.warning("Could NOT EMail from " + client.getSMTPHost() + ": " + client.getRequestEMail() + " (" + client.getRequestUser() + ") to " + client.getRequestEMail() + ": " + msg);
            }
            catch (Exception ex) {
                throw new AdempiereException((Throwable)ex);
            }
        }
    }
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        email = email.trim();
        final String regex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.)+[A-Za-z]{2,4}$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
   
    
    private void setEmailBody() {
    	email_body = new StringBuilder();
        email_body.append("<html><body>");
        email_body.append("<p>Respected Vendor</p>");
        email_body.append("<p>Please receive your New Purchase Order (attached) for the material manufacturing. Please note the following(s);</p>");
        email_body.append("<ul>");
        email_body.append("<li>All material should be supplied in One Go / One Delivery</li>");
        email_body.append("<li>Only 1 Invoice will be provided along with material delivery</li>");
        email_body.append("<li>Complete Purchase Order must be closed within 15 Days</li>");
        email_body.append("<li>Supplier Performance Data will now be compiled, Rejections will may affect in logs as passing percentages. Kindly ensure Pre-Dispatched Quality clearance + assurance at your end</li>");
        email_body.append("</ul>");
        email_body.append("<p>Thank You</p>");
        email_body.append("</body></html>");
    }
    private File getAttachment(PO po) {
    	int order_id = po.get_ValueAsInt(po.get_TableName()+"_ID");
    	ProcessInfo pi = new ProcessInfo ("po_print", 1000411, 259, order_id);
    	pi.setExport(true);
		pi.setRecord_ID ( order_id);
		pi.setIsBatch(true);
		pi.setPrintPreview(true);
		pi.setExportFileExtension("pdf");
		ProcessInfoParameter pip = new ProcessInfoParameter("C_Order_ID", order_id,null,null,null);
		pi.setParameter( new ProcessInfoParameter[] {pip} );
		MPInstance mpi = new MPInstance(Env.getCtx(), 0, null);
		mpi.setAD_Process_ID(1000411); 
		mpi.setRecord_ID(order_id);
		mpi.save();
		pi.setAD_PInstance_ID(mpi.get_ID());
		ServerProcessCtl.process(pi, null);
		
		return pi.getExportFile();
    }
}
