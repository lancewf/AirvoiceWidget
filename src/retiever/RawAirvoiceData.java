package retiever;

import java.util.Calendar;

public class RawAirvoiceData {
	
	public RawAirvoiceData(String phoneNumber, float dollarValue, 
			Calendar expireDate, String planText, Calendar observedDate){
		this.dollarValue = dollarValue;
		this.expireDate = expireDate;
		this.planText = planText;
		this.observedDate = observedDate;
		this.phoneNumber = phoneNumber;
	}
	
	private float dollarValue;
	private Calendar expireDate;
	private String planText;
	private Calendar observedDate;
	private String phoneNumber;
	
	public float getDollarValue(){return dollarValue;}
	public String getPlanText(){return planText;}
	public Calendar getExpireDate(){return expireDate;}
	public Calendar getObservedDate(){return observedDate;}
	public String getPhoneNumber(){return phoneNumber;}
	
	public String toString(){
		return "RawAirvoiceData(" + phoneNumber + "," + dollarValue + ", " 
				+ planText + ")";
	}
}
