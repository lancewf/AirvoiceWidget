package retiever;

import android.util.Log;

import com.finfrock.airvoicewidget2.plans.PayAsYouGoPlan;
import com.finfrock.airvoicewidget2.plans.TenDollarPlan;
import com.finfrock.airvoicewidget2.plans.UnknownPlan;
import com.finfrock.airvoicewidget2.plans.UnlimitedPlan;

public class DataRetiever {
	/*
	 * 
	 */
	public RawAirvoiceData getRawData(String phoneNumber) {
		RawAirvoiceData value = null;
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
				value = new RawAirvoiceData();
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
