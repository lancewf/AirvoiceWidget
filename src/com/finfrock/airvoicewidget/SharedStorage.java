package com.finfrock.airvoicewidget;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedStorage {

	private static final String PHONE_NAME_KEY = "phoneNumber_";
	private static final String DISPLAY_TYPE_KEY = "displayType_";
    private static final String PREFS_NAME = "com.finfrock.airvoicewidget.AirvoiceDisplay";
    
    public void saveInformation(Context context, int appWidgetId, String phoneNumber, String displayType) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PHONE_NAME_KEY + appWidgetId, phoneNumber);
        prefs.putString(DISPLAY_TYPE_KEY + appWidgetId, displayType);
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
}
