package com.finfrock.airvoicewidget2;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedStorage {

	private static final String PHONE_NAME_KEY = "phoneNumber_";
	private static final String DISPLAY_TYPE_KEY = "displayType_";
	private static final String NAME_KEY = "name_";
	private static final String WARNING_LIMIT_KEY = "warning_limit_";
    private static final String PREFS_NAME = "com.finfrock.airvoicewidget2.AirvoiceDisplay";
    
    public void saveInformation(Context context, int appWidgetId, String phoneNumber, String displayType, String name, int warningLimit) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PHONE_NAME_KEY + appWidgetId, phoneNumber);
        prefs.putString(DISPLAY_TYPE_KEY + appWidgetId, displayType);
        prefs.putString(NAME_KEY + appWidgetId, name);
        prefs.putInt(WARNING_LIMIT_KEY + appWidgetId, warningLimit);
        prefs.commit();
    }
    
    public String getPhoneNumber(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		if (prefs != null) {
			String value = prefs.getString(PHONE_NAME_KEY + appWidgetId,
					"phone number not found");
			return value;
		} else {
			return "not found";
		}
    }
    
    public String getNameLabel(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		if (prefs != null) {
			String value = prefs.getString(NAME_KEY + appWidgetId,
					"Airvoice Wireless");
			return value;
		} else {
			return "not found";
		}
    }
    
    public String getDisplayType(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		if (prefs != null) {
			String value = prefs.getString(DISPLAY_TYPE_KEY + appWidgetId,
					"no display type found");
			return value;
		} else {
			return "not found";
		}
    }
    
    public int getWarningLimit(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		if (prefs != null) {
			return prefs.getInt(WARNING_LIMIT_KEY + appWidgetId, 0);
		} else {
			return 0;
		}
    }
}
