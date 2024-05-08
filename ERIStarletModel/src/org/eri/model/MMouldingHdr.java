package org.eri.model;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.adempiere.exceptions.DBException;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

public class MMouldingHdr extends X_ER_MouldingHdr {

	public MMouldingHdr(Properties ctx, int ER_Moulding_Hdr_ID, String trxName) {
		super(ctx, ER_Moulding_Hdr_ID, trxName);
	}

	  public MMouldingHdr (Properties ctx, ResultSet rs, String trxName)
	    {
	      super (ctx, rs, trxName);
	    }

	  
	private static final long serialVersionUID = 1L;

	
	
	
}
