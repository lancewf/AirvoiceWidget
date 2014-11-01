package com.finfrock.airvoicewidget2.plans;

import java.text.DecimalFormat;

public class TenDollarPlan implements Plan {
	public float getCostPerMb(){
		return 0.066f;
	}
	
	public float getCostPerMinute(){
		return 0.04f;
	}
	
	public String getTextForWidget(float amount, String displayType) {

		String text = NO_DATA_FOUND_TAG;
		if (displayType.equals(MONEY_DISPLAY_TYPE)) {
			DecimalFormat df = new DecimalFormat("#.00");
			text = "$"+df.format(amount);
		} else if (displayType.equals(DATA_DISPLAY_TYPE)) {
			text = Math.round(amount) + "mb";
		} else if (displayType.equals(MINUTES_DISPLAY_TYPE)) {
			text = Math.round(amount) + "m";
		}
		return text;
	}
	
	public float getAmount(float dollarValue, String displayType){
		float amount = 0.0f;
		if (displayType.equals(MONEY_DISPLAY_TYPE)) {
			amount = dollarValue;
		} else if (displayType.equals(DATA_DISPLAY_TYPE)) {
			float mbs = dollarValue / getCostPerMb();
			amount = mbs;
		} else if (displayType.equals(MINUTES_DISPLAY_TYPE)) {
			float mins = dollarValue / getCostPerMinute();
			amount = Math.round(mins);
		}
		
		return amount;
	}
}
