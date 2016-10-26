package com.finfrock.airvoicewidget2;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedStorage {

	private static final String PHONE_NAME_KEY = "phoneNumber_";
	private static final String DISPLAY_TYPE_KEY = "displayType_";
	private static final String NAME_KEY = "name_";
	private static final String WARNING_LIMIT_KEY = "warning_limit_";
	private static final String WARNING_DAYS_KEY = "warning_days_";
	private static final String HAS_SENT_WARNING_AMOUNT_NOTIFICATION = "has_sent_warning_amount_notification_";
    private static final String HAS_SENT_WARNING_EXPIRE_NOTIFICATION = "has_sent_warning_expire_notification_";
	private static final String PREFS_NAME = "com.finfrock.airvoicewidget2.AirvoiceDisplay";
    
    public void saveInformation(Context context, int appWidgetId, 
    		String phoneNumber, String displayType, String name, int warningLimit, 
    		int warningDays) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PHONE_NAME_KEY + appWidgetId, phoneNumber);
        prefs.putString(DISPLAY_TYPE_KEY + appWidgetId, displayType);
        prefs.putString(NAME_KEY + appWidgetId, name);
        prefs.putInt(WARNING_LIMIT_KEY + appWidgetId, warningLimit);
        prefs.putInt(WARNING_DAYS_KEY + appWidgetId, warningDays);
        prefs.putBoolean(HAS_SENT_WARNING_AMOUNT_NOTIFICATION + appWidgetId, false);
        prefs.commit();
    }
    
    public Boolean hasSentWarningAmountNotification(Context context, int appWidgetId){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		if (prefs != null) {
			return prefs.getBoolean(HAS_SENT_WARNING_AMOUNT_NOTIFICATION + appWidgetId, false);
		}
		
    	return false;
    }
    
    public void setSentWarningAmountNotification(Context context, int appWidgetId){
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(HAS_SENT_WARNING_AMOUNT_NOTIFICATION + appWidgetId, true);
        prefs.commit();
    }
    
    public void clearSentWarningAmountNotification(Context context, int appWidgetId){
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(HAS_SENT_WARNING_AMOUNT_NOTIFICATION + appWidgetId, false);
        prefs.commit();
    }
    
    public Boolean hasSentWarningExpireNotification(Context context, int appWidgetId){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		if (prefs != null) {
			return prefs.getBoolean(HAS_SENT_WARNING_EXPIRE_NOTIFICATION + appWidgetId, false);
		}
		
    	return false;
    }
    
    public void setSentWarningExpireNotification(Context context, int appWidgetId){
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(HAS_SENT_WARNING_EXPIRE_NOTIFICATION + appWidgetId, true);
        prefs.commit();
    }
    
    public void clearSentWarningExpireNotification(Context context, int appWidgetId){
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(HAS_SENT_WARNING_EXPIRE_NOTIFICATION + appWidgetId, false);
        prefs.commit();
    }
    
    public String getPhoneNumber(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		if (prefs != null) {
			String value = prefs.getString(PHONE_NAME_KEY + appWidgetId, null);
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
    
    public int getWarningDays(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		if (prefs != null) {
			return prefs.getInt(WARNING_DAYS_KEY + appWidgetId, 0);
		} else {
			return 0;
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
