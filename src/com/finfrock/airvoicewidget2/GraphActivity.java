package com.finfrock.airvoicewidget2;

import java.util.List;

import retiever.RawAirvoiceData;

import com.finfrock.airvoicewidget2.data.AmountDbAdapter;
import com.jjoe64.graphview.GraphView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class GraphActivity extends Activity {

	private GraphBuilder graphBuilder = new GraphBuilder();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph);
		
		String phoneNumber = getIntent().getExtras().getString("phoneNumber");
		String name = getIntent().getExtras().getString("name");
		String type = getIntent().getExtras().getString("type");
		
		
		List<RawAirvoiceData> rawAirvoiceDatas = getRawAirvoiceDatas(phoneNumber);
        
		GraphView graphView = null;
		if(type.equals("Day")){
			graphView = graphBuilder.createCurrentDayGraph(this, rawAirvoiceDatas, name);
		} else if(type.equals("Week")){
			graphView = graphBuilder.createWeekGraph(this, rawAirvoiceDatas, name);
		} else if(type.equals("Month")){
			graphView = graphBuilder.createMonthGraph(this, rawAirvoiceDatas, name);
		} else if(type.equals("All")){
			graphView = graphBuilder.createAllGraph(this, rawAirvoiceDatas, name);
		}
		
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
}
