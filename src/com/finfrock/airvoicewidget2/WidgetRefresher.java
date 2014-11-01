package com.finfrock.airvoicewidget2;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.finfrock.airvoicewidget2.plans.Plan;
import com.finfrock.airvoicewidget2.plans.PlanFactory;

import retiever.RawAirvoiceData;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetRefresher {

	private SharedStorage sharedStorage = new SharedStorage();
	private CacheStorage cacheStorage = new CacheStorage();
	private PlanFactory planFactory = new PlanFactory();
	private SimpleDateFormat formate = new SimpleDateFormat("MM/dd/yyyy");

	private class WidgetUpdateData {
		public String valueText;
		public String dateText;
		public int textAmountColor;
		public int textDateColor;
	}

	public void updateWidgetFromCache(Context context, int appWidgetId) {
		if (cacheStorage.isThereCachedData(context, appWidgetId)) {
			try {
				RawAirvoiceData rawAirvoiceData = cacheStorage
						.getRawAirvoiceData(context, appWidgetId);
				updateWidget(rawAirvoiceData, context, appWidgetId);
			} catch (Exception ex) {
				Log.i("error", "error in DataRetiever: " + ex.getMessage());
			}
		}
	}

	public void updateWidget(RawAirvoiceData rawData, Context context,
			int appWidgetId) {
		if (rawData != null) {
			try {
				Plan plan = planFactory.createPlan(rawData.getPlanText());
				if (plan != null) {

					WidgetUpdateData widgetUpdateData = createWidgetUpdateData(
							rawData, context, appWidgetId, plan);

					writeOnWidget(context, appWidgetId, widgetUpdateData);
				}
			} catch (Exception ex) {
				Log.e("error", "error in DataRetiever: " + ex.getMessage());
			}
		}
	}

	private void writeOnWidget(Context context, int appWidgetId,
			WidgetUpdateData widgetUpdateData) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.main);

		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);

		remoteViews.setTextViewText(R.id.dataTextView,
				widgetUpdateData.valueText);
		remoteViews.setTextViewText(R.id.dateText, widgetUpdateData.dateText);

		remoteViews.setTextColor(R.id.dataTextView,
				widgetUpdateData.textAmountColor);

		remoteViews.setInt(R.id.dateText, "setBackgroundColor",
				widgetUpdateData.textDateColor);

		String name = sharedStorage.getNameLabel(context, appWidgetId);
		remoteViews.setTextViewText(R.id.nameLabel, name);

		addWidgetEditClick(remoteViews, context, appWidgetId);

		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}

	private void addWidgetEditClick(RemoteViews remoteViews, Context context,
			int appWidgetId) {
		Intent intent = new Intent();
		intent.setClassName("com.finfrock.airvoicewidget2",
				"com.finfrock.airvoicewidget2.AirvoiceWidgetEdit");
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

		PendingIntent pendingIntent2 = PendingIntent.getActivity(context,
				appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.dataTextView, pendingIntent2);
		remoteViews
				.setOnClickPendingIntent(R.id.LinearLayout01, pendingIntent2);
	}

	private WidgetUpdateData createWidgetUpdateData(RawAirvoiceData rawData,
			final Context context, final int appWidgetId, Plan plan) {

		WidgetUpdateData widgetUpdateData = new WidgetUpdateData();

		String displayType = sharedStorage.getDisplayType(context, appWidgetId);
		int warningLimit = sharedStorage.getWarningLimit(context, appWidgetId);
		int warningDays = sharedStorage.getWarningDays(context, appWidgetId);

		float amount = plan.getAmount(rawData.getDollarValue(), displayType);
		widgetUpdateData.valueText = plan.getTextForWidget(amount, displayType);
		widgetUpdateData.dateText = "exp: "
				+ formate.format(rawData.getExpireCalendar().getTime());
		int daysToExpire = calcDaysToExpire(rawData.getExpireCalendar());

		if (daysToExpire > warningDays) {
			widgetUpdateData.textDateColor = Color.GRAY;
			sharedStorage.clearSentWarningExpireNotification(context,
					appWidgetId);
		} else {
			notifyWarningExpire(context, appWidgetId, warningDays);
			widgetUpdateData.textDateColor = Color.RED;
		}

		if (amount < warningLimit) {
			notifyWarningAmount(context, appWidgetId, rawData, warningLimit,
					displayType);
			widgetUpdateData.textAmountColor = Color.RED;
		} else if (isDataOld(context, appWidgetId)) {
			widgetUpdateData.textAmountColor = Color.GRAY;
		} else {
			sharedStorage.clearSentWarningAmountNotification(context,
					appWidgetId);
			widgetUpdateData.textAmountColor = Color.BLACK;
		}

		return widgetUpdateData;
	}

	private Boolean isDataOld(final Context context, final int appWidgetId) {
		if (cacheStorage.isThereCachedData(context, appWidgetId)) {
			Calendar lastUpdate = cacheStorage.getLastUpdatedDate(context,
					appWidgetId);
			Calendar oneHoursAgo = Calendar.getInstance();
			oneHoursAgo.add(Calendar.HOUR_OF_DAY, -1);

			if (lastUpdate.before(oneHoursAgo)) {
				return true;
			}
		}

		return false;
	}

	private void notifyWarningAmount(Context context, int appWidgetId,
			RawAirvoiceData rawData, int warningLimit, String displayType) {
		if (!sharedStorage.hasSentWarningAmountNotification(context,
				appWidgetId)) {
			String name = sharedStorage.getNameLabel(context, appWidgetId);
			Plan plan = planFactory.createPlan(rawData.getPlanText());
			if (plan != null) {
				String message = name + " is below "
						+ plan.getTextForWidget(warningLimit, displayType);
				String title = "Below Limit";
				sendNotification(title, message, context);
				sharedStorage.setSentWarningAmountNotification(context,
						appWidgetId);
			}
		}
	}

	private void notifyWarningExpire(Context context, int appWidgetId,
			int daysWarningExpire) {
		if (!sharedStorage.hasSentWarningExpireNotification(context,
				appWidgetId)) {
			String name = sharedStorage.getNameLabel(context, appWidgetId);
			String message = name + " expires in less than "
					+ daysWarningExpire + " days";
			String title = "Expiration Warning";
			sendNotification(title, message, context);
			sharedStorage
					.setSentWarningExpireNotification(context, appWidgetId);
		}
	}

	private void sendNotification(String title, String message, Context context) {

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context);

		mBuilder.setContentTitle(title).setContentText(message)
				.setSmallIcon(R.drawable.ic_launcher)
				.setDefaults(Notification.DEFAULT_SOUND);

		mNotificationManager.notify(0, mBuilder.build());
	}

	private int calcDaysToExpire(Calendar expireDate) {
		Calendar now = Calendar.getInstance();

		long diff = expireDate.getTimeInMillis() - now.getTimeInMillis();

		return (int) Math.round(diff / (24 * 60 * 60 * 1000)) + 1;
	}
}
