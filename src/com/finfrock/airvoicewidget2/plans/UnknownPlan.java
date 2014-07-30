package com.finfrock.airvoicewidget2.plans;

public class UnknownPlan implements Plan {
	public double getCostPerMb(){
		return 0.000000000001;
	}
	
	public double getCostPerMinute(){
		return 0.00000000001;
	}
	
	public String getTextForWidget(Double dollarValue, String displayType) {
		return "UNKN";
	}
}
