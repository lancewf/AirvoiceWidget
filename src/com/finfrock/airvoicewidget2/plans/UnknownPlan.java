package com.finfrock.airvoicewidget2.plans;

public class UnknownPlan implements Plan {
	public float getCostPerMb(){
		return 0.000000000001f;
	}
	
	public float getCostPerMinute(){
		return 0.00000000001f;
	}
	
	public String getTextForWidget(float dollarValue, String displayType) {
		return "UNKN";
	}
	
	public float getAmount(float dollarValue, String displayType){
		return 0.0f;
	}
}
