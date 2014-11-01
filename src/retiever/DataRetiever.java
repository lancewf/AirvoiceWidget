package retiever;

import android.util.Log;

public class DataRetiever {

	public String getRawData(String phoneNumber) {
		String result = null;
		try {
			HttpRetiever httpSender = new HttpRetiever();

			HttpPart[] httpParts = buildHttpParts(phoneNumber);

			result = httpSender
					.sendPostMessage(
							"http://csi.airvoicewireless.com/ericssonTpspApiPublic.aspx",
							httpParts);
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e("error", "error in DataRetiever: " + ex.getMessage());
		}

		return result;
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
		int htmlValueIndex = html.lastIndexOf("\"" + id + "\"");
		
		int indexOfValueTag = html.indexOf("value=", htmlValueIndex) + 7;
		
		int indexOfEndOfValue = html.indexOf("\"", indexOfValueTag);

		return html.substring(indexOfValueTag, indexOfEndOfValue);
	}
}
