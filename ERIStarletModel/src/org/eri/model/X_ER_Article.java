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

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for ER_Article
 *  @author iDempiere (generated) 
 *  @version Release 7.1 - $Id$ */
public class X_ER_Article extends PO implements I_ER_Article, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20210329L;

    /** Standard Constructor */
    public X_ER_Article (Properties ctx, int ER_Article_ID, String trxName)
    {
      super (ctx, ER_Article_ID, trxName);
      /** if (ER_Article_ID == 0)
        {
			setER_Article_ID (0);
        } */
    }

    /** Load Constructor */
    public X_ER_Article (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
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
      StringBuilder sb = new StringBuilder ("X_ER_Article[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
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

	/** Set Article.
		@param ER_Article_ID Article	  */
	public void setER_Article_ID (int ER_Article_ID)
	{
		if (ER_Article_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_ER_Article_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_ER_Article_ID, Integer.valueOf(ER_Article_ID));
	}

	/** Get Article.
		@return Article	  */
	public int getER_Article_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_ER_Article_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set ER_Article_UU.
		@param ER_Article_UU ER_Article_UU	  */
	public void setER_Article_UU (String ER_Article_UU)
	{
		set_Value (COLUMNNAME_ER_Article_UU, ER_Article_UU);
	}

	/** Get ER_Article_UU.
		@return ER_Article_UU	  */
	public String getER_Article_UU () 
	{
		return (String)get_Value(COLUMNNAME_ER_Article_UU);
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}