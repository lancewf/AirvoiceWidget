package com.finfrock.airvoicewidget2;

import com.finfrock.airvoicewidget2.R;
import com.finfrock.airvoicewidget2.plans.PayAsYouGoPlan;
import com.finfrock.airvoicewidget2.plans.Plan;
import com.finfrock.airvoicewidget2.plans.TenDollarPlan;
import com.finfrock.airvoicewidget2.plans.UnknownPlan;
import com.finfrock.airvoicewidget2.plans.UnlimitedPlan;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
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
		if (intent.getAction().equalsIgnoreCase(TOAST_ACTION)) {
			Log.i("info", "bullshit");
			Intent editIntent = new Intent().setClass(context, 
                    AirvoiceWidgetEdit.class);
			int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
			editIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			context.startActivity(editIntent);
		} else if(intent.hasExtra(WIDGET_IDS_KEY)){
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
	private class Storage{
		public Context context;
		public int appWidgetId;
		public String widgetText;
		public String dateText;
		public AppWidgetManager appWidgetManager;
		public int[] appWidgetIds;
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
			
			updateName(context, appWidgetManager, appWidgetId);
			
			startBackgroundRequest(context, appWidgetId, appWidgetManager, appWidgetIds, 
					phoneNumber, displayType, warningLimit);
		}
	}
	
	private void updateName(Context context, AppWidgetManager appWidgetManager, int appWidgetId){
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.main);
        String name = sharedStorage.getNameLabel(context, appWidgetId);
		remoteViews.setTextViewText(R.id.nameLabel, name);
		
		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}
	
	/**
	 * A background thread is created because any network request needs to be off the main thread
	 */
	private void startBackgroundRequest(Context context, int appWidgetId, 
			AppWidgetManager appWidgetManager, int[] appWidgetIds, String phoneNumber,
			String displayType, int warningLimit){
		(new AsyncTask<Object, Void, Storage>(){
			protected Storage doInBackground(Object... args) {
				try {
					Storage storage = new Storage();

					storage.context = (Context) args[0];
					storage.appWidgetId = (Integer) args[1];
					storage.appWidgetManager = (AppWidgetManager) args[2];
					storage.appWidgetIds = (int[]) args[3];
					String phoneNumber = (String) args[4];
					String displayType = (String) args[5];
					int warningLimit = (Integer) args[6];

					RawData rawData = getRawData(phoneNumber);
					if(rawData != null){
						storage.widgetText = rawData.plan.getTextForWidget(rawData.dollarValue, displayType);
						storage.dateText = "exp: " + rawData.expireDate;
						storage.error = false;
						double amount = rawData.plan.getAmount(rawData.dollarValue, displayType);
						if(amount > warningLimit){
							storage.textColor = Color.BLACK;
						} else{
							storage.textColor = Color.RED;
						}
					}
					else{
						storage.error = true;
						storage.textColor = Color.GREEN;
					}

					return storage;
				} catch (Exception e) {
					return null;
				}
			}

			protected void onPostExecute(Storage storage) {
				if (!storage.error) {
					updateWidget(storage.context,
							storage.widgetText, storage.dateText, storage.textColor,
							storage.appWidgetIds, storage.appWidgetId, storage.appWidgetManager);
				}
			}
		}).execute(context, appWidgetId, appWidgetManager, appWidgetIds, phoneNumber, displayType, warningLimit);
	}
	
	private void updateWidget(Context context, String widgetText, String dateText, int textColor,
			int[] appWidgetIds, int appWidgetId, AppWidgetManager appWidgetManager ) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.main);

		remoteViews.setTextViewText(R.id.dataTextView, widgetText);
		remoteViews.setTextViewText(R.id.dateText, dateText);
		remoteViews.setTextColor(R.id.dataTextView, textColor);

        // When we click the widget, we want to open our main activity.
        Intent defineIntent2 = new Intent(context, AirvoiceWidgetEdit.class);
        defineIntent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, defineIntent2, 0);
        remoteViews.setOnClickPendingIntent(R.id.dataTextView, pendingIntent2);
        remoteViews.setOnClickPendingIntent(R.id.LinearLayout01, pendingIntent2);
        
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}
	
	/*
	 * 
	 */
	private RawData getRawData(String phoneNumber) {
		RawData value = null;
		try {
			HttpRetiever httpSender = new HttpRetiever();

			HttpPart[] httpParts = buildHttpParts(phoneNumber);

			String result = httpSender.sendPostMessage(
					"http://csi.airvoicewireless.com/ericssonTpspApiPublic.aspx",
							httpParts);

			String dollarValueAmountLeft = getValueInHtml(result,
					"mainAccountBalance");
			
			String expireDate = getValueInHtml(result,
					"airTimeExpirationDate");
			
			String ratePlan = getValueInHtml(result,
					"ratePlan");

			if (dollarValueAmountLeft.contains("$")) {
				value = new RawData();
				value.dollarValue = Double.parseDouble(dollarValueAmountLeft.replace("$", ""));
				value.expireDate = expireDate;
				
				if(ratePlan.contains("PAY AS YOU GO")){
					value.plan = new PayAsYouGoPlan();
				} else if(ratePlan.contains("250 TALK OR 500 TEXT 30 DAYS")){
					value.plan = new TenDollarPlan();
				} else if(ratePlan.contains("UNLIMITED")){
					value.plan = new UnlimitedPlan();
				} else {
					value.plan = new UnknownPlan();
				}
			}
		} catch (Exception ex) {
			Log.i("error", ex.getMessage());
		}

		return value;
	}
	
	static class RawData{
		public double dollarValue;
		public String expireDate;
		public Plan plan;
	}
	
	private HttpPart[] buildHttpParts(String phoneNumber) throws Exception{
		HttpPart[] httpParts = new HttpPart[4];
		
		String[] data = getViewstateKey();

		httpParts[0] = new HttpPart();
		httpParts[0].setName("btnAccountDetails");
		httpParts[0].setValue("View Account Info");

		httpParts[1] = new HttpPart();
		httpParts[1].setName("txtSubscriberNumber");
		httpParts[1].setValue(phoneNumber);

		httpParts[2] = new HttpPart();
		httpParts[2].setName("__VIEWSTATE");
		httpParts[2].setValue(data[0]);
		
		httpParts[3] = new HttpPart();
		httpParts[3].setName("__EVENTVALIDATION");
		httpParts[3].setValue(data[1]);
		
		return httpParts;
	}
	
	private String[] getViewstateKey() throws Exception{
		HttpRetiever httpSender = new HttpRetiever();
		String result = httpSender.sendPostMessage(
				"http://csi.airvoicewireless.com/ericssonTpspApiPublic.aspx",
				new HttpPart[0]);
		
		String viewstate = getValueId(result, "__VIEWSTATE");
		String eventvalidation = getValueId(result, "__EVENTVALIDATION");
		
		String[] data = new String[]{viewstate, eventvalidation};
		
		return data;
	}
	
	private String getValueId(String html, String id) {
		int htmlValueIndex = html.lastIndexOf(id);
		
		int indexOfValueTag = html.indexOf("value=", htmlValueIndex) + 7;
		
		int indexOfEndOfValue = html.indexOf("\"", indexOfValueTag);

		return html.substring(indexOfValueTag, indexOfEndOfValue);
	}

	private String getValueInHtml(String html, String id) {
		int htmlValue = html.lastIndexOf(id);

		int startIndex = html.indexOf(">", htmlValue) + 1;
		int endIndex = html.indexOf("<", htmlValue);

		return html.substring(startIndex, endIndex);
	}
}
