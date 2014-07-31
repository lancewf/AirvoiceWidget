package com.finfrock.airvoicewidget2.plans;

import java.text.DecimalFormat;

import com.finfrock.airvoicewidget2.AirvoiceDisplay;

public class PayAsYouGoPlan implements Plan {
	public double getCostPerMb(){
		return 0.066;
	}
	
	public double getCostPerMinute(){
		return 0.1;
	}
	
	public String getTextForWidget(Double dollarValue, String displayType) {

		String text = AirvoiceDisplay.NO_DATA_FOUND_TAG;
		if (displayType.equals(AirvoiceDisplay.MONEY_DISPLAY_TYPE)) {
			DecimalFormat df = new DecimalFormat("#.00");
			text = "$" + df.format(dollarValue);
		} else if (displayType.equals(AirvoiceDisplay.DATA_DISPLAY_TYPE)) {
			double mbs = dollarValue / getCostPerMb();
			text = Math.round(mbs) + "mb";
		} else if (displayType.equals(AirvoiceDisplay.MINUTES_DISPLAY_TYPE)) {
			double mins = dollarValue / getCostPerMinute();
			text = Math.round(mins) + "m";
		}
		return text;
	}
}
