package org.eri.processes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.PO;
import org.compiere.util.DB;
import org.compiere.util.Env;

import org.eri.model.X_IDL_SMS;
import org.eri.model.X_IDL_SMS_Config;

public class SmsSender {	
	String phoneNo;
	String transactionId;
	String userName;
	String password;
	String senderId;
	String messageText;
	String errorMessage;
	String smsTemplate;
	int document_id;
	PO object;	
	public SmsSender(PO object) {
		this.object = object;
		phoneNo="";
		transactionId="";
		password="";
		senderId="";
		messageText="";
		errorMessage="";
		smsTemplate="";
		document_id=0;;
	}
	public void sendSMS() {		
		BigDecimal amount = Env.ZERO;
		document_id = (int)object.get_Value(object.get_TableName()+"_ID");		
		if(object.get_TableName().equalsIgnoreCase("C_Payment"))
			amount = (BigDecimal)object.get_Value("PayAmt");
		X_IDL_SMS_Config smsConfig = getSmsConfig();
		userName =smsConfig.getUserName();
		password = smsConfig.getPassword();
		senderId = smsConfig.getsenderid();
		setPhoneAndTemplate(object.get_ValueAsInt("C_BPartner_ID"));
		String stamp = new Timestamp(System.currentTimeMillis()).toString();
		stamp = stamp.replaceAll(" ","");
		stamp = stamp.replaceAll(":","-");
        stamp = stamp.replaceAll("\\.","-");
		transactionId = Integer.toString(document_id)+stamp ;
		smsTemplate = smsTemplate.replaceAll("%", amount.toString());
		smsTemplate = smsTemplate.replaceAll(" ", "%20");		
		generate_GET_Request();			
	}
	public void saveSMS(String tableName,String phoneNo,int document_id,String text,String response,String httpCode) {
		String responseCode = httpCode;
		String errorMessage = response;
		try {
			responseCode = Integer.toString(Integer.parseInt(response.substring(8, 11).replaceAll(" ","").trim()));
			errorMessage = getResponseMessage(response.substring(8, 11));
		}
		catch(Exception e) {
			
		}
		
		X_IDL_SMS sms = new X_IDL_SMS(Env.getCtx(), 0, null);
		
		sms.setreciever_no(phoneNo);
		sms.setsmsbody(text);
		sms.setdocumentname( tableName);
		sms.setresponsecode( String.format("%03d", Integer.parseInt(responseCode)));
		sms.seterrormessage(errorMessage);
		sms.setC_Payment_ID(document_id);
		if("013".equals(String.format("%03d", Integer.parseInt(responseCode))))
			sms.setissent(true);
			
		sms.save();
	}
	public X_IDL_SMS_Config getSmsConfig() {
		X_IDL_SMS_Config config = null;
		String strSQL = "select * \n"
				+ " from adempiere.idl_sms_config bpl\n";
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		try
		{
			pstmt = DB.prepareStatement (strSQL.toString(), null);
			rs = pstmt.executeQuery ();
			
			while (rs.next ())	
			{
				config = new X_IDL_SMS_Config(Env.getCtx(), rs, null);
			}
		}
		catch (Exception e)
		{
			throw new AdempiereException(e);
		}
				return config;
		}
	public void setPhoneAndTemplate(int bpId) {
		Pattern mobNO;
		Matcher matcher;
		String strSQL = "select coalesce(phone,phone2) phone_no, smstemplate \n"
						+ " from adempiere.c_bpartner_location bpl\n"
						+ " where c_bpartner_id = "+ bpId;
		PreparedStatement pstmt = null;
		ResultSet rs = null;		
		try
		{
			pstmt = DB.prepareStatement (strSQL.toString(), null);
			rs = pstmt.executeQuery ();
			
			while (rs.next ())	
			{
				phoneNo = rs.getString("phone_no");
				smsTemplate = rs.getString("smstemplate");
			}
		}
		catch (Exception e)
		{
			throw new AdempiereException(e);
		}
		phoneNo = phoneNo==null?"":phoneNo;
		if(phoneNo.length()!=0) {
			 phoneNo = phoneNo.replaceAll("-", "");
			 mobNO = Pattern.compile("^((\\+92)?(0092)?(92)?(0)?)(3)([0-9]{9})$");
		     matcher = mobNO.matcher(phoneNo);
		    if(!matcher.find()) {
		    	phoneNo=  "";
		    	return ;
		    }
		    else {		    	
		    	if(phoneNo.charAt(0)=='+')
		    		phoneNo = phoneNo.substring(1, phoneNo.length());
		    	else if (phoneNo.charAt(0)=='0')
		    		phoneNo ="92"+phoneNo.substring(1, phoneNo.length());
		    		return;
		    }
		 
		}	
		phoneNo =  "";
	}
	public String getResponseMessage(String response) {
		String responseCode="";
		try {
			responseCode = Integer.toString(Integer.parseInt(response));
			responseCode = String.format("%03d", Integer.parseInt(responseCode));
		}
		catch(Exception e) {
			return response;
		}
		HashMap<String, String> hm = new HashMap();
		hm.put("000", "No connection");
		hm.put("001", "Authorization failed – user/password");
		hm.put("002", "Type of the message missing");
		hm.put("003", "Recipient number missing");
		hm.put("004", "Text of the message missing");
		hm.put("005", "Sender id missing");
		hm.put("006", "Operator id missing");
		hm.put("007", "Transaction id missing");
		hm.put("107", "Transaction ID not greater than 36 characters");
		hm.put("008", "Transaction id duplicate");
		hm.put("009", "Delivery report is missing");
		hm.put("102", "Wrong type of message");
		hm.put("103", "Wrong type of recipient number");
		hm.put("105", "Wrong type sender id");
		hm.put("106", "Wrong type operator id");
		hm.put("010", "Delivery success");
		hm.put("011", "Delivery failure");
		hm.put("012", "Message buffered");
		hm.put("013", "Message sent to SMSC");
		hm.put("014", "Message reject from SMSC");
		hm.put("015", "Message Queued for later delivery");
		hm.put("016", "Message expired");
		hm.put("017", "Route error");
		hm.put("018", "System error");
		return hm.getOrDefault(responseCode, response);
	}
	public void generate_GET_Request() {
	try {
			String urlString = "http://api.itelservices.com/send.php?transaction_id="+transactionId+"&user="+userName+"&pass="+password+"&number="+phoneNo+"&text="+smsTemplate+"&from="+senderId+"&type=sms";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200) {
            	errorMessage = "Failed : HTTP Error code : "+ conn.getResponseCode();   	
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null) {
            	errorMessage+=output;
                System.out.println(output);
            }
            conn.disconnect();

        } catch (Exception e) {
        	errorMessage = "Exception thrown " + errorMessage;
        }		
		saveSMS(object.get_TableName(),phoneNo,document_id,smsTemplate.replaceAll("%20", " "),errorMessage,"");
	}
}

