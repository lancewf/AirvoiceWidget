package com.finfrock.airvoicewidget2.plans;

public interface Plan {
	public static final String NO_DATA_FOUND_TAG = "---";
	public static final String MONEY_DISPLAY_TYPE = "Money";
	public static final String DATA_DISPLAY_TYPE = "Data";
	public static final String MINUTES_DISPLAY_TYPE = "Minutes";
	double getCostPerMb();
	double getCostPerMinute();
	String getTextForWidget(Double dollarValue, String displayType);
    double getAmount(Double dollarValue, String displayType);
}
