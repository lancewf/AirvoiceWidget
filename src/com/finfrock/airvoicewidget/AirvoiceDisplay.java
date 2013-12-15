package com.finfrock.airvoicewidget;

import java.text.DecimalFormat;

import com.finfrock.airvoicewidget.plans.PayAsYouGoPlan;
import com.finfrock.airvoicewidget.plans.Plan;
import com.finfrock.airvoicewidget.plans.TenDollarPlan;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.RemoteViews;

public class AirvoiceDisplay extends AppWidgetProvider {
	public static final String WIDGET_IDS_KEY = "mywidgetproviderwidgetids";
	
	private static final String NO_DATA_FOUND_TAG = "---";
	private static final String MONEY_DISPLAY_TYPE = "Money";
	private static final String DATA_DISPLAY_TYPE = "Data";
	private static final String MINUTES_DISPLAY_TYPE = "Minutes";
	
	// -------------------------------------------------------------------------
	// Private Data
	// -------------------------------------------------------------------------

	private SharedStorage sharedStorage = new SharedStorage();

	// -------------------------------------------------------------------------
	// AppWidgetProvider Members
	// -------------------------------------------------------------------------

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.hasExtra(WIDGET_IDS_KEY)) {
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
	}
	
	private void update(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		for (int appWidgetId : appWidgetIds) {
			String phoneNumber = sharedStorage.getPhoneNumber(
					context, appWidgetId);
			String displayType = sharedStorage.getDisplayType(context,
					appWidgetId);
			
			startBackgroundRequest(context, appWidgetId, 
					appWidgetManager, appWidgetIds, phoneNumber, displayType);
			
//			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
//					R.layout.main);
//
//			remoteViews.setTextViewText(R.id.dataTextView, NO_DATA_FOUND_TAG);
//			remoteViews.setTextViewText(R.id.dateText, MISSING_DATE);
//
//			appWidgetManager.updateAppWidget(appWidgetId,
//					remoteViews);
		}
	}
	
	/**
	 * A background thread is created because any network request needs to be off the main thread
	 */
	private void startBackgroundRequest(Context context, int appWidgetId, 
			AppWidgetManager appWidgetManager, int[] appWidgetIds, String phoneNumber,
			String displayType){
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

					RawData rawData = getRawData(phoneNumber);
					if(rawData != null){
						storage.widgetText = getTextForWidget(rawData, displayType);
						storage.dateText = "exp: " + rawData.expireDate;
						storage.error = false;
					}
					else{
						storage.error = true;
					}

					return storage;
				} catch (Exception e) {
					return null;
				}
			}

			protected void onPostExecute(Storage storage) {
				if (!storage.error) {
					RemoteViews remoteViews = buildRemoteViews(storage.context,
							storage.widgetText, storage.dateText,
							storage.appWidgetIds, storage.appWidgetId);

					storage.appWidgetManager.updateAppWidget(
							storage.appWidgetId, remoteViews);
				}
			}
		}).execute(context, appWidgetId, appWidgetManager, appWidgetIds, phoneNumber, displayType);
	}
	
	private RemoteViews buildRemoteViews(Context context, String widgetText, String dateText,
			int[] appWidgetIds, int appWidgetId) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.main);

		remoteViews.setTextViewText(R.id.dataTextView, widgetText);
		remoteViews.setTextViewText(R.id.dateText, dateText);

		Intent intent = new Intent(context, AirvoiceDisplay.class);

		Uri data = Uri.withAppendedPath(Uri.parse("av" + "://widget/id/"),
				String.valueOf(appWidgetId));
		intent.setData(data);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		remoteViews.setOnClickPendingIntent(R.id.LinearLayout01, pendingIntent);
		remoteViews.setOnClickPendingIntent(R.id.dataTextView, pendingIntent);

		return remoteViews;
	}
	
	private String getTextForWidget(RawData rawData, String displayType) {

		String text = NO_DATA_FOUND_TAG;
		
		if (rawData != null) {
			if (displayType.equals(MONEY_DISPLAY_TYPE)) {
				DecimalFormat df = new DecimalFormat("#.00");
				text = df.format(rawData.dollarValue);
			} else if (displayType.equals(DATA_DISPLAY_TYPE)) {
				double mbs = rawData.dollarValue / rawData.plan.getCostPerMb();
				DecimalFormat df = new DecimalFormat("#.00");
				text = df.format(mbs);
			} else if (displayType.equals(MINUTES_DISPLAY_TYPE)) {
				double mins = rawData.dollarValue / rawData.plan.getCostPerMinute();
				text = Math.round(mins) + "";
			}
		}
		return text;
	}
	
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
				} else{
					value.plan = new TenDollarPlan();
				}
			}
		} catch (Exception ex) {
			System.out.println("in catch: " + ex.getMessage());
			
		}

		return value;
	}
	
	static class RawData{
		public double dollarValue;
		public String expireDate;
		public Plan plan;
	}
	
	private HttpPart[] buildHttpParts(String phoneNumber){
		HttpPart[] httpParts = new HttpPart[4];

		httpParts[0] = new HttpPart();
		httpParts[0].setName("btnAccountDetails");
		httpParts[0].setValue("View Account Info");

		httpParts[1] = new HttpPart();
		httpParts[1].setName("txtSubscriberNumber");
		httpParts[1].setValue(phoneNumber);

		httpParts[2] = new HttpPart();
		httpParts[2].setName("__VIEWSTATE");
		httpParts[2]
				.setValue("/wEPDwUKMTMyNzk0MjM0Mw9kFgICAw9kFgICAQ8PFgIeBFRleHRlZGRkvyRh51hMIBRDFQVUDTRD12xhu6E=");

		httpParts[3] = new HttpPart();
		httpParts[3].setName("__EVENTVALIDATION");
		httpParts[3]
				.setValue("/wEWBQKrt4UNAvXNlYYNAviw+NENAv/60p0OArP42eoCM18TGGMql5Z58olLEz4T3pkwlMg=");
		
		return httpParts;
	}

	private String getValueInHtml(String html, String id) {
		int htmlValue = html.lastIndexOf(id);

		int startIndex = html.indexOf(">", htmlValue) + 1;
		int endIndex = html.indexOf("<", htmlValue);

		return html.substring(startIndex, endIndex);
	}
}
