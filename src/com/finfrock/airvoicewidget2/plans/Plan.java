package com.finfrock.airvoicewidget2.plans;

public interface Plan {
	double getCostPerMb();
	double getCostPerMinute();
	String getTextForWidget(Double dollarValue, String displayType);
    double getAmount(Double dollarValue, String displayType);
}
