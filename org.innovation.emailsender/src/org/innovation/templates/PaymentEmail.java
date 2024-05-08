package org.innovation.templates;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MClient;
import org.compiere.model.MPayment;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.compiere.util.EMail;
import org.compiere.util.Env;

public class PaymentEmail {
	MPayment payment;
    BigDecimal payAmt;
    StringBuilder email_body;
    protected transient CLogger log;
    public PaymentEmail() {
    	this.log = CLogger.getCLogger((Class)this.getClass());
    }
    public void sendEmail(PO obj) {
    	
    	final PO po = obj;
        this.getPaymentInfo(po);
        final int bPartnerID = po.get_ValueAsInt("C_BPartner_ID");
        final int clientID = po.get_ValueAsInt("AD_Client_ID");
        final MBPartner bp = new MBPartner(Env.getCtx(), bPartnerID, (String)null);
        final MBPartnerLocation bpl = new MBPartnerLocation(Env.getCtx(), bp.getPrimaryC_BPartner_Location_ID(), (String)null);
        String email_add = bpl.get_ValueAsString("Email_ID");
        if (this.isValidEmail(email_add)) {
            email_add = email_add.trim();
            this.setEmailBody();
            final MClient client = new MClient(Env.getCtx(), clientID, (String)null);
            final EMail email = new EMail(client, client.getRequestEMail(), email_add, "Payment Acknowledgement!", this.email_body.toString(), true);
            email.createAuthenticator(client.getRequestUser(), client.getRequestUserPW());
            if (email == null) {
                new StringBuilder("Could not create EMail: ");
                return;
            }
            try {
                email.addCc("erp@groupstarlet.com");
                email.addCc("mgracct@groupstarlet.com");
                email.addCc("mgr.procurement@groupstarlet.com");
                email.addCc("momen@groupstarlet.com");
                email.addCc("momen@groupstarlet.com");
                email.addCc("cfo@groupstarlet.com");
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
    
    private void getPaymentInfo(final PO po) {
        final int paymentID = po.get_ValueAsInt("C_Payment_ID");
        this.payment = new MPayment(Env.getCtx(), paymentID, (String)null);
        this.payAmt = this.payment.getPayAmt();
    }
    
    private void setEmailBody() {
        (this.email_body = new StringBuilder()).append("<html>");
        this.email_body.append("<body>");
        this.email_body.append("<p>Dear Valued Supplier:</p>");
        this.email_body.append("<p>We are pleased to inform you that our company paid via ");
        if (this.payment.getC_DocType_ID() == 1000009) {
            this.email_body.append("<b>").append(this.payment.getC_BankAccount().getName()).append("</b> ");
        }
        else {
            this.email_body.append("<b>").append(" Cash ").append("<b>");
        }
        this.email_body.append(" of ");
        this.email_body.append(this.payment.getCurrencyISO()).append(" <b>").append(this.payment.getPayAmt()).append("</b>");
        this.email_body.append(", on account of your supplies / services. Kindly acknowledge the receipt and update your statement of account.</p>");
        this.email_body.append("<p>Finance Department (Starlet Innovation Pvt Ltd)</p>");
        this.email_body.append("</body>");
        this.email_body.append("</html>");
    }
}
