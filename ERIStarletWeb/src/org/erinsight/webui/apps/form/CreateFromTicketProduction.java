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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.compiere.apps.IStatusBar;
import org.compiere.grid.CreateFrom;
import org.compiere.minigrid.IMiniTable;
import org.compiere.model.GridTab;
import org.compiere.model.MLocator;
import org.compiere.model.MWarehouse;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.KeyNamePair;
import org.compiere.util.Msg;
import org.eri.model.MLaborActivity;
import org.eri.model.MLaborActivityLine;

/**
 *  Create Invoice Transactions from PO Orders or Receipt
 *
 *  @author Jorg Janke
 *  @version  $Id: VCreateFromShipment.java,v 1.4 2006/07/30 00:51:28 jjanke Exp $
 * 
 * @author Teo Sarca, SC ARHIPAC SERVICE SRL
 * 			<li>BF [ 1896947 ] Generate invoice from Order error
 * 			<li>BF [ 2007837 ] VCreateFrom.save() should run in trx
 */
public abstract class CreateFromTicketProduction extends CreateFrom 
{
	private int defaultLocator_ID=0;
	
	int SiteID = 0;
	int SiteToID=0;
	int LocatorTypeID = 0;
	int LocatorTypeToID=0;
	int LocatorToID=0;
	
	
	private String tolocator="";
	private String toSite="";

	/**
	 *  Protected Constructor
	 *  @param mTab MTab
	 */
	public CreateFromTicketProduction(GridTab mTab)
	{
		super(mTab);
		if (log.isLoggable(Level.INFO)) log.info(mTab.toString());
	}   //  VCreateFromShipment

	
	public void setToLocator(String str)
	{
		tolocator = str;
	}
	
	public String getToLocator()
	{
		return tolocator;
	}

	public void setToSite(String str)
	{
		toSite = str;
	}
	
	/**
	 *  Dynamic Init
	 *  @return true if initialized
	 */
	public boolean dynInit() throws Exception
	{
		log.config("");
		setTitle(Msg.getElement(Env.getCtx(), "ER_LaborActivity_ID", false) + " .. " + Msg.translate(Env.getCtx(), "CreateFrom"));
		
		return true;
	}   //  dynInit

	protected Vector<Vector<Object>> getStockDataByLPN (int ASI)
	{
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();

		StringBuilder sql = new StringBuilder("select org.ad_org_id, org.value as orgvalue, asi.description as asidescription,  strg.M_Locator_ID, loc.value as locatorname, u.name as uomname, p.C_UOM_ID, strg.m_attributesetinstance_id, strg.qtyonhand, p.M_Product_ID, p.name as productname from m_storageonhand strg ");

		sql.append(" inner join m_product p on p.m_product_id=strg.m_product_id");
		sql.append(" inner join C_UOM u on p.C_UOM_ID=u.C_UOM_ID");
		sql.append(" inner join M_Locator loc on strg.M_Locator_ID=loc.M_Locator_ID");
		sql.append(" inner join m_attributesetinstance asi on strg.m_attributesetinstance_id = asi.m_attributesetinstance_id");
		sql.append(" inner join ad_org org on strg.ad_org_id = org.ad_org_id");

		sql.append(" where strg.m_attributesetinstance_id=" + ASI + " and strg.qtyonhand <> 0 and strg.m_locator_id=1000016");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				Integer LPN = 0;
				Vector<Object> line = new Vector<Object>();

				line.add(Boolean.TRUE);           //  0-Selection
				
				BigDecimal qtyOnHand = rs.getBigDecimal("QtyOnHand");
				BigDecimal qtyEntered = qtyOnHand;

				KeyNamePair pp = new KeyNamePair(rs.getInt("M_Product_ID"), rs.getString("productname").trim());			// Product
				line.add(pp);                           //  2-UOM

				BigDecimal mic = Env.ZERO;
				BigDecimal siz = Env.ZERO;
				
				LPN = rs.getInt("m_attributesetinstance_id");
				String strBrandName = "";
				line.add(mic);  //  1-Qty
				line.add(siz);  //  1-Qty
				line.add(strBrandName);  //  1-Qty

				line.add(qtyEntered);  //  1-Qty
				line.add(qtyEntered);  //  1-Qty

				KeyNamePair ppuom = new KeyNamePair(rs.getInt("C_UOM_ID"), rs.getString("uomname").trim());			// UOM
				line.add(ppuom);		//UOM

				KeyNamePair pploc = new KeyNamePair(rs.getInt("m_locator_id"), rs.getString("locatorname").trim());			// Loc
				line.add(pploc);		//UOM
				
				KeyNamePair ppasi = new KeyNamePair(rs.getInt("m_attributesetinstance_id"), rs.getString("asidescription").trim());			// Loc
				line.add(ppasi);		//UOM

				line.add(LPN);		//UOM

				KeyNamePair porg = new KeyNamePair(rs.getInt("ad_org_id"), rs.getString("orgvalue").trim());			// org
				line.add(porg);		//UOM

				
				data.add(line);
				
				
			}
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, sql.toString(), e);
			//throw new DBException(e, sql.toString());
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
		return data;
	}

	protected Vector<Vector<Object>> getCouponData() 
	{
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		
		return data;
	}
	
	/**
	 *  Load Data - Order
	 *  @param C_Order_ID Order
	 *  @param forInvoice true if for invoice vs. delivery qty
	 */
	
	/**
	 * Get KeyNamePair for Locator.
	 * If no locator specified or the specified locator is not valid (e.g. warehouse not match),
	 * a default one will be used.
	 * @param M_Locator_ID
	 * @return KeyNamePair
	 */
	protected KeyNamePair getLocatorKeyNamePair(int M_Locator_ID)
	{
		MLocator locator = null;
		
		// Load desired Locator
		if (M_Locator_ID > 0)
		{
			locator = MLocator.get(Env.getCtx(), M_Locator_ID);
			// Validate warehouse
			if (locator != null && locator.getM_Warehouse_ID() != getM_Warehouse_ID())
			{
				locator = null;
			}
		}
		
		// Try to use default locator from Order Warehouse
		if (locator == null && p_order != null && p_order.getM_Warehouse_ID() == getM_Warehouse_ID())
		{
			MWarehouse wh = MWarehouse.get(Env.getCtx(), p_order.getM_Warehouse_ID());
			if (wh != null)
			{
				locator = wh.getDefaultLocator();
			}
		}
		// Try to get from locator field
		if (locator == null)
		{
			if (defaultLocator_ID > 0)
			{
				locator = MLocator.get(Env.getCtx(), defaultLocator_ID);
			}
		}
		// Validate Warehouse
		if (locator == null || locator.getM_Warehouse_ID() != getM_Warehouse_ID())
		{
			locator = MWarehouse.get(Env.getCtx(), getM_Warehouse_ID()).getDefaultLocator();
		}
		
		KeyNamePair pp = null ;
		if (locator != null)
		{
			pp = new KeyNamePair(locator.get_ID(), locator.getValue());
		}
		return pp;
	}
	
	/**
	 *  List number of rows selected
	 */
	public void info(IMiniTable miniTable, IStatusBar statusBar)
	{

	}   //  infoInvoice

	
	/**
	 *  Save - Create Invoice Lines
	 *  @return true if saved
	 */
	public boolean save(IMiniTable miniTable, String trxName)
	{
		
		int ER_LaborActivity_ID = ((Integer) getGridTab().getValue("ER_LaborActivity_ID")).intValue();
		MLaborActivity movgroup = new MLaborActivity(Env.getCtx(), ER_LaborActivity_ID, trxName);
				
		List<MLaborActivityLine> list = movgroup.getLines(true);
				
				for (int i = 0; i < miniTable.getRowCount(); i++)
				{
					if (((Boolean)miniTable.getValueAt(i, 0)).booleanValue()) {
		
						KeyNamePair ppCoup = (KeyNamePair) miniTable.getValueAt(i, 1);
						KeyNamePair partner = (KeyNamePair) miniTable.getValueAt(i, 6);
						int couponID = ppCoup.getKey();
		
						for (MLaborActivityLine mov : list) {
							if (mov.getER_Coupon_ID() == couponID)
							{
								break;
							}
						}

						BigDecimal QtyEntered = (BigDecimal) miniTable.getValueAt(i, 5); // Qty

						
						MLaborActivityLine movement;
							movement = new MLaborActivityLine(Env.getCtx(), 0, trxName);
							movement.setAD_Org_ID(movgroup.getAD_Org_ID());
							movement.setER_LaborActivity_ID(movgroup.get_ID());
							movement.setER_Coupon_ID(couponID);
							movement.setQty(QtyEntered);
							movement.setLine(0);
							movement.setC_BPartner_ID(partner.getKey());
							movement.save();
							list.add(movement);
						
					}   //   if selected
				}   //  for all rows
		
				
		return true;		

	}   //  saveInvoice

	protected Vector<String> getOISColumnNames()
	{
	    Vector<String> columnNames = new Vector<String>(10);
	    columnNames.add(Msg.getMsg(Env.getCtx(), "Select"));
	    columnNames.add(Msg.translate(Env.getCtx(), "ER_Coupon_ID"));
	    columnNames.add(Msg.translate(Env.getCtx(), "ER_Operation_ID"));
	    columnNames.add(Msg.translate(Env.getCtx(), "ER_DailyProduction_ID"));
	    columnNames.add(Msg.translate(Env.getCtx(), "Quantity"));
	    columnNames.add(Msg.translate(Env.getCtx(), "QtyApplied"));

	    columnNames.add(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
	    
	    return columnNames;
	}

	protected void configureMiniTable (IMiniTable miniTable)
	{
		int i = 1;
		miniTable.setColumnClass(0, Boolean.class,  false);     		//  Selection
		miniTable.setColumnClass(i++, String.class, true);   		//  CouponID
		miniTable.setColumnClass(i++, BigDecimal.class, true);   	//  OperationID
		miniTable.setColumnClass(i++, BigDecimal.class, true);   	//  DailyProductionID
		miniTable.setColumnClass(i++, String.class, true);   		//  CouponQuantity
		miniTable.setColumnClass(i++, BigDecimal.class, false);      //  QtyApplied
		miniTable.setColumnClass(i++, String.class, false);      //  QtyApplied

	}
	
	protected Vector<Vector<Object>> getStockData (int C_Order_ID, boolean forInvoice, int M_Locator_ID)
	{
		defaultLocator_ID = M_Locator_ID;
		return getOrderData (C_Order_ID, forInvoice);
	}
}
