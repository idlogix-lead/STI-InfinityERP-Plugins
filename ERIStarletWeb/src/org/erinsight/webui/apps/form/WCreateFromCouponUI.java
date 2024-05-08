/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/

package org.erinsight.webui.apps.form;

import java.util.Vector;
import java.util.logging.Level;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.webui.ClientInfo;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.apps.form.WCreateFromWindow;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.event.ActionEvent;
import org.adempiere.webui.event.ActionListener;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.model.GridTab;
import org.compiere.model.MBPartner;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.util.CLogger;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.eri.model.MCoupon;
import org.eri.model.X_ER_LaborActivity;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Vlayout;

public class WCreateFromCouponUI extends CreateFromCoupon implements ActionListener, EventListener<Event>, ValueChangeListener
{

	private WCreateFromWindow window;
	X_ER_LaborActivity mg = null;
	Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	int ER_Coupon_ID = 0;
	int C_BPartner_ID = 0;
	
	public WCreateFromCouponUI(GridTab tab) 
	{
		super(tab);
		log.info(getGridTab().toString());
		window = new WCreateFromWindow(this, getGridTab().getWindowNo());
		p_WindowNo = getGridTab().getWindowNo();
		getGridTab().getRecord_ID();
		
		//mg = new X_ER_LaborActivity(Env.getCtx(), getGridTab().getRecord_ID(), "noname");
		
		try
		{
			if (!dynInit())
				return;
			zkInit();
			setInitOK(true);
			loadTableOIS(data);
			
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
			setInitOK(false);
			throw new AdempiereException(e.getMessage());
		}
		window.setClosable(true);
		AEnv.showWindow(window);
	}
	
	/** Window No               */
	private int p_WindowNo;

	/**	Logger			*/
	private final static CLogger log = CLogger.getCLogger(WCreateFromCouponUI.class);
		
	protected Label upcLabel = new Label();
	protected WStringEditor upcField = new WStringEditor();
	
	protected Label bPartnerLabel = new Label();
	protected WEditor bPartnerField;
	
	
	private Grid parameterStdLayout;

	private int noOfParameterColumn;
    
	/**
	 *  Dynamic Init
	 *  @throws Exception if Lookups cannot be initialized
	 *  @return true if initialized
	 */
	public boolean dynInit() throws Exception
	{
		log.config("");
		super.dynInit();
		window.setTitle(getTitle());
		upcField = new WStringEditor ("ER_Coupon_ID", false, false, true, 10, 30, null, null);
//		upcField.getComponent().addEventListener(Events.ON_CHANGE, this);
		upcField.getComponent().addEventListener(Events.ON_OK, this);
//		bPartnerField.setReadWrite(false);
//		bPartnerField.addValueChangeListener(this);
		initBPartner(false);
		
		return true;

	}
	
	protected void initBPartner (boolean forInvoice) throws Exception
	{
		int AD_Column_ID = 3499;        //  C_Invoice.C_BPartner_ID
		MLookup lookup = MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, AD_Column_ID, DisplayType.Search);
		bPartnerField = new WSearchEditor ("C_BPartner_ID", true, false, true, lookup);
		int C_BPartner_ID = Env.getContextAsInt(Env.getCtx(), p_WindowNo, "C_BPartner_ID");
		bPartnerField.setValue(Integer.valueOf(C_BPartner_ID));
	}   //  initBPartner

	protected void zkInit() throws Exception
	{

    	bPartnerLabel.setText(Msg.getElement(Env.getCtx(), "C_BPartner_ID"));

		upcLabel.setText(Msg.getElement(Env.getCtx(), "ER_Coupon_ID", false));
		Vlayout vlayout = new Vlayout();
		ZKUpdateUtil.setVflex(vlayout, "min");
		ZKUpdateUtil.setWidth(vlayout, "100%");
    	Panel parameterPanel = window.getParameterPanel();
		parameterPanel.appendChild(vlayout);
		
		parameterStdLayout = GridFactory.newGridLayout();
    	vlayout.appendChild(parameterStdLayout);
    	ZKUpdateUtil.setVflex(vlayout, "parameterStdLayout");
    	
    	setupColumns(parameterStdLayout);
		
		Rows rows = (Rows) parameterStdLayout.newRows();
		Row row = rows.newRow();
//		row = rows.newRow();

		
		row.appendChild(bPartnerLabel.rightAlign());
		if (bPartnerField != null) {
			row.appendChild(bPartnerField.getComponent());
			bPartnerField.fillHorizontal();
		}
		
		row.appendChild(upcLabel.rightAlign());
		row.appendChild(upcField.getComponent());
		ZKUpdateUtil.setHflex(upcField.getComponent(), "1");

		if (ClientInfo.isMobile()) {    		
    		if (noOfParameterColumn == 2)
				LayoutUtils.compactTo(parameterStdLayout, 2);		
			ClientInfo.onClientInfo(window, this::onClientInfo);
		}
		
	}

	private boolean 	m_actionActive = false;
	
	/**
	 *  Action Listener
	 *  @param e event
	 * @throws Exception 
	 */
	public void onEvent(Event e) throws Exception
	{
		if (m_actionActive)
			return;
		m_actionActive = true;
		
		if (e.getTarget().equals(upcField.getComponent())){
			if (upcField.getDisplay().length() > 0 )
			{
				if (ER_Coupon_ID == Integer.parseInt(upcField.getDisplay()))
				{
					m_actionActive = false;
					return;
				}

				ER_Coupon_ID = Integer.parseInt(upcField.getDisplay());
				
				C_BPartner_ID = Integer.parseInt(bPartnerField.getValue().toString());
				MBPartner bp = new MBPartner(Env.getCtx(), C_BPartner_ID, "noname");
				upcField.setValue("");
				boolean found = false;
				
				MCoupon coup = new MCoupon(Env.getCtx(), ER_Coupon_ID, "noname");
				if (coup.get_ID() > 0)
				{
					
					for (Vector<Object> vector : data) {
						if (((KeyNamePair) vector.get(1)).getKey() == ER_Coupon_ID)
						{
							found=true;
							break;
						}
					}
					
					if (!found)
					{
					
						Vector<Object> line = new Vector<Object>();
						line.add(Boolean.FALSE); 
						KeyNamePair pp = new KeyNamePair(ER_Coupon_ID, coup.getDescription());			// Product
						line.add(pp);                           //  Coupon

						KeyNamePair op = new KeyNamePair(coup.getER_Operation_ID(), coup.getER_Operation().getName());			// Product
						line.add(op);                           //  Operation

						KeyNamePair dp = new KeyNamePair(coup.getM_Production_ID(), coup.getM_Production().getM_Product().getName());			// Product
						line.add(dp);                           //  Operation
						
						line.add(coup.getQty());
						line.add(coup.getQty());
						
						KeyNamePair partner = new KeyNamePair(bp.get_ID(), bp.getName());			// Product
						line.add(partner);
						
						data.add(line);
						
						window.getWListbox().getModel().removeTableModelListener(window);
						ListModelTable model = new ListModelTable(data);
						model.addTableModelListener(window);
						window.getWListbox().setData(model, getOISColumnNames());
						
					}
					
					
				}
				

//				configureMiniTable(window.getWListbox());
				
////	    	int M_Product_ID = bProductField.getValue() == null?0:((Integer)bProductField.getValue()).intValue();
//				
//				loadTableOIS(getStockData(1));
//				window.getWListbox().setColumnClass(0, boolean.class, false);
		//
//				ListModelTable model = (ListModelTable) window.getWListbox().getModel();
//				model.updateComponent(1);
//				
				
//				loadTableOIS(getStockDataByLPN(Integer.parseInt(lpn)));
//				window.getWListbox().setColumnClass(0, boolean.class, false);

//				ListModelTable model = (ListModelTable) window.getWListbox().getModel();
//				model.updateComponent(1);
				
//				List<MStorageOnHand> strg = MStorageOnHand.getAll(Env.getCtx(), M_Product_ID, M_Locator_ID, trxName)
//				for (MProduct product : products)
//				{
//					int row = findProductRow(product.get_ID());
//					if (row >= 0)
//					{
//						BigDecimal qty = (BigDecimal)model.getValueAt(row, 1);
//						model.setValueAt(qty, row, 1);
//						model.setValueAt(Boolean.TRUE, row, 0);
//						model.updateComponent(row, row);
//					}
//				}
				
				
			}
		}
				
		m_actionActive = false;
	}
	
	
	protected void loadTableOIS (Vector<?> data)
	{
		window.getWListbox().clear();
		window.getWListbox().getModel().removeTableModelListener(window);
		ListModelTable model = new ListModelTable(data);
		model.addTableModelListener(window);
		window.getWListbox().setData(model, getOISColumnNames());
		configureMiniTable(window.getWListbox());
	}   //  loadOrder
	
	
	/**
	 *  Load Order/Invoice/Shipment data into Table
	 *  @param data data
	 */
	
	
	public void showWindow()
	{
		window.setVisible(true);
	}
	
	public void closeWindow()
	{
		window.dispose();
	}

	@Override
	public Object getWindow() {
		return window;
	}
	
	protected void setupColumns(Grid parameterGrid) {
		noOfParameterColumn = ClientInfo.maxWidth((ClientInfo.EXTRA_SMALL_WIDTH+ClientInfo.SMALL_WIDTH)/2) ? 2 : 4;
		Columns columns = new Columns();
		parameterGrid.appendChild(columns);
		
		if (ClientInfo.maxWidth((ClientInfo.EXTRA_SMALL_WIDTH+ClientInfo.SMALL_WIDTH)/2))
		{
			Column column = new Column();
			ZKUpdateUtil.setWidth(column, "35%");
			columns.appendChild(column);
			column = new Column();
			ZKUpdateUtil.setWidth(column, "65%");
			columns.appendChild(column);
		}
		else
		{
			Column column = new Column();
			columns.appendChild(column);		
			column = new Column();
			ZKUpdateUtil.setWidth(column, "10%");
			columns.appendChild(column);
			ZKUpdateUtil.setWidth(column, "24%");

			column = new Column();
			ZKUpdateUtil.setWidth(column, "10%");
			columns.appendChild(column);
			
			column = new Column();
			ZKUpdateUtil.setWidth(column, "23%");
			columns.appendChild(column);
		
			column = new Column();
			ZKUpdateUtil.setWidth(column, "10%");
			columns.appendChild(column);
			
			column = new Column();
			ZKUpdateUtil.setWidth(column, "23%");
			columns.appendChild(column);

		}
	}
	
	protected void onClientInfo()
	{
		if (ClientInfo.isMobile() && parameterStdLayout != null && parameterStdLayout.getRows() != null)
		{
			int nc = ClientInfo.maxWidth((ClientInfo.EXTRA_SMALL_WIDTH+ClientInfo.SMALL_WIDTH)/2) ? 2 : 4;
			int cc = noOfParameterColumn;
			if (nc == cc)
				return;
			
			parameterStdLayout.getColumns().detach();
			setupColumns(parameterStdLayout);
			if (cc > nc)
			{
				LayoutUtils.compactTo(parameterStdLayout, nc);
			}
			else
			{
				LayoutUtils.expandTo(parameterStdLayout, nc, false);
			}
			
			ZKUpdateUtil.setCSSHeight(window);
			ZKUpdateUtil.setCSSWidth(window);
			window.invalidate();			
		}
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

//	public void actionPerformed(ActionEvent event) {
//
////    	int M_Product_ID = bProductField.getValue() == null?0:((Integer)bProductField.getValue()).intValue();
//		
//		loadTableOIS(getStockData(1));
//		window.getWListbox().setColumnClass(0, boolean.class, false);
//
//		ListModelTable model = (ListModelTable) window.getWListbox().getModel();
//		model.updateComponent(1);
//		
//	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		
	}

}
