package org.eri.processes;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.eri.model.MStitching;
import org.eri.model.MStitchingHdr;

public class AddStitchingProduction extends SvrProcess{

	int id ;
	int stitchingHdrID;
	int dpID;
	Timestamp movementDate;
	int productionLine;
	
	@Override
	protected void prepare() {
		id = getRecord_ID();
		
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("ER_StitchingHdr_ID"))
				stitchingHdrID = para[i].getParameterAsInt();			
		
			else if (name.equals("ER_DailyProduction_ID"))
				dpID = para[i].getParameterAsInt();
			else if (name.equals("PP_Production_Line_ID"))
				productionLine = para[i].getParameterAsInt();
			else if (name.equals("MovementDate"))
				movementDate = para[i].getParameterAsTimestamp();

			
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	}

	@Override
	protected String doIt() throws Exception {
		// TODO Auto-generated method stub
//		System.err.println("I am nt here!");
		
		
		
		
		
		int rows = 0; 
		String resultMsg ="";
		String id=""; int hdrid=stitchingHdrID;
		MStitchingHdr StitchingHdr= new MStitchingHdr(Env.getCtx(), stitchingHdrID, null);
		String strSQL = "select * from  M_Production where  ER_DailyProduction_ID=" + dpID  ; 
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmtbal = null;
		ResultSet rs_bal = null;
		int balqty =0;
		try
		{
			pstmt = DB.prepareStatement (strSQL.toString(), null);
						rs = pstmt.executeQuery ();
			while (rs.next ())		//	Order
			{
				String strSQLbal = "select * from er_stitching_balance_size_v where balanceqty>0 and M_Production_ID=" + rs.getInt("M_Production_ID"); 
				pstmtbal = DB.prepareStatement (strSQLbal.toString(), null);
				rs_bal = pstmtbal.executeQuery ();
				balqty=0;
				while (rs_bal.next ())		//	Order
				{
					balqty = rs_bal.getInt("balanceqty");		
				}
				rows =rows+1;				
				MStitching pline = new MStitching(Env.getCtx(), 0, null);
				pline.setAD_Org_ID(rs.getInt("AD_Org_ID"));
				pline.setM_Production_ID(rs.getInt("M_Production_ID"));
				pline.setQty(new BigDecimal(balqty));
				pline.set_ValueOfColumn("M_Product_ID", rs.getInt("m_product_id"));
				pline.set_ValueOfColumn("ER_StitchingHdr_ID", hdrid);
				pline.set_ValueOfColumn("PP_Production_Line_ID", productionLine);
//				pline.set_ValueOfColumn("MovementDate",new Timestamp(movementDate.getTime()));
	            if(balqty>0)				
	            pline.save();		
			}
//				StitchingHdr.set_Value("Processed",'Y');
				StitchingHdr.save();	
				resultMsg="product"+rows+" Production Saved Successfully.";
		}
		catch (Exception e)
		{
		resultMsg="exception:"+e.getMessage();
		}

		
System.err.println(resultMsg);
		return resultMsg;
	}

}
