package com.finfrock.airvoicewidget;

import com.finfrock.airvoicewidget.R;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class AirvoiceDisplay extends AppWidgetProvider {
	public static final String WIDGET_IDS_KEY ="mywidgetproviderwidgetids";
	public static final String WIDGET_DATA_KEY ="mywidgetproviderwidgetdata";
	
	public static final String MONEY_DISPLAY_TYPE = "Money";
	public static final String DATA_DISPLAY_TYPE = "Data";
	public static final String MINUTES_DISPLAY_TYPE = "Minutes";
	
	@Override
	public void onReceive(Context context, Intent intent) {
	    if (intent.hasExtra(WIDGET_IDS_KEY)) {
	        int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
	        this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
	    } else super.onReceive(context, intent);
	}
	    
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		update(context, appWidgetManager, appWidgetIds);
	}
	
	private void update(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds){
		for (int i = 0; i < appWidgetIds.length; i++) {
			int appWidgetId = appWidgetIds[i];
			
			String phoneNumber = 
				AirvoiceWidgetConfigure.phoneNumberPref(context, appWidgetId);
			String displayType = 
					AirvoiceWidgetConfigure.displayType(context, appWidgetId);

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.main);

			String text = "---";

			try {
				text = getData(phoneNumber, displayType);
			} catch (Exception e) {
				e.printStackTrace();
			}

			remoteViews.setTextViewText(R.id.dataTextView, text);

			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
	}

	private String getData(String phoneNumber, String displayType) throws Exception {
		HttpRetiever httpSender = new HttpRetiever();

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

		String result = httpSender.sendPostMessage(
				"http://csi.airvoicewireless.com/ericssonTpspApiPublic.aspx",
				httpParts);

		String costLeft = getValueInHtml(result, "mainAccountBalance");
		
		String text = "---";
		
		if (displayType.equals(MONEY_DISPLAY_TYPE) && costLeft.contains("$")) {
			text = costLeft;
		} else if (displayType.equals(DATA_DISPLAY_TYPE)) {
			costLeft = costLeft.replace("$", "");

			try {
				double costValue = Double.parseDouble(costLeft);
				double mbs = costValue / .33;
				text = Math.round(mbs) + " MB";
			} catch (Exception e) {
			}
		} else if (displayType.equals(MINUTES_DISPLAY_TYPE)) {
			costLeft = costLeft.replace("$", "");

			try {
				double costValue = Double.parseDouble(costLeft);
				double mins = costValue / .1;
				text = Math.round(mins) + " mins";
			} catch (Exception e) {
			}
		}

		return text;
	}

	private String getValueInHtml(String html, String id) {
		int mainAccountBalanceEndIndex = html.lastIndexOf(id);

		int startIndex = html.indexOf(">", mainAccountBalanceEndIndex) + 1;
		int endIndex = html.indexOf("<", mainAccountBalanceEndIndex);

		return html.substring(startIndex, endIndex);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		System.out.println("onEnabled");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		System.out.println("onDisabled");

	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		System.out.println("onDeleted");

	}
	
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId, String phoneNumber) {
    	
        AppWidgetManager man = AppWidgetManager.getInstance(context);
        int[] ids = man.getAppWidgetIds(
                new ComponentName(context,AirvoiceDisplay.class));
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AirvoiceDisplay.WIDGET_IDS_KEY, ids);
        context.sendBroadcast(updateIntent);
    }
}
