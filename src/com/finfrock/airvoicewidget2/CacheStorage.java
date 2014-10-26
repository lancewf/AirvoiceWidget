package com.finfrock.airvoicewidget2;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheStorage {

    private static final String RAW_CACHED_KEY = "raw_cached_";
    private static final String LAST_UPDATED_KEY = "last_updated_";
    private static final String CACHE_NAME = "com.finfrock.airvoicewidget2.AirvoiceDisplay.Cache";
    
    public void saveCacheData(Context context, int appWidgetId, 
    		String rawData) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(CACHE_NAME, 0).edit();
        prefs.putString(RAW_CACHED_KEY + appWidgetId, rawData);
        prefs.putLong(LAST_UPDATED_KEY + appWidgetId, Calendar.getInstance().getTimeInMillis());
        prefs.commit();
    }
    
    public String getRawCachedValue(Context context, int appWidgetId){
        SharedPreferences prefs = context.getSharedPreferences(CACHE_NAME, 0);
		if (prefs != null) {
			return prefs.getString(RAW_CACHED_KEY + appWidgetId, null);
		} else {
			return null;
		}
    }
    
    public Calendar getLastUpdatedDate(Context context, int appWidgetId){
        SharedPreferences prefs = context.getSharedPreferences(CACHE_NAME, 0);
		if (prefs != null) {
			Long timeInMillis = prefs.getLong(LAST_UPDATED_KEY + appWidgetId, -1);
			
			if(timeInMillis == -1){
				return null;
			} else{
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(timeInMillis);
				return calendar;
			}
		} else {
			return null;
		}
    }
    
    public Boolean isThereCachedData(Context context, int appWidgetId){
        SharedPreferences prefs = context.getSharedPreferences(CACHE_NAME, 0);
		if (prefs != null && prefs.getString(RAW_CACHED_KEY + appWidgetId, null) != null) {
			return true;
		} else {
			return false;
		}
    }
}
