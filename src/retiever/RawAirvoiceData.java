package retiever;

import java.util.Calendar;

public class RawAirvoiceData {
	
	public RawAirvoiceData(float dollarValue, Calendar expireCalendar, 
			String planText, Calendar date){
		this.dollarValue = dollarValue;
		this.expireCalendar = expireCalendar;
		this.planText = planText;
		this.date = date;
	}
	
	private float dollarValue;
	private Calendar expireCalendar;
	private String planText;
	private Calendar date;
	
	public float getDollarValue(){return dollarValue;}
	public String getPlanText(){return planText;}
	public Calendar getExpireCalendar(){return expireCalendar;}
	public Calendar getDate(){return date;}
	
	public String toString(){
		return "RawAirvoiceData(" + dollarValue + ", " + planText + ")";
	}
}
