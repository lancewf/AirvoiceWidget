package com.finfrock.airvoicewidget2.plans;

import java.text.DecimalFormat;

public class PayAsYouGoPlan implements Plan {
	public double getCostPerMb(){
		return 0.066;
	}
	
	public double getCostPerMinute(){
		return 0.1;
	}
	
	public String getTextForWidget(Double amount, String displayType) {

		String text = NO_DATA_FOUND_TAG;
		if (displayType.equals(MONEY_DISPLAY_TYPE)) {
			DecimalFormat df = new DecimalFormat("#.00");
			text = "$" + df.format(amount);
		} else if (displayType.equals(DATA_DISPLAY_TYPE)) {
			text = Math.round(amount) + "mb";
		} else if (displayType.equals(MINUTES_DISPLAY_TYPE)) {
			text = Math.round(amount) + "m";
		}
		return text;
	}
	
	public double getAmount(Double dollarValue, String displayType){
		double amount = 0;
		if (displayType.equals(MONEY_DISPLAY_TYPE)) {
			amount = dollarValue;
		} else if (displayType.equals(DATA_DISPLAY_TYPE)) {
			double mbs = dollarValue / getCostPerMb();
			amount = mbs;
		} else if (displayType.equals(MINUTES_DISPLAY_TYPE)) {
			double mins = dollarValue / getCostPerMinute();
			amount = Math.round(mins);
		}
		
		return amount;
	}
}
