package com.finfrock.airvoicewidget2;

import retiever.DataRetiever;

import android.content.Intent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AirvoiceDisplay extends AppWidgetProvider {
	public static final String WIDGET_IDS_KEY = "mywidgetproviderwidgetids";
    
	
	// -------------------------------------------------------------------------
	// Private Data
	// -------------------------------------------------------------------------

	private SharedStorage sharedStorage = new SharedStorage();
	private WidgetRefresher widgetRefresher = new WidgetRefresher();

	// -------------------------------------------------------------------------
	// AppWidgetProvider Members
	// -------------------------------------------------------------------------

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (intent.hasExtra(WIDGET_IDS_KEY)) {
				int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
				onUpdate(context, AppWidgetManager.getInstance(context), ids);
			} else {
				super.onReceive(context, intent);
			}
		} catch (Exception e) {
			Log.w("error", "error in background thread " + e.getMessage());
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
        
		try {
			update(context, appWidgetIds);
		} catch (Exception e) {
			Log.w("error", "error in background thread " + e.getMessage());
		}
	}
	
	// -------------------------------------------------------------------------
	// Private Members
	// -------------------------------------------------------------------------
	
	private void update(Context context, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			widgetRefresher.updateWidgetFromCache(context, appWidgetId);
			
			updateFromAirvoice(context, appWidgetId);
		}
	}
	
	/**
	 * A background thread is created because any network request needs to be off the UI thread
	 */
	private void updateFromAirvoice(final Context context, final int appWidgetId){
		(new AsyncTask<Object, Void, String>(){
			protected String doInBackground(Object... args) {
				try {
					String phoneNumber = sharedStorage.getPhoneNumber(
							context, appWidgetId);

					return getRawData(phoneNumber);
				} catch (Exception e) {
					Log.w("error", "error in background thread " + e.getMessage() + " " + e.getLocalizedMessage());
					return null;
				}
			}

			protected void onPostExecute(String rawDataString) {
				if(rawDataString == null){
					widgetRefresher.updateWidgetFromCache(context, appWidgetId);
				} else{
					widgetRefresher.updateWidget(rawDataString, context, appWidgetId);
				}
			}
			
			private String getRawData(String phoneNumber){
				String rawDataString = null;
				if(phoneNumber != null){
					DataRetiever dataRetiever = new DataRetiever();
					rawDataString = dataRetiever.getRawData(phoneNumber);
				}
				
				return rawDataString;
			}
		}).execute();
	}
	

}
