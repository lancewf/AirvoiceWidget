package com.finfrock.airvoicewidget2.plans;

public class PlanFactory {
	
	public Plan createPlan(String planText) {
		if (planText.contains("PAY AS YOU GO")) {
			return new PayAsYouGoPlan();
		} else if (planText.contains("250 TALK OR 500 TEXT 30 DAYS")) {
			return new TenDollarPlan();
		} else if (planText.toUpperCase().contains("UNLIMITED")) {
			return new UnlimitedPlan();
		} else {
			return null;
		}
	}
}
