package com.finfrock.airvoicewidget2;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retiever.RawAirvoiceData;
import android.content.Context;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

public class GraphBuilder {

	public GraphView createCurrentDayGraph(Context context, 
			List<RawAirvoiceData> rawAirvoiceDatas, String name) {
		Calendar oldest = Calendar.getInstance();
		oldest.add(Calendar.HOUR_OF_DAY, -24);
		String datePattern = "HH:mm";
		
		ArrayList<GraphViewData> filteredGraphViewDatas = new ArrayList<GraphViewData>();
		
		for (int index = 0; index < rawAirvoiceDatas.size(); index++) {
			RawAirvoiceData rawAirvoiceData = rawAirvoiceDatas.get(index);
			Calendar observedDate = rawAirvoiceData.getObservedDate();
			
			if (observedDate.after(oldest)) {
				filteredGraphViewDatas.add(new GraphViewData(observedDate
						.getTimeInMillis(), rawAirvoiceData.getDollarValue()));
			}
		}
		
		GraphViewData[] graphViewDatas = filteredGraphViewDatas.toArray(new GraphViewData[0]);
		
		if(filteredGraphViewDatas.size() < 2 && rawAirvoiceDatas.size() > 0){
			
			RawAirvoiceData rawAirvoiceData = rawAirvoiceDatas.get(0);
			GraphViewData nowGraphViewData = new GraphViewData(Calendar.getInstance()
					.getTimeInMillis(), rawAirvoiceData.getDollarValue());
			
			Calendar yesterday = Calendar.getInstance();
			yesterday.set(Calendar.DAY_OF_MONTH, -1);
			GraphViewData yesterdayGraphViewData = new GraphViewData(yesterday
					.getTimeInMillis(), rawAirvoiceData.getDollarValue());
			graphViewDatas = new GraphViewData[]{yesterdayGraphViewData, nowGraphViewData};
		} else if(filteredGraphViewDatas.size() > 0){
			graphViewDatas = filteredGraphViewDatas.toArray(new GraphViewData[0]);
		} else{
			graphViewDatas = new GraphViewData[0];
		}

		return createGraph(context, rawAirvoiceDatas, oldest, datePattern, 
				graphViewDatas, name);
	}
	
	public GraphView createWeekGraph(Context context, 
			List<RawAirvoiceData> rawAirvoiceDatas, String name) {
		Calendar oldest = Calendar.getInstance();
		oldest.add(Calendar.HOUR_OF_DAY, -24 * 7);

		String datePattern = "E HH:mm";
		
		ArrayList<GraphViewData> filteredGraphViewDatas = new ArrayList<GraphViewData>();
		
		for (int index = 0; index < rawAirvoiceDatas.size(); index++) {
			RawAirvoiceData rawAirvoiceData = rawAirvoiceDatas.get(index);
			Calendar observedDate = rawAirvoiceData.getObservedDate();
			
			if (observedDate.after(oldest)) {
				filteredGraphViewDatas.add(new GraphViewData(observedDate
						.getTimeInMillis(), rawAirvoiceData.getDollarValue()));
			}
		}
		
		GraphViewData[] graphViewDatas = filteredGraphViewDatas.toArray(new GraphViewData[0]);
		
		if(filteredGraphViewDatas.size() < 2 && rawAirvoiceDatas.size() > 0){
			
			RawAirvoiceData rawAirvoiceData = rawAirvoiceDatas.get(0);
			GraphViewData nowGraphViewData = new GraphViewData(Calendar.getInstance()
					.getTimeInMillis(), rawAirvoiceData.getDollarValue());
			
			Calendar lastWeek = Calendar.getInstance();
			lastWeek.set(Calendar.DAY_OF_MONTH, -7);
			GraphViewData lastWeekGraphViewData = new GraphViewData(lastWeek
					.getTimeInMillis(), rawAirvoiceData.getDollarValue());
			graphViewDatas = new GraphViewData[]{lastWeekGraphViewData, nowGraphViewData};
		}
		else if(filteredGraphViewDatas.size() > 0){
			graphViewDatas = filteredGraphViewDatas.toArray(new GraphViewData[0]);
		} else{
			graphViewDatas = new GraphViewData[0];
		}
		
		return createGraph(context, rawAirvoiceDatas, oldest, datePattern, 
				graphViewDatas, name);
	}
	
	public GraphView createMonthGraph(Context context, 
			List<RawAirvoiceData> rawAirvoiceDatas, String name) {
		Calendar oldest = Calendar.getInstance();
		oldest.add(Calendar.MONTH, -1);

		String datePattern = "MMM d";
		
		ArrayList<GraphViewData> filteredGraphViewDatas = new ArrayList<GraphViewData>();
		
		for (int index = 0; index < rawAirvoiceDatas.size(); index++) {
			RawAirvoiceData rawAirvoiceData = rawAirvoiceDatas.get(index);
			Calendar observedDate = rawAirvoiceData.getObservedDate();
			
			if (observedDate.after(oldest)) {
				filteredGraphViewDatas.add(new GraphViewData(observedDate
						.getTimeInMillis(), rawAirvoiceData.getDollarValue()));
			}
		}
		
		GraphViewData[] graphViewDatas = filteredGraphViewDatas.toArray(new GraphViewData[0]);
		
		if(filteredGraphViewDatas.size() < 2 && rawAirvoiceDatas.size() > 0){
			
			RawAirvoiceData rawAirvoiceData = rawAirvoiceDatas.get(0);
			GraphViewData nowGraphViewData = new GraphViewData(Calendar.getInstance()
					.getTimeInMillis(), rawAirvoiceData.getDollarValue());
			
			Calendar lastMonth = Calendar.getInstance();
			lastMonth.set(Calendar.MONTH, -1);
			GraphViewData lastMonthGraphViewData = new GraphViewData(lastMonth
					.getTimeInMillis(), rawAirvoiceData.getDollarValue());
			graphViewDatas = new GraphViewData[]{lastMonthGraphViewData, nowGraphViewData};
		}
		else if(filteredGraphViewDatas.size() > 0){
			graphViewDatas = filteredGraphViewDatas.toArray(new GraphViewData[0]);
		} else{
			graphViewDatas = new GraphViewData[0];
		}
		
		return createGraph(context, rawAirvoiceDatas, oldest, datePattern, 
				graphViewDatas, name);
	}
	
	public GraphView createAllGraph(Context context, 
			List<RawAirvoiceData> rawAirvoiceDatas, String name) {
		Calendar oldest = Calendar.getInstance();
		oldest.add(Calendar.YEAR, -1000);

		String datePattern = findCorrectDatePattern(rawAirvoiceDatas);
		
		ArrayList<GraphViewData> filteredGraphViewDatas = new ArrayList<GraphViewData>();
		
		for (int index = 0; index < rawAirvoiceDatas.size(); index++) {
			RawAirvoiceData rawAirvoiceData = rawAirvoiceDatas.get(index);
			Calendar observedDate = rawAirvoiceData.getObservedDate();
			
			if (observedDate.after(oldest)) {
				filteredGraphViewDatas.add(new GraphViewData(observedDate
						.getTimeInMillis(), rawAirvoiceData.getDollarValue()));
			}
		}
		
		GraphViewData[] graphViewDatas = filteredGraphViewDatas.toArray(new GraphViewData[0]);
		
		if(filteredGraphViewDatas.size() > 0){
			graphViewDatas = filteredGraphViewDatas.toArray(new GraphViewData[0]);
		} else{
			graphViewDatas = new GraphViewData[0];
		}
		
		return createGraph(context, rawAirvoiceDatas, oldest, datePattern, graphViewDatas, name);
	}
	
	private GraphView createGraph(Context context, 
			List<RawAirvoiceData> rawAirvoiceDatas, Calendar oldest, 
			final String datePattern, GraphViewData[] graphViewDatas, String name) {
		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries(graphViewDatas);

		GraphView graphView = new LineGraphView(context, name);
		((LineGraphView) graphView).setDrawDataPoints(true);
		((LineGraphView) graphView).setDataPointsRadius(15f);
		graphView.addSeries(exampleSeries);
		graphView.setScrollable(true);
		graphView.setScalable(true);

		graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
			private DecimalFormat df = new DecimalFormat("#.00");
			private SimpleDateFormat dateFormat = new SimpleDateFormat(
					datePattern);

			@Override
			public String formatLabel(double value, boolean isValueX) {
				if (isValueX) {
					Date d = new Date((long) value);
					return dateFormat.format(d);
				} else {
					return "$" + df.format(value);
				}
			}
		});

		return graphView;
	}
	
	private String findCorrectDatePattern(List<RawAirvoiceData> rawAirvoiceDatas) {
		if (rawAirvoiceDatas.size() > 0) {
			Calendar startDate = rawAirvoiceDatas.get(0).getObservedDate();
			Calendar endDate = rawAirvoiceDatas
					.get(rawAirvoiceDatas.size() - 1).getObservedDate();

			long dateDiffInHours = Math.abs(startDate.getTimeInMillis()
					- endDate.getTimeInMillis()) / 1000 / 60 / 60;

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
