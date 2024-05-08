/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.eri.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for ER_MasterProductionLine
 *  @author iDempiere (generated) 
 *  @version Release 7.1 - $Id$ */
public class X_ER_MasterProductionLine extends PO implements I_ER_MasterProductionLine, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20210329L;

    /** Standard Constructor */
    public X_ER_MasterProductionLine (Properties ctx, int ER_MasterProductionLine_ID, String trxName)
    {
      super (ctx, ER_MasterProductionLine_ID, trxName);
      /** if (ER_MasterProductionLine_ID == 0)
        {
			setC_OrderLine_ID (0);
			setER_MasterProductionLine_ID (0);
			setProcessed (false);
			setQty (Env.ZERO);
        } */
    }

    /** Load Constructor */
    public X_ER_MasterProductionLine (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 1 - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_ER_MasterProductionLine[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_C_OrderLine getC_OrderLine() throws RuntimeException
    {
		return (org.compiere.model.I_C_OrderLine)MTable.get(getCtx(), org.compiere.model.I_C_OrderLine.Table_Name)
			.getPO(getC_OrderLine_ID(), get_TrxName());	}

	/** Set Sales Order Line.
		@param C_OrderLine_ID 
		Sales Order Line
	  */
	public void setC_OrderLine_ID (int C_OrderLine_ID)
	{
		if (C_OrderLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_OrderLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_OrderLine_ID, Integer.valueOf(C_OrderLine_ID));
	}

	/** Get Sales Order Line.
		@return Sales Order Line
	  */
	public int getC_OrderLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_OrderLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	public I_ER_MasterProduction getER_MasterProduction() throws RuntimeException
    {
		return (I_ER_MasterProduction)MTable.get(getCtx(), I_ER_MasterProduction.Table_Name)
			.getPO(getER_MasterProduction_ID(), get_TrxName());	}

	/** Set Master Production.
		@param ER_MasterProduction_ID Master Production	  */
	public void setER_MasterProduction_ID (int ER_MasterProduction_ID)
	{
		if (ER_MasterProduction_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_ER_MasterProduction_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_ER_MasterProduction_ID, Integer.valueOf(ER_MasterProduction_ID));
	}

	/** Get Master Production.
		@return Master Production	  */
	public int getER_MasterProduction_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ER_MasterProduction_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Master Production Line.
		@param ER_MasterProductionLine_ID Master Production Line	  */
	public void setER_MasterProductionLine_ID (int ER_MasterProductionLine_ID)
	{
		if (ER_MasterProductionLine_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_ER_MasterProductionLine_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_ER_MasterProductionLine_ID, Integer.valueOf(ER_MasterProductionLine_ID));
	}

	/** Get Master Production Line.
		@return Master Production Line	  */
	public int getER_MasterProductionLine_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ER_MasterProductionLine_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set ER_MasterProductionLine_UU.
		@param ER_MasterProductionLine_UU ER_MasterProductionLine_UU	  */
	public void setER_MasterProductionLine_UU (String ER_MasterProductionLine_UU)
	{
		set_Value (COLUMNNAME_ER_MasterProductionLine_UU, ER_MasterProductionLine_UU);
	}

	/** Get ER_MasterProductionLine_UU.
		@return ER_MasterProductionLine_UU	  */
	public String getER_MasterProductionLine_UU () 
	{
		return (String)get_Value(COLUMNNAME_ER_MasterProductionLine_UU);
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1) 
			set_Value (COLUMNNAME_M_Product_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Quantity.
		@param Qty 
		Quantity
	  */
	public void setQty (BigDecimal Qty)
	{
		set_Value (COLUMNNAME_Qty, Qty);
	}

	/** Get Quantity.
		@return Quantity
	  */
	public BigDecimal getQty () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_Qty);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}