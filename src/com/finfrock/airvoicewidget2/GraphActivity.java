package com.finfrock.airvoicewidget2;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retiever.RawAirvoiceData;

import com.finfrock.airvoicewidget2.data.AmountDbAdapter;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class GraphActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph);
		
		String phoneNumber = getIntent().getExtras().getString("phoneNumber");
		String name = getIntent().getExtras().getString("name");
		List<RawAirvoiceData> rawAirvoiceDatas = getRawAirvoiceDatas(phoneNumber);
        
        GraphViewData[] graphViewDatas = new GraphViewData[rawAirvoiceDatas.size()];
        for(int index = 0; index < rawAirvoiceDatas.size(); index++){
        	RawAirvoiceData rawAirvoiceData = rawAirvoiceDatas.get(index);
        	Calendar observedDate = rawAirvoiceData.getObservedDate();
        	
        	graphViewDatas[index] = 
        			new GraphViewData(observedDate.getTimeInMillis(), 
        					rawAirvoiceData.getDollarValue());
        }

		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries(graphViewDatas);
		 
		GraphView graphView = new LineGraphView(this, name);
		((LineGraphView) graphView).setDrawDataPoints(true);
        ((LineGraphView) graphView).setDataPointsRadius(15f);
		graphView.addSeries(exampleSeries); // data
		int day = 1000*60*60*24;
//		graphView.setViewPort(day * 5, day * 7);
		graphView.setScrollable(true);
		graphView.setScalable(true);
		
		final String datePattern = findCorrectDatePattern(rawAirvoiceDatas);
		
        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
        	private DecimalFormat df = new DecimalFormat("#.00");
        	private SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        	
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    Date d = new Date((long) value);
                    return dateFormat.format(d);
                } else{
                	return "$" + df.format(value);
                }
            }
        });
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph_view);
		layout.addView(graphView);
	}
	
	private List<RawAirvoiceData> getRawAirvoiceDatas(String phoneNumber){
        AmountDbAdapter amountDbAdapter = new AmountDbAdapter(this);
        amountDbAdapter.open();
        List<RawAirvoiceData> rawAirvoiceDatas = amountDbAdapter.getAllRawAirvoiceDatas(phoneNumber);
        amountDbAdapter.close();
        
        return rawAirvoiceDatas;
	}
	
	private String findCorrectDatePattern(List<RawAirvoiceData> rawAirvoiceDatas){
		if(rawAirvoiceDatas.size() > 0){
		Calendar startDate = rawAirvoiceDatas.get(0).getObservedDate();
		Calendar endDate = rawAirvoiceDatas.get(rawAirvoiceDatas.size() -1).getObservedDate();
		
		long dateDiffInHours = Math.abs(startDate.getTimeInMillis() - endDate.getTimeInMillis())/1000/60/60;
		
		String datePattern = "";
			if (dateDiffInHours < 3 * 24) {
				datePattern = "MMM d HH:mm";
			} else if (dateDiffInHours < 45 * 24) {
				datePattern = "MMM d";
			} else {
				datePattern = "yy/MM";
			}

			return datePattern;
		} else {
			return "MMM d HH:mm";
		}
	}
}
