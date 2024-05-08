package org.eri.factories;

import java.sql.ResultSet;

import org.adempiere.base.IModelFactory;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MProductionLine;
import org.compiere.model.PO;
import org.compiere.util.Env;
import org.eri.model.MCoupon;
import org.eri.model.MCutting;
import org.eri.model.MDailyProduction;
import org.eri.model.MLaborActivity;
import org.eri.model.MLaborActivityLine;
import org.eri.model.MLasting;
import org.eri.model.MMasterProduction;
import org.eri.model.MMasterProductionLine;
import org.eri.model.MMoulding;
import org.eri.model.MMovement;
import org.eri.model.MOperation;
import org.eri.model.MPacking;
import org.eri.model.MProduct;
import org.eri.model.MProductBOM;
import org.eri.model.MProduction;
import org.eri.model.MStitching;
import org.eri.model.MStrobel;

public class ModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
		
		
		
		if (tableName.equals(MMasterProduction.Table_Name)) { return MMasterProduction.class; }
		if (tableName.equals(MMasterProductionLine.Table_Name)) { return MMasterProductionLine.class; }
		if (tableName.equals(MProduction.Table_Name)) { return MProduction.class; }
		if (tableName.equals(MProductionLine.Table_Name)) { return MProductionLine.class; }
		if (tableName.equals(MCutting.Table_Name)) { return MCutting.class; }
		if (tableName.equals(MStitching.Table_Name)) { return MStitching.class; }
		if (tableName.equals(MStrobel.Table_Name)) { return MStrobel.class; }
		if (tableName.equals(MMoulding.Table_Name)) { return MMoulding.class; }
		if (tableName.equals(MPacking.Table_Name)) { return MPacking.class; }
		if (tableName.equals(MDailyProduction.Table_Name)) { return MDailyProduction.class; }
		if (tableName.equals(MProduct.Table_Name)) { return MProduct.class; }
		if (tableName.equals(MProductBOM.Table_Name)) { return MProductBOM.class; }
		if (tableName.equals(MLasting.Table_Name)) { return MLasting.class; }
		if (tableName.equals(MCoupon.Table_Name)) { return MCoupon.class; }
		if (tableName.equals(MOperation.Table_Name)) { return MOperation.class; }
		if (tableName.equals(MLaborActivityLine.Table_Name)) { return MLaborActivityLine.class; }
		if (tableName.equals(MLaborActivity.Table_Name)) { return MLaborActivity.class; }
		if (tableName.equals(MMovement.Table_Name)) { return MMovement.class; }
		
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		
		if (tableName.equals(MMasterProduction.Table_Name)) { return new MMasterProduction(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MMasterProductionLine.Table_Name)) { return new MMasterProductionLine(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MProduction.Table_Name)) { return new MProduction(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MProductionLine.Table_Name)) { return new MProductionLine(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MCutting.Table_Name)) { return new MCutting(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MStitching.Table_Name)) { return new MStitching(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MStrobel.Table_Name)) { return new MStrobel(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MMoulding.Table_Name)) { return new MMoulding(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MPacking.Table_Name)) { return new MPacking(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MDailyProduction.Table_Name)) { return new MDailyProduction(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MProduct.Table_Name)) { return new MProduct(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MProductBOM.Table_Name)) { return new MProductBOM(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MLasting.Table_Name)) { return new MLasting(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MCoupon.Table_Name)) { return new MCoupon(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MOperation.Table_Name)) { return new MOperation(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MLaborActivityLine.Table_Name)) { return new MLaborActivityLine(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MLaborActivity.Table_Name)) { return new MLaborActivity(Env.getCtx(), Record_ID, trxName); }
		if (tableName.equals(MMovement.Table_Name)) { return new MMovement(Env.getCtx(), Record_ID, trxName); }
		
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		
		if (tableName.equals(MMasterProduction.Table_Name)) { return new MMasterProduction(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MMasterProductionLine.Table_Name)) { return new MMasterProductionLine(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MProduction.Table_Name)) { return new MProduction(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MProductionLine.Table_Name)) { return new MProductionLine(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MCutting.Table_Name)) { return new MCutting(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MStitching.Table_Name)) { return new MStitching(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MStrobel.Table_Name)) { return new MStrobel(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MMoulding.Table_Name)) { return new MMoulding(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MPacking.Table_Name)) { return new MPacking(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MDailyProduction.Table_Name)) { return new MDailyProduction(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MProduct.Table_Name)) { return new MProduct(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MProductBOM.Table_Name)) { return new MProductBOM(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MLasting.Table_Name)) { return new MLasting(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MCoupon.Table_Name)) { return new MCoupon(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MOperation.Table_Name)) { return new MOperation(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MLaborActivityLine.Table_Name)) { return new MLaborActivityLine(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MLaborActivity.Table_Name)) { return new MLaborActivity(Env.getCtx(), rs, trxName); }
		if (tableName.equals(MMovement.Table_Name)) { return new MMovement(Env.getCtx(), rs, trxName); }

		return null;
	}

}
