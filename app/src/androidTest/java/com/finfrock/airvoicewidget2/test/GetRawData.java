package com.finfrock.airvoicewidget2.test;


import retiever.DataParser;
import retiever.DataRetiever;
import retiever.RawAirvoiceData;

import junit.framework.TestCase;

public class GetRawData extends TestCase {
	public void test1() throws Exception{
		DataRetiever dataRetiever = new DataRetiever();
		String phoneNumber = "3606329882";
		String rawData = dataRetiever.getRawData(phoneNumber);
		
		DataParser dataParser = new DataParser();
		RawAirvoiceData rawAirvoiceData = dataParser.parseRawData(phoneNumber, rawData);
		
		if(rawAirvoiceData == null){
			System.out.println("is null");
		} else{
			System.out.println("not null");
			System.out.println(rawAirvoiceData);
		}
		
	}
}
