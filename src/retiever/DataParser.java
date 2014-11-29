package retiever;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class DataParser {
	
	private SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
	/*
	 * 
	 */
	public RawAirvoiceData parseRawData(String phoneNumber, String rawData) {
		RawAirvoiceData value = null;
		try {
			String dollarValueAmountLeft = getValueInHtml(rawData,
					"mainAccountBalance");

			String expireDate = getValueInHtml(rawData, "airTimeExpirationDate");

			String ratePlan = getValueInHtml(rawData, "ratePlan");

			if (dollarValueAmountLeft.contains("$")) {
				
				float dollarValue = Float.parseFloat(dollarValueAmountLeft
						.replace("$", ""));
				Calendar expireCalendar = parseToCalendar(expireDate);
				String planText = ratePlan;
				Calendar date = Calendar.getInstance();
				
				value = new RawAirvoiceData(phoneNumber, dollarValue, expireCalendar, planText, date);
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
