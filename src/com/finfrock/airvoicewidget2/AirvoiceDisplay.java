package com.finfrock.airvoicewidget2;

import java.util.Calendar;

import retiever.DataParser;
import retiever.DataRetiever;
import retiever.RawAirvoiceData;

import com.finfrock.airvoicewidget2.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.util.Log;

public class AirvoiceDisplay extends AppWidgetProvider {
	public static final String WIDGET_IDS_KEY = "mywidgetproviderwidgetids";
	
	public static final String NO_DATA_FOUND_TAG = "---";
	public static final String MONEY_DISPLAY_TYPE = "Money";
	public static final String DATA_DISPLAY_TYPE = "Data";
	public static final String MINUTES_DISPLAY_TYPE = "Minutes";
    public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";
    
	
	// -------------------------------------------------------------------------
	// Private Data
	// -------------------------------------------------------------------------

	private SharedStorage sharedStorage = new SharedStorage();
	private CacheStorage cacheStorage = new CacheStorage();

	// -------------------------------------------------------------------------
	// AppWidgetProvider Members
	// -------------------------------------------------------------------------

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.hasExtra(WIDGET_IDS_KEY)){
			int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
			onUpdate(context, AppWidgetManager.getInstance(context), ids);
		} else {
			super.onReceive(context, intent);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
        
		update(context, appWidgetManager, appWidgetIds);
	}
	
	// -------------------------------------------------------------------------
	// Private Members
	// -------------------------------------------------------------------------

	/**
	 * Used for the background thread request
	 * any network request needs to be off the main thread
	 */
	private class WidgetUpdateData{
		public String valueText;
		public String dateText;
		public Boolean error;
		public int textAmountColor;
		public int textDateColor;
	}
	
	private void update(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		for (int appWidgetId : appWidgetIds) {
			updateWithCachedData(context, appWidgetManager, appWidgetId);
			
			updateDynamicData(context, appWidgetId, appWidgetManager);
		}
	}
	
	/**
	 * This is needed because something there is a long wait. 
	 * @param context
	 * @param appWidgetManager
	 * @param appWidgetId
	 */
	private void updateWithCachedData(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
		if (cacheStorage.isThereCachedData(context, appWidgetId)) {
			String cachedRawDataString = cacheStorage.getRawCachedValue(
					context, appWidgetId);
			WidgetUpdateData widgetUpdateData = createWidgetUpdateData(
					cachedRawDataString, context, appWidgetId);

			writeOnWidget(context, appWidgetId, appWidgetManager,
					widgetUpdateData);
		}
	}
	
	private void addWidgetEditClick(RemoteViews remoteViews, Context context, int appWidgetId){
		Intent intent = new Intent();
		intent.setClassName("com.finfrock.airvoicewidget2", 
				"com.finfrock.airvoicewidget2.AirvoiceWidgetEdit");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.dataTextView, pendingIntent2);
        remoteViews.setOnClickPendingIntent(R.id.LinearLayout01, pendingIntent2);
	}
	
	private void writeOnWidget(Context context, int appWidgetId, AppWidgetManager appWidgetManager,
			WidgetUpdateData widgetUpdateData){
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.main);

		if(widgetUpdateData != null){
			if (!widgetUpdateData.error) {
				remoteViews.setTextViewText(R.id.dataTextView,
						widgetUpdateData.valueText);
				remoteViews.setTextViewText(R.id.dateText,
						widgetUpdateData.dateText);
			}
			
			remoteViews.setTextColor(R.id.dataTextView,
					widgetUpdateData.textAmountColor);
			
			remoteViews.setInt(R.id.dateText, "setBackgroundColor",
					widgetUpdateData.textDateColor);
		}

        String name = sharedStorage.getNameLabel(context, appWidgetId);
		remoteViews.setTextViewText(R.id.nameLabel, name);
		
		addWidgetEditClick(remoteViews, context, appWidgetId );
        
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}
	
	private int calcDaysToExpire(Calendar expireDate){
		Calendar now = Calendar.getInstance();
		
		long diff = expireDate.getTimeInMillis() - now.getTimeInMillis();
		
		return (int) (diff / (24 * 60 * 60 * 1000));
	}
	
	private WidgetUpdateData createWidgetUpdateData(String rawDataString, 
			final Context context, final int appWidgetId){

		WidgetUpdateData widgetUpdateData = new WidgetUpdateData();
		
		String displayType = sharedStorage.getDisplayType(context,
				appWidgetId);
		int warningLimit = sharedStorage.getWarningLimit(context,
				appWidgetId);
		int warningDays = sharedStorage.getWarningDays(context,
				appWidgetId);
		
		DataParser dataParser = new DataParser();
		RawAirvoiceData rawData = dataParser.parseRawData(rawDataString);
		widgetUpdateData.valueText = rawData.plan.getTextForWidget(rawData.dollarValue, displayType);
		widgetUpdateData.dateText = "exp: " + rawData.expireDate;
		widgetUpdateData.error = false;
		double amount = rawData.plan.getAmount(rawData.dollarValue, displayType);
		int daysToExpire = calcDaysToExpire(rawData.expireCalendar);
		
		if(daysToExpire > warningDays){
			widgetUpdateData.textDateColor = Color.GRAY;
		} else{
			widgetUpdateData.textDateColor = Color.RED;
		}
		
		if(amount > warningLimit){
			widgetUpdateData.textAmountColor = Color.BLACK;
		} else{
			widgetUpdateData.textAmountColor = Color.RED;
		}
		
		return widgetUpdateData;
	}
	
	/**
	 * A background thread is created because any network request needs to be off the UI thread
	 */
	private void updateDynamicData(final Context context, final int appWidgetId, 
			final AppWidgetManager appWidgetManager){
		(new AsyncTask<Object, Void, WidgetUpdateData>(){
			protected WidgetUpdateData doInBackground(Object... args) {
				try {
					WidgetUpdateData widgetUpdateData = null;
					String phoneNumber = sharedStorage.getPhoneNumber(
							context, appWidgetId);
					String rawDataString = getRawData(phoneNumber);
					if(rawDataString != null){
						cacheStorage.saveCacheData(context, appWidgetId, rawDataString);
						
						widgetUpdateData = createWidgetUpdateData(rawDataString, context, appWidgetId);
					}
					else{
						if(cacheStorage.isThereCachedData(context, appWidgetId)){
							String cachedRawDataString = cacheStorage.getRawCachedValue(context, appWidgetId);
							widgetUpdateData = createWidgetUpdateData(cachedRawDataString, context, appWidgetId);
						} else{
							widgetUpdateData = new WidgetUpdateData();
							widgetUpdateData.error = true;
						}
						
						widgetUpdateData.textAmountColor = Color.GRAY;
					}

					return widgetUpdateData;
				} catch (Exception e) {
					Log.w("error", "error in background thread " + e.getMessage() + " " + e.getLocalizedMessage());
					return null;
				}
			}

			protected void onPostExecute(WidgetUpdateData widgetUpdateData) {
				writeOnWidget(context, appWidgetId, appWidgetManager,
						widgetUpdateData);
			}
		}).execute();
	}
	
	private String getRawData(String phoneNumber){
		String rawDataString = null;
		if(phoneNumber != null){
			DataRetiever dataRetiever = new DataRetiever();
			rawDataString = dataRetiever.getRawData(phoneNumber);
		}
		
		return rawDataString;
	}
}
