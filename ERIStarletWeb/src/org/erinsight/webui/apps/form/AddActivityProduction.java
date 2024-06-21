package org.erinsight.webui.apps.form;



import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.util.ProcessUtil;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListCell;
import org.adempiere.webui.component.ListHead;
import org.adempiere.webui.component.ListHeader;
import org.adempiere.webui.component.ListItem;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Messagebox;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.panel.ADForm;
import org.compiere.model.MPInstance;
import org.compiere.model.MProcess;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfo;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.eri.model.MCuttingHdr;
import org.eri.model.MDailyProduction;
import org.eri.model.MLastingHdr;
import org.eri.model.MPackingHdr;
import org.eri.model.MStitchingHdr;
import org.eri.model.MStrobelHdr;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;

public class AddActivityProduction extends ADForm  {

	Label hdrLabel = new Label("Add Production");
	Listbox dataTable ;
	Messagebox mb = new Messagebox();
	final String hdrLabelCSS = "background-color:#d9e8fa;text-align: center; font-size: 30px; font-weight: bold;display: block;";
	Radiogroup activityChoice ;
	Map<String,String> viewMap = new HashMap<>();
	Map<String,String> tableMap = new HashMap<>();
	Map<String,Integer> priorityMap = new HashMap<>();
	Textbox ticketID = new Textbox();
	Listbox ticketsList;
	
	@Override
	protected void initForm() {
		// TODO Auto-generated method stub
		
		// Initialize header elements
		hdrLabel.setStyle(hdrLabelCSS);
		activityChoice = getRadioGroup();
		ticketID.setPlaceholder("Scan Ticket#");
		ticketID.addEventListener(Events.ON_OK, this);
		Hlayout inputElements = new Hlayout();
		inputElements.appendChild(ticketID);
		inputElements.appendChild(activityChoice);
		inputElements.setStyle("display: flex; align-items: center; margin-top: 10px;margin-bottom: 10px;");
		
		
		ticketsList = getTicketsListBox( );
		dataTable = getDetailListBox();
		activityChoice.setStyle("align-self: center;");
		activityChoice.addEventListener(Events.ON_CHECK, this);
		
		
		
		// Initialize Data Elements
		Hlayout mainPane = new Hlayout();
		mainPane.appendChild(ticketsList);
		mainPane.appendChild(dataTable);
		mainPane.setHflex("1");
		ticketsList.setHflex("4");
		dataTable.setHflex("6");
		
		// Append all elements in Form
		this.appendChild(hdrLabel);
		this.appendChild(inputElements);
		this.appendChild(mainPane);
		
		viewMap.put("Cutting", "ER_Cutting_Balance_V");
		viewMap.put("Stitching", "ER_Stitching_Balance_V");
		viewMap.put("Strobel", "ER_Strobel_Balance_V");
		viewMap.put("Lasting", "ER_Lasting_Balance_V");
		viewMap.put("Packing", "ER_Packing_Balance_V");
		tableMap.put("Cutting", "ER_Cutting");
		tableMap.put("Stitching", "ER_Stitching");
		tableMap.put("Strobel", "ER_Strobel");
		tableMap.put("Lasting", "ER_Lasting");
		tableMap.put("Packing", "ER_Packing");
		
		priorityMap.put("Cutting", 1);
		priorityMap.put("Stitching", 2);
		priorityMap.put("Strobel", 3);
		priorityMap.put("Lasting", 4);
		priorityMap.put("Packing", 5);
	}

	
	
	Listbox getTicketsListBox() {
		Listbox box= new Listbox();
		ListHead listhead = new ListHead();
		ListHeader idHdr =new ListHeader("Ticket#");
		idHdr.setHflex("3");
		ListHeader descHdr =new ListHeader("Description");
		descHdr.setHflex("7");
		listhead.appendChild(idHdr);
		listhead.appendChild(descHdr);
        box.appendChild(listhead);
        box.addEventListener(Events.ON_SELECT, this);
		box.setCheckmark(true);
		return box;
	}
	Listbox getDetailListBox() {
		Listbox box= new Listbox();
		ListHead listhead = new ListHead();
		ListHeader codeHdr =new ListHeader("Product Code");
		codeHdr.setHflex("2");
		ListHeader nameHdr =new ListHeader("Product Name");
		nameHdr.setHflex("7");
		ListHeader qtyHdr =new ListHeader("Qty");
		qtyHdr.setHflex("1");
		listhead.appendChild(codeHdr);
		listhead.appendChild(nameHdr);
		listhead.appendChild(qtyHdr);
		box.appendChild(listhead);
		box.setStyle("border: 1px solid #c4c4c4;border-radius: 5px;");
		return box;
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		String intRegex = "^\\d+$";
		if(event.getTarget()==ticketID && event.getName().equals(Events.ON_OK)) {
			String value = ticketID.getRawValue().toString();
			if(value==null)
				return;
			boolean isValidValue = value.matches(intRegex);
			if(!isValidValue) {
				showMessage("Please Enter Valid Ticket Number!");
				return;
			}
				int dpID = Integer.parseInt(value);
				String activity = activityChoice.getSelectedItem().getLabel();
				if(isValidTicket(activity,dpID)) {
					ticketID.setRawValue("");
					ticketID.setFocus(true);
					System.out.println("Valid Ticket");
					addProduction(dpID);
				
				}
				else {
					if(isTicketExist(dpID)) {
						String meesage = getLatestProductionMessage(dpID);
						if(meesage.length()>0) {
							showMessage(meesage);
						}
						else {
							showMessage("Production might already added aginst this ticket");
						}
						
						ticketID.setRawValue("");
						ticketID.setFocus(true);
					}
					else {
						showMessage("This Ticket# is not valid");
						ticketID.setRawValue("");
						ticketID.setFocus(true);
					}
					
				}
			
			
		}
		if(event.getTarget()==ticketsList && event.getName().equals(Events.ON_SELECT)) {
			int dpID = Integer.parseInt(ticketsList.getSelectedItem().getLabel());
			showData(false,dpID);
		}
		if(event.getTarget().getParent() ==activityChoice && event.getName().equals(Events.ON_CHECK)) {
			if( ticketsList.getItems().size()>0 && ticketsList.getSelectedItem()!=null) {
				int dpID = Integer.parseInt(ticketsList.getSelectedItem().getLabel());
				showData(false, dpID);
				
			}
			ticketID.setFocus(true);
		}
		super.onEvent(event);
	}

	void showMessage(String msg){
		mb.showDialog(msg,"Message/Warning/Error", 1, Messagebox.ERROR);
	}
	
	boolean isValidTicket(String act,int id) {
		
		String view = viewMap.get(act);
		int answer =0;
		String sql = "select exists\n"
				+ "(select er_dailyproduction_id \n"
				+ "from ER_DailyProduction where ER_DailyProduction.DocStatus = 'IP' and er_dailyproduction_id = "+id+" \n"
				+ "AND ER_DailyProduction.ER_DailyProduction_ID IN (SELECT ER_DailyProduction_ID FROM "+ view +" WHERE balanceqty> 0 ))::integer";
		if(view!=null) {
			answer = DB.getSQLValue(view, sql);
		}
		return answer==1;
	}
		Radiogroup getRadioGroup() {
		Radiogroup rg = new Radiogroup();
		Radio cuttingRadio =new Radio("Cutting");
		Radio stitchingRadio =new Radio("Stitching");
		Radio strobelRadio =new Radio("Strobel");
		Radio lastingRadio =new Radio("Lasting");
		Radio packingRadio =new Radio("Packing");
		cuttingRadio.setParent(rg);
		stitchingRadio.setParent(rg);
		strobelRadio.setParent(rg);
		lastingRadio.setParent(rg);
		packingRadio.setParent(rg);
		rg.setSelectedItem(cuttingRadio);
		return rg;
	}
	
	boolean isTicketExist(int ticketID) {
		List<MDailyProduction> tickets = new Query(Env.getCtx(),MDailyProduction.Table_Name, " ER_DailyProduction_ID = ?", null).setParameters(ticketID).list();
		return tickets.size()>0;
	}
	
	String getLatestProductionMessage(int ticketID) {
		String message="";	
		String choice = activityChoice.getSelectedItem().getLabel();
		List<MCuttingHdr> ctickets = new Query(Env.getCtx(),MCuttingHdr.Table_Name, " ER_DailyProduction_ID = ?", null).setParameters(ticketID).list();
		if(ctickets.size()==0 && priorityMap.get(choice)> priorityMap.get("Cutting") )
			message+="Cutting, ";
		List<MStitchingHdr> stickets = new Query(Env.getCtx(),MStitchingHdr.Table_Name, " ER_DailyProduction_ID = ?", null).setParameters(ticketID).list();
		if(stickets.size()==0 && priorityMap.get(choice)> priorityMap.get("Stitching"))
			message+="Stitching, ";
		List<MStrobelHdr> sttickets = new Query(Env.getCtx(),MStrobelHdr.Table_Name, " ER_DailyProduction_ID = ?", null).setParameters(ticketID).list();
		if(sttickets.size()==0 && priorityMap.get(choice)> priorityMap.get("Strobel"))
			message+="Strobel, ";
		List<MLastingHdr> ltickets = new Query(Env.getCtx(),MLastingHdr.Table_Name, " ER_DailyProduction_ID = ?", null).setParameters(ticketID).list();
		if(ltickets.size()==0 && priorityMap.get(choice)> priorityMap.get("Lasting"))
			message+="Lasting, ";
		List<MPackingHdr> ptickets = new Query(Env.getCtx(),MPackingHdr.Table_Name, " ER_DailyProduction_ID = ?", null).setParameters(ticketID).list();
		if(ptickets.size()==0 && priorityMap.get(choice)> priorityMap.get("Packing"))
			message+="Packing ";
		if(message.length()!=0)
			message = message +" still Pending";
		return message;
	}
	
	void addProduction(int dpID) {
		
		String activity = activityChoice.getSelectedItem().getLabel();
		int recordID = 0;
		if(activity.equalsIgnoreCase("cutting")) {
			MCuttingHdr hdr = new MCuttingHdr(Env.getCtx(), 0, null);
			hdr.setER_DailyProduction_ID(dpID);
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			hdr.setMovementDate(currentTime);
			hdr.setProcessed(true);
			hdr.save();
			recordID = hdr.get_ID();
		}
		else if(activity.equalsIgnoreCase("stitching"))
		{
			MStitchingHdr hdr = new MStitchingHdr(Env.getCtx(), 0, null);
			hdr.setER_DailyProduction_ID(dpID);
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			hdr.setMovementDate(currentTime);
			hdr.setProcessed(true);
			hdr.save();
			recordID = hdr.get_ID();
		}
		else if(activity.equalsIgnoreCase("strobel")) {
			MStrobelHdr hdr = new MStrobelHdr(Env.getCtx(), 0, null);
			hdr.setER_DailyProduction_ID(dpID);
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			hdr.setMovementDate(currentTime);
			hdr.setProcessed(true);
			hdr.save();
			recordID = hdr.get_ID();
		}
		else if(activity.equalsIgnoreCase("lasting")) {
			MLastingHdr hdr = new MLastingHdr(Env.getCtx(), 0, null);
			hdr.setER_DailyProduction_ID(dpID);
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			hdr.setMovementDate(currentTime);
			hdr.setProcessed(true);
			hdr.save();
			recordID = hdr.get_ID();
		}
		else if(activity.equalsIgnoreCase("packing")) {
			MPackingHdr hdr = new MPackingHdr(Env.getCtx(), 0, null);
			hdr.setER_DailyProduction_ID(dpID);
			Timestamp currentTime = new Timestamp(System.currentTimeMillis());
			hdr.setMovementDate(currentTime);
			hdr.setProcessed(true);
			hdr.save();
			recordID = hdr.get_ID();
		}
		
		Timestamp currentDate = new Timestamp(System.currentTimeMillis());
		ProcessInfoParameter pi1 = new ProcessInfoParameter("ER_CuttingHdr_ID",recordID,"","","");
		ProcessInfoParameter pi2 = new ProcessInfoParameter("ER_StitchingHdr_ID",recordID,"","","");
		ProcessInfoParameter pi3 = new ProcessInfoParameter("ER_StrobelHdr_ID",recordID,"","","");
		ProcessInfoParameter pi4 = new ProcessInfoParameter("ER_LastingHdr_ID",recordID,"","","");
		ProcessInfoParameter pi5 = new ProcessInfoParameter("ER_PackingHdr_ID",recordID,"","","");
		ProcessInfoParameter pi6 = new ProcessInfoParameter("ER_DailyProduction_ID", dpID,"","","");
		ProcessInfoParameter pi7 = new ProcessInfoParameter("MovementDate", currentDate,"","","");
		ProcessInfo pi = new ProcessInfo("", 53226,0,0);
		pi.setParameter(new ProcessInfoParameter[] {pi1,pi2,pi3,pi4,pi5,pi6,pi7});
		MProcess pr = new Query(Env.getCtx(), MProcess.Table_Name, "value=?", null)
		                        .setParameters(new Object[]{activity+"_AddProduction"})
		                        .first();
		if (pr==null) 
		{
	      return ;
		}
		MPInstance mpi = new MPInstance(Env.getCtx(), 0, null);
		mpi.setAD_Process_ID(pr.get_ID());
		mpi.setRecord_ID(recordID);
		mpi.save();
		pi.setAD_PInstance_ID(mpi.get_ID());
		pi.setRecord_ID(recordID);
		pi.setClassName(pr.getClassname());
		ProcessUtil.startJavaProcess(Env.getCtx(), pi, null);
		addToTicketList(dpID);
		showData(true,recordID);
	}
	
	void showData(boolean isHdr,int recordID) {
		dataTable.removeAllItems();
		
		BigDecimal total = Env.ZERO;
        
		String tableName =tableMap.get(activityChoice.getSelectedItem().getLabel());
		String sql = "select p.value,p.name ,c.qty\n"
				+ "from "+tableName+" c\n"
				+ "join m_product p ON c.m_product_id = p.m_product_id\n";
		if(isHdr) {
				sql = sql+ "where "+tableName+"hdr_id = "+recordID;
		}
		else {
			sql = sql+ "where "+tableName+"hdr_id IN (select "+tableName+"hdr_id from "+tableName+"hdr where er_dailyproduction_id = "+recordID+") ";
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			
			pstmt = DB.prepareStatement (sql.toString(), null);
			rs = pstmt.executeQuery ();
			
			while (rs.next ())		
			{
				String prodCode = rs.getString("value");
				String prodName = rs.getString("name");
				BigDecimal qty = rs.getBigDecimal("qty");
				total = total.add(qty);
				ListItem item = new ListItem();
				dataTable.appendChild(item);
				ListCell cell = new ListCell(prodCode);
				item.appendChild(cell);
				cell = new ListCell(prodName);
				item.appendChild(cell);
				cell = new ListCell(qty.toString());
				item.appendChild(cell);
				
			}
		}
		catch (Exception e)
		{
			throw new AdempiereException(e);
		}
		ListItem item = new ListItem();
		dataTable.appendChild(item);
		ListCell cell = new ListCell("");
		cell.setStyle("background-color:#d9e8fa;");
		item.appendChild(cell);
		cell = new ListCell("Total");
		cell.setStyle("background-color:#d9e8fa;font-weight: bold;text-align: right;");
		item.appendChild(cell);
		cell = new ListCell(total.toString());
		cell.setStyle("background-color:#d9e8fa;font-weight: bold;");
		item.appendChild(cell);
		if(isHdr);
//		showMessage("Successfully added "+activityChoice.getSelectedItem().getLabel())	;
	}
	
	void addToTicketList(int id) {
		
		
		for(Listitem item : ticketsList.getItems()) {
			int dpID = Integer.parseInt(item.getLabel());
			if(id==dpID) {
				ticketsList.setSelectedItem(item);
				return;
			}
		}
		
		String sql = "select 'Order# '||od.poreference||' - Cutomer :'||bp.name description \n"
				+ "from er_dailyproduction dp\n"
				+ "join c_order od ON dp.c_order_id =od.c_order_id\n"
				+ "join c_bpartner bp ON od.c_bpartner_id = bp.c_bpartner_id\n"
				+ "where er_dailyproduction_id = "+id;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			
			pstmt = DB.prepareStatement (sql.toString(), null);
			rs = pstmt.executeQuery ();
			ListItem item = new ListItem();
			while (rs.next ())		
			{
				String description  = rs.getString("description");
				
				
				ticketsList.appendChild(item);
				ListCell cell = new ListCell(String.valueOf(id));
				item.appendChild(cell);
				cell = new ListCell(description);
				item.appendChild(cell);
				
				
			}
			if(ticketsList!=null) {
				ticketsList.setSelectedItem(item);
			}
		}
		catch (Exception e)
		{
			throw new AdempiereException(e);
		}
		
	}
	
	
	
}
