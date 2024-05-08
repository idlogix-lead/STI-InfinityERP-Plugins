package org.eri.taxprovider;

import java.math.BigDecimal;

import org.adempiere.model.ITaxProvider;
import org.compiere.model.I_C_InvoiceLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrderTax;
import org.compiere.model.MPriceList;
import org.compiere.model.MRMA;
import org.compiere.model.MRMALine;
import org.compiere.model.MRMATax;
import org.compiere.model.MTaxProvider;
import org.compiere.process.ProcessInfo;
import org.compiere.model.MProduct;
//import org.compiere.model.MProductPrice;
import org.compiere.model.MInvoiceTax;
import org.compiere.model.MPriceListVersion;
//import org.compiere.model.MProductPricing;




public class DefaultTaxProvider implements ITaxProvider{

	@Override
	public boolean calculateOrderTaxTotal(MTaxProvider provider, MOrder order) {
		// TODO Auto-generated method stub
//		double nettotal=0;
//		double grandtotal=0;
//		
//		for ( MOrderLine line : order.getLines() ) {
//			nettotal = nettotal + line.getLineNetAmt().doubleValue();
//			grandtotal = grandtotal + line.getLineTotalAmt().doubleValue();
//		}
//		order.setTotalLines(new BigDecimal(nettotal));
//		order.setGrandTotal(new BigDecimal(nettotal));
//		order.save();
		return true;
	}

	@Override
	public boolean updateOrderTax(MTaxProvider provider, MOrderLine line) {
		// TODO Auto-generated method stub
		calculateOrderTax(provider, line.is_new(),line);
		return true;
	}

	@Override
	public boolean recalculateTax(MTaxProvider provider, MOrderLine line, boolean newRecord) {
		// TODO Auto-generated method stub
		calculateOrderTax(provider, newRecord,line);
		return true;
	}

	@Override
	public boolean updateHeaderTax(MTaxProvider provider, MOrderLine line) {
		// TODO Auto-generated method stub
//		calculateOrderTax(provider, line.is_new(),line);
		
		return true;
	}

	@Override
	public boolean calculateInvoiceTaxTotal(MTaxProvider provider, MInvoice invoice) {
		// TODO Auto-generated method stub
//		double nettotal=0;
//		double grandtotal=0;
//		
//		for ( MInvoiceLine line : invoice.getLines() ) {
//		//	calculateInvoiceTax(provider, line.is_new(),line,false);
//			nettotal = nettotal + line.getLineNetAmt().doubleValue();
//			grandtotal = grandtotal + line.getLineTotalAmt().doubleValue();
//		}
//		invoice.setTotalLines(new BigDecimal(nettotal));
//		invoice.setGrandTotal(new BigDecimal(grandtotal));
//		invoice.save();
		return true;
	}

	@Override
	public boolean updateInvoiceTax(MTaxProvider provider, MInvoiceLine line) {
		// TODO Auto-generated method stub
//		calculateInvoiceTax(provider, line.is_new(),line,true);
		
		return true;
	}

	@Override
	public boolean recalculateTax(MTaxProvider provider, MInvoiceLine line, boolean newRecord) {
		// TODO Auto-generated method stub
		calculateInvoiceTax(provider, newRecord,line,true);
		return true;
	}

	@Override
	public boolean updateHeaderTax(MTaxProvider provider, MInvoiceLine line) {
		// TODO Auto-generated method stub
//		calculateInvoiceTax(provider, line.is_new(),line,true);
		
		return true;
	}

	@Override
	public boolean calculateRMATaxTotal(MTaxProvider provider, MRMA rma) {
		// TODO Auto-generated method stub
		
		return true;
	}

	@Override
	public boolean updateRMATax(MTaxProvider provider, MRMALine line) {
		// TODO Auto-generated method stub
//		calculateMRMATax(provider, line.is_new(),line);
		
		return true;
	}

	@Override
	public boolean recalculateTax(MTaxProvider provider, MRMALine line, boolean newRecord) {
		// TODO Auto-generated method stub
//		calculateMRMATax(provider, newRecord,line);
		return true;
	}

	@Override
	public boolean updateHeaderTax(MTaxProvider provider, MRMALine line) {
		// TODO Auto-generated method stub
//		calculateMRMATax(provider, line.is_new(),line);
		return true;
	}

	@Override
	public String validateConnection(MTaxProvider provider, ProcessInfo pi) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	private void taxcal() {
		
		try {}catch(Exception ex) {}
		String msg="";
//		
////MInvoiceTax mt =  (MInvoiceTax)A_PO;
////MInvoice invoice = MInvoice.get( A_PO.get_Value("C_Invoice_ID"));
////
//if(invoice.isSOTrx() ) {
//		double nettotal=0;
//		double grandtotal=0;
//		double tax = 0;
//		double taxBaseAmount = 0;				
//		for ( MInvoiceLine line : invoice.getLines() ) {
//			if(mt.get_ID()==line.getC_Tax_ID()) {
//				MProduct product = line.getProduct();
//				double taxrate= mt.getC_Tax().getRate().doubleValue();
//				double qty = (double)line.get_Value("QtyInvoiced");
//			if( product == null)
//			{
//				taxBaseAmount = taxBaseAmount + (line.getLineNetAmt().doubleValue());
//				tax = tax + (line.getLineNetAmt().doubleValue() * (taxrate/100));
//			}
//			else{
//				MPriceList pList =  MPriceList.get( invoice.getM_PriceList_ID(),null);
//				MPriceListVersion plv = pList.getPriceListVersion(invoice.getDateInvoiced());
//				double mrp =	((BigDecimal)plv.getProductPrice(" and m_product_id= "+line.get_Value("M_Product_ID") )[0]
//								.get_Value("MRP")).doubleValue();
//				double mrpextax  = mrp / (1+ taxrate / 100 ) ;
//				tax = tax + (qty * (mrpextax * (taxrate/100)));
//				taxBaseAmount = taxBaseAmount + (qty * mrpextax );
//				
//				}		
//		} // //End Same TaxType
//	}//End For Loop
//	mt.setTaxAmt(new BigDecimal( (tax )));
//	mt.setTaxBaseAmt(new BigDecimal( (taxBaseAmount )));
//	}// End IsSOTrx
//	
//result=msg;

	}
	private void calculateInvoiceTax(MTaxProvider provider,boolean newRecord, MInvoiceLine line,boolean calculateTotal) {
	
		MInvoiceTax mt =  MInvoiceTax.get(line, 0, newRecord, null);
		
		
		
		MInvoice invoice = MInvoice.get(line.getC_Invoice_ID());
	   
		if(invoice.isSOTrx() ) {
			invoice.getC_BPartner().getSO_CreditLimit();
		MProduct product =	line.getProduct();
		
//		BigDecimal old = mt.getTaxAmt();
		double qty = (double)line.getQtyInvoiced().doubleValue();
		double taxrate= mt.getC_Tax().getRate().doubleValue();
		double old_tax=mt.getTaxAmt().doubleValue(),old_taxBaseAmount=mt.getTaxBaseAmt().doubleValue();
		double tax = 0;
		double taxBaseAmount = 0;
		double oldtaxBaseAmount =0;
		double old_line_tax=0;
		if( product == null)
		{
			taxBaseAmount = line.getLineNetAmt().doubleValue();
			 oldtaxBaseAmount =((BigDecimal) line.get_ValueOld(I_C_InvoiceLine.COLUMNNAME_LineNetAmt)).doubleValue();
			tax = taxBaseAmount * (taxrate/100);
			old_line_tax = oldtaxBaseAmount * (taxrate/100);
		}
		
		else{
			MPriceList pList =  MPriceList.get( invoice.getM_PriceList_ID(), invoice.get_TrxName());
			MPriceListVersion plv = pList.getPriceListVersion(invoice.getDateInvoiced());
			//+ line.getM_Product_ID();	//		Product ID=1004573
			double oldlineQTY =((BigDecimal) line.get_ValueOld(I_C_InvoiceLine.COLUMNNAME_QtyInvoiced)).doubleValue();
			
			double mrp =	((BigDecimal)plv.getProductPrice(" and m_product_id= "+line.getM_Product_ID() )[0]
							.get_Value("MRP")).doubleValue();
			double mrpextax  = mrp / (1+ taxrate / 100 ) ;
			tax = qty * (mrpextax * (taxrate/100));
			old_line_tax = oldlineQTY* (mrpextax * (taxrate/100));
			taxBaseAmount = qty * mrpextax ;
			oldtaxBaseAmount = oldlineQTY * mrpextax ; 
			
			}		
		if(newRecord)
		{
			mt.setTaxAmt(new BigDecimal(old_tax + (tax )));
			mt.setTaxBaseAmt(new BigDecimal(old_taxBaseAmount+ (taxBaseAmount )));
			
		}	
		else
			{
			mt.setTaxAmt(new BigDecimal(old_tax + (tax-old_line_tax)));
			mt.setTaxBaseAmt(new BigDecimal(old_taxBaseAmount+ (taxBaseAmount-oldtaxBaseAmount)));
			}
//		mt.setC_TaxProvider_ID(provider.get_ID());
		mt.save();
		
		if(calculateTotal) {
		double nettotal=0;
		double grandtotal=0;
		for ( MInvoiceLine ll : invoice.getLines() ) {
			if(line.get_ID()==ll.get_ID()) {
				nettotal = nettotal + line.getLineNetAmt().doubleValue();
				grandtotal = grandtotal + line.getLineTotalAmt().doubleValue();				
			}
			else{
			nettotal = nettotal + ll.getLineNetAmt().doubleValue();
			grandtotal = grandtotal + ll.getLineTotalAmt().doubleValue();}
		}
		invoice.setTotalLines(new BigDecimal(nettotal));
		invoice.setGrandTotal(new BigDecimal(grandtotal));
		invoice.save();
		}
		}
	}
	private void calculateOrderTax(MTaxProvider provider,boolean newRecord, MOrderLine line) {
	MOrderTax mt =  MOrderTax.get(line, 0, newRecord, null);

	MOrder invoice = new MOrder(line.getCtx(), line.getC_Order_ID(),line.get_TrxName());
		if(invoice.isSOTrx() ) {
			
		MProduct product =	line.getProduct();

//		BigDecimal old = mt.getTaxAmt();
		double qty = (double)line.getQtyInvoiced().doubleValue();

		double tax = 0;
		double taxBaseAmount = 0;
		if( product == null)
		{
			taxBaseAmount = line.getLineNetAmt().doubleValue();
		}
		
		else{
			MPriceList pList =  MPriceList.get( invoice.getM_PriceList_ID(), invoice.get_TrxName());
			MPriceListVersion plv = pList.getPriceListVersion(invoice.getDateOrdered());
			//+ line.getM_Product_ID();	//		Product ID=1004573
			double price =	((BigDecimal)plv.getProductPrice(" and m_product_id= "+line.getM_Product_ID() )[0].get_Value("MRP")).doubleValue();
			taxBaseAmount = price * qty;
			
		}
		tax = taxBaseAmount * (mt.getC_Tax().getRate().doubleValue()/100);
		BigDecimal taxamt = new BigDecimal(tax);
	
		mt.setTaxAmt(taxamt);
		mt.setTaxBaseAmt(line.getLineNetAmt());
		mt.setC_TaxProvider_ID(provider.get_ID());
		mt.save();
		
		double nettotal=0;
		double grandtotal=0;
		for ( MOrderLine ll : invoice.getLines() ) {
			if(line.get_ID()==ll.get_ID()) {
				nettotal = nettotal + line.getLineNetAmt().doubleValue();
//				grandtotal = grandtotal + line.g				
			}
			else{
			nettotal = nettotal + ll.getLineNetAmt().doubleValue();
//				grandtotal = grandtotal + ll.getLine().doubleValue();}
			}
		invoice.setTotalLines(new BigDecimal(nettotal));
		invoice.setGrandTotal(new BigDecimal(nettotal));
		invoice.save();
		}
	}
	}
	
	private void calculateMRMATax(MTaxProvider provider,boolean newRecord, MRMALine line) {
	MRMATax mt =  MRMATax.get(line, 0, newRecord, null);
	
	MRMA invoice = new MRMA(line.getCtx(), line.getM_RMA_ID(),line.get_TrxName());
		if(invoice.isSOTrx() ) {
			
		MProduct product =	line.getProduct();

//		BigDecimal old = mt.getTaxAmt();
		double qty = (double)line.getQtyInvoiced().doubleValue();

		double tax = 0;
		double taxBaseAmount = 0;
		if( product == null)
		{
			taxBaseAmount = line.getLineNetAmt().doubleValue();
		}
		
		else{
			MPriceList pList =  MPriceList.get( invoice.getOriginalInvoice().getM_PriceList_ID(), invoice.get_TrxName());
			MPriceListVersion plv = pList.getPriceListVersion(invoice.getOriginalInvoice().getDateInvoiced());
			//+ line.getM_Product_ID();	//		Product ID=1004573
			double price =	((BigDecimal)plv.getProductPrice(" and m_product_id= "+line.getM_Product_ID() )[0].get_Value("MRP")).doubleValue();
			taxBaseAmount = price * qty;
			
		}
		tax = taxBaseAmount * (mt.getC_Tax().getRate().doubleValue()/100);
		BigDecimal taxamt = new BigDecimal(tax);
	
		mt.setTaxAmt(taxamt);
		mt.setTaxBaseAmt(line.getLineNetAmt());
		mt.setC_TaxProvider_ID(provider.get_ID());
		mt.save();
		
		
	}
	}
}
