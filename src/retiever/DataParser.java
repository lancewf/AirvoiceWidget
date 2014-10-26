package retiever;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.finfrock.airvoicewidget2.plans.PayAsYouGoPlan;
import com.finfrock.airvoicewidget2.plans.TenDollarPlan;
import com.finfrock.airvoicewidget2.plans.UnlimitedPlan;

public class DataParser {
	
	private SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
	/*
	 * 
	 */
	public RawAirvoiceData parseRawData(String rawData) {
		RawAirvoiceData value = null;
		try {
			String dollarValueAmountLeft = getValueInHtml(rawData,
					"mainAccountBalance");

			String expireDate = getValueInHtml(rawData, "airTimeExpirationDate");

			String ratePlan = getValueInHtml(rawData, "ratePlan");

			if (dollarValueAmountLeft.contains("$")) {
				value = new RawAirvoiceData();
				value.dollarValue = Double.parseDouble(dollarValueAmountLeft
						.replace("$", ""));
				value.expireDate = expireDate;
				value.expireCalendar = parseToCalendar(expireDate);

				if (ratePlan.contains("PAY AS YOU GO")) {
					value.plan = new PayAsYouGoPlan();
				} else if (ratePlan.contains("250 TALK OR 500 TEXT 30 DAYS")) {
					value.plan = new TenDollarPlan();
				} else if (ratePlan.contains("UNLIMITED")) {
					value.plan = new UnlimitedPlan();
				} else {
					return null;
				}
			}
		} catch (Exception ex) {
			Log.i("error", ex.getMessage());
		}

		return value;
	}
	
	private Calendar parseToCalendar(String text) {
		try {
			Date date = parser.parse(text);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		} catch (Exception ex) {
			Log.i("error", ex.getMessage());
			return null;
		}
	}

	private String getValueInHtml(String html, String id) {
		int htmlValue = html.lastIndexOf(id);

		int startIndex = html.indexOf(">", htmlValue) + 1;
		int endIndex = html.indexOf("<", htmlValue);

		return html.substring(startIndex, endIndex);
	}
}
