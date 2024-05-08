package org.innovation.forms;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.Listbox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.WAttachment;
import org.adempiere.webui.window.WTextEditorDialog;
import org.compiere.model.MAttachment;
import org.compiere.model.MAttachmentEntry;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.MSysConfig;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.au.out.AuEcho;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.impl.XulElement;

public class SOPForm extends ADForm implements ValueChangeListener {
	private static final CLogger log = CLogger.getCLogger(WAttachment.class);
	private MAttachment m_attachment = null;
	private Listbox cbContent = new Listbox();
	private Iframe preview = new Iframe();
	protected AMedia media;
	
	private int mediaVersion = 0;
	private static List<String> autoPreviewList;
	private int maxPreviewSize;
	Label hdrLabel = new Label("Company SOPs");
	private Component customPreviewComponent;
	private Panel previewPanel = new Panel();
	WTableDirEditor editor;
	static {
		autoPreviewList = new ArrayList<String>();
		autoPreviewList.add("image/jpeg");
		autoPreviewList.add("image/png");
		autoPreviewList.add("image/gif");
		autoPreviewList.add("text/plain");
		autoPreviewList.add("application/pdf");
		autoPreviewList.add("text/xml");
		autoPreviewList.add("application/json");
		// autoPreviewList.add("text/html"); IDEMPIERE-3980
	}
	final String hdrLabelCSS = "background-color:#d9e8fa;text-align: center;font-family: 'Brush Script MT', cursive; font-size: 30px; font-weight: bold;display: block;";
	
	
	
	@Override
	protected void initForm() {
		// TODO Auto-generated method stub
		cbContent.addEventListener(Events.ON_SELECT, this);
		
		maxPreviewSize = MSysConfig.getIntValue(MSysConfig.ZK_MAX_ATTACHMENT_PREVIEW_SIZE, 1048576, Env.getAD_Client_ID(Env.getCtx()));
		hdrLabel.setStyle(hdrLabelCSS);
		hdrLabel.setHflex("1");
		editor = getEditor();
		editor.addValueChangeListener(this);
		cbContent.setMold("select");
		Vlayout mainLayout = new Vlayout();
		mainLayout.setVflex("1");
		mainLayout.appendChild(hdrLabel);
		Hlayout row = new Hlayout();
		row.appendChild(editor.getComponent());
		row.appendChild(cbContent);
		mainLayout.appendChild(row);
//		mainLayout.appendChild(cbContent);
//		mainLayout.appendChild(editor.getComponent());
		preview.setHflex("1");
		preview.setVflex("1");
		previewPanel.setVflex("1");
		previewPanel.setHflex("1");
		previewPanel.appendChild(preview);
		mainLayout.appendChild(preview);
		this.appendChild(mainLayout);
		
		
		m_attachment = new MAttachment(Env.getCtx(), 1000004, 0, null);

		int size = m_attachment.getEntryCount();

		for (int i = 0; i < size; i++)
			cbContent.appendItem(m_attachment.getEntryName(i), m_attachment.getEntryName(i));
		
			cbContent.setSelectedIndex(-1);
		
//		displaySelected();

	}

	public void displaySelected() {
		int displayIndex = cbContent.getSelectedIndex(); 
		MAttachmentEntry entry = m_attachment.getEntry(displayIndex);
		if (log.isLoggable(Level.CONFIG)) log.config("Index=" + displayIndex + " - " + entry);
		if (entry != null && entry.getData() != null && autoPreviewList.contains(entry.getContentType()))
		{
			if (log.isLoggable(Level.CONFIG)) log.config(entry.toStringX());

			try
			{
				String contentType = entry.getContentType();
				AMedia media = new AMedia(entry.getName(), null, contentType, entry.getData());

				preview.setContent(media);
				preview.setVisible(true);
				preview.invalidate();
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, "attachment", e);
			}
		}
	}

	// -- ComponentCtrl --//
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}

	/**
	 * A utility class to implement {@link #getExtraCtrl}. It is used only by
	 * component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl implements DynamicMedia {
		// -- DynamicMedia --//
		public Media getMedia(String pathInfo) {
			return media;
		}
	}
	private WTableDirEditor getEditor() {
		
		MLookupInfo info =  MLookupFactory.getLookupInfo (Env.getCtx(), getWindowNo(), 0,
				1002977, 0,
				Env.getLanguage(Env.getCtx()), "HR_Manuals_ID", 0,
				false, "HR_Manuals_ID IN (SELECT HR_Manuals_ID FROM HR_Manuals_Access WHERE ad_role_id = @#AD_Role_ID@)");
		MLookup lookup = new MLookup(info,0);
//		MLookup lookup = MLookupFactory.get(Env.getCtx(), getWindowNo(), 0, 1000084, 0);
		WTableDirEditor editor = new WTableDirEditor("HR_Manuals_ID", false, false,true, lookup, true);
		return editor;
	}
	
	@Override
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		if(event.getTarget()==cbContent && event.getName().equalsIgnoreCase(Events.ON_SELECT)) {
			clearPreview();
			autoPreview (cbContent.getSelectedIndex(), false);
		}
		
		
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		// TODO Auto-generated method stub
		if(editor.getValue() !=null){
			int recordID = (int)editor.getValue();
			loadAttachments(recordID);
		}
	}
	
	private void loadAttachments(int recordID) {
		cbContent.removeAllItems();
		m_attachment = new MAttachment(Env.getCtx(), 1000146, recordID, null);

		int size = m_attachment.getEntryCount();

		for (int i = 0; i < size; i++)
			cbContent.appendItem(m_attachment.getEntryName(i), m_attachment.getEntryName(i));
		cbContent.setSelectedIndex(-1);
		
	}
	private boolean autoPreview(int index, boolean immediate)
	{
		MAttachmentEntry entry = m_attachment.getEntry(index);
		if (entry != null)
		{
			String mimeType = entry.getContentType();
			byte[] data = entry.getData();
			String unit = " KB";
			BigDecimal size = new BigDecimal(data != null ? data.length : 0);
			size = size.divide(new BigDecimal("1024"));
			if (size.compareTo(new BigDecimal("1024")) >= 0)
			{
				size = size.divide(new BigDecimal("1024"));
				unit = " MB";
			}
			size = size.setScale(2, RoundingMode.HALF_EVEN);
			

			if (autoPreviewList.contains(mimeType))
			{
				if (data.length <= maxPreviewSize) {
					displayData(index, immediate);
				} else {
					clearPreview();
					String msg = WTextEditorDialog.sanitize(Msg.getMsg(Env.getCtx(), "FileTooBigForPreview"));
					Media media = new AMedia(null, null, "text/html", msg.getBytes());
					preview.setContent(media);
					preview.setVisible(true);
					
					return false;
				}
				return true;
			}
			else
			{
				clearPreview();
				return false;
			}
		}
		else
		{
			
			return false;
		}
	}
	private void clearPreview()
	{
		preview.setSrc(null);
		preview.setVisible(false);
		if (customPreviewComponent != null)
		{
			customPreviewComponent.detach();
			customPreviewComponent = null;
		}
		
	}
	private void displayData (int index, boolean immediate)
	{
		//	Reset UI
		preview.setSrc(null);

		int displayIndex = index;

		if (immediate)
			displaySelected();
		else
			Clients.response(new AuEcho(this, "displaySelected", null));
//		bPreview.setEnabled(false);
	} 
	private String getExtension(String name) {
		int index = name.lastIndexOf(".");
		if (index > 0) {
			return name.substring(index+1);
		}
		return "";
	}
}

