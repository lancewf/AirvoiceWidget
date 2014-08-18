package com.finfrock.airvoicewidget2;

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
		public int textColor;
	}
	
	private void update(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		for (int appWidgetId : appWidgetIds) {
			String phoneNumber = sharedStorage.getPhoneNumber(
					context, appWidgetId);
			String displayType = sharedStorage.getDisplayType(context,
					appWidgetId);
			int warningLimit = sharedStorage.getWarningLimit(context,
					appWidgetId);
			
			updateStaticData(context, appWidgetManager, appWidgetId);
			
			updateDynamicData(context, appWidgetId, appWidgetManager, 
					phoneNumber, displayType, warningLimit);
		}
	}
	
	private void updateStaticData(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.main);
        String name = sharedStorage.getNameLabel(context, appWidgetId);
		remoteViews.setTextViewText(R.id.nameLabel, name);
		
		Intent intent = new Intent();
		intent.setClassName("com.finfrock.airvoicewidget2", 
				"com.finfrock.airvoicewidget2.AirvoiceWidgetEdit");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.dataTextView, pendingIntent2);
        remoteViews.setOnClickPendingIntent(R.id.LinearLayout01, pendingIntent2);
		
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}
	
	/**
	 * A background thread is created because any network request needs to be off the main thread
	 */
	private void updateDynamicData(final Context context, final int appWidgetId, 
			final AppWidgetManager appWidgetManager, final String phoneNumber,
			final String displayType, final int warningLimit){
		(new AsyncTask<Object, Void, WidgetUpdateData>(){
			protected WidgetUpdateData doInBackground(Object... args) {
				try {
					WidgetUpdateData widgetUpdateData = new WidgetUpdateData();
					DataRetiever dataRetiever = new DataRetiever();
					RawAirvoiceData rawData = dataRetiever.getRawData(phoneNumber);
					if(rawData != null){
						widgetUpdateData.valueText = rawData.plan.getTextForWidget(rawData.dollarValue, displayType);
						widgetUpdateData.dateText = "exp: " + rawData.expireDate;
						widgetUpdateData.error = false;
						double amount = rawData.plan.getAmount(rawData.dollarValue, displayType);
						if(amount > warningLimit){
							widgetUpdateData.textColor = Color.BLACK;
						} else{
							widgetUpdateData.textColor = Color.RED;
						}
					}
					else{
						widgetUpdateData.error = true;
						widgetUpdateData.textColor = Color.GRAY;
					}

					return widgetUpdateData;
				} catch (Exception e) {
					return null;
				}
			}

			protected void onPostExecute(WidgetUpdateData widgetUpdateData) {
				RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
						R.layout.main);

				if (!widgetUpdateData.error) {
					remoteViews.setTextViewText(R.id.dataTextView,
							widgetUpdateData.valueText);
					remoteViews.setTextViewText(R.id.dateText,
							widgetUpdateData.dateText);
				}
				
				remoteViews.setTextColor(R.id.dataTextView,
						widgetUpdateData.textColor);
		        
		        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
			}
		}).execute();
	}
}
