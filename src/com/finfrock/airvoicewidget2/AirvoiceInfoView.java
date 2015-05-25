package com.finfrock.airvoicewidget2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retiever.RawAirvoiceData;

import com.finfrock.airvoicewidget2.R;
import com.finfrock.airvoicewidget2.data.AmountDbAdapter;
import com.jjoe64.graphview.GraphView;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class AirvoiceInfoView extends ActionBarActivity {
	// -------------------------------------------------------------------------
	// Private Data
	// -------------------------------------------------------------------------

	private SharedStorage sharedStorage = new SharedStorage();
	private String currentType = "Month";

	// -------------------------------------------------------------------------
	// Activity Member
	// -------------------------------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.window);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
				R.array.action_list,
				android.R.layout.simple_spinner_dropdown_item);

		
		ActionBar.OnNavigationListener mOnNavigationListener = new ActionBar.OnNavigationListener() {
			// Get the same strings provided for the drop-down's ArrayAdapter
			String[] strings = getResources().getStringArray(
					R.array.action_list);

			@Override
			public boolean onNavigationItemSelected(int position, long itemId) {

				Log.i("info", "strings: " + strings[position]);

				currentType = strings[position];
				setFields(currentType);
				
				return true;
			}
		};
		
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
		actionBar.setSelectedNavigationItem(2);
		
		if (getAppWidgetId() != AppWidgetManager.INVALID_APPWIDGET_ID) {

		} else {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.ic_action_settings:
			settingsButtonPressed();
			return true;
		case R.id.action_purchase:
			addMoneyButtonPressed();
			return true;
		case R.id.ic_action_full_screen:
			showGraph();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// -------------------------------------------------------------------------
	// Private Members
	// -------------------------------------------------------------------------

	private int getAppWidgetId() {
		int appWidgetId = getIntent().getExtras().getInt(
				AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		return appWidgetId;
	}

	private void showGraph() {
		Intent intent = new Intent().setClass(this, GraphActivity.class);
		String phoneNumber = sharedStorage.getPhoneNumber(this,
				getAppWidgetId());
		String name = sharedStorage.getNameLabel(this, getAppWidgetId());
		intent.putExtra("phoneNumber", phoneNumber);
		intent.putExtra("name", name);
		intent.putExtra("type", currentType);
		this.startActivity(intent);
	}

	private void settingsButtonPressed() {
		Intent intent = new Intent().setClass(this, AirvoiceWidgetEdit.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getAppWidgetId());
		this.startActivity(intent);
	}

	private void addMoneyButtonPressed() {
		String phoneNumber = sharedStorage.getPhoneNumber(this,
				getAppWidgetId());
		Intent browserIntent = new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("https://www.airvoicewireless.com/AddAirtimeAutoRefilloptions.aspx?sn="
						+ phoneNumber));
		startActivity(browserIntent);
	}

	private List<RawAirvoiceData> getRawAirvoiceDatas(String phoneNumber) {
		AmountDbAdapter amountDbAdapter = new AmountDbAdapter(this);
		amountDbAdapter.open();
		List<RawAirvoiceData> rawAirvoiceDatas = amountDbAdapter
				.getAllRawAirvoiceDatas(phoneNumber);
		amountDbAdapter.close();

		return rawAirvoiceDatas;
	}

	private void setName() {
		String label = sharedStorage.getNameLabel(this, getAppWidgetId());
		((TextView) findViewById(R.id.textView_window_name)).setText(label);
	}

	private void createGraph(List<RawAirvoiceData> rawAirvoiceDatas, String type) {
		GraphBuilder graphBuilder = new GraphBuilder();
		GraphView graphView = null;
		
		if(type.equals("Day")){
			graphView = graphBuilder.createCurrentDayGraph(this, rawAirvoiceDatas, "");
		} else if(type.equals("Week")){
			graphView = graphBuilder.createWeekGraph(this, rawAirvoiceDatas, "");
		} else if(type.equals("Month")){
			graphView = graphBuilder.createMonthGraph(this, rawAirvoiceDatas, "");
		} else if(type.equals("All")){
			graphView = graphBuilder.createAllGraph(this, rawAirvoiceDatas, "");
		}

		LinearLayout layout = (LinearLayout) findViewById(R.id.graph_sample_view);
		ScrollView scrollViewLayout = (ScrollView) findViewById(R.id.scroll_layout);
		int textViewHeight = ((TextView) findViewById(R.id.textView_window_name)).getHeight() + ((TextView) findViewById(R.id.average_amount)).getHeight();
		layout.removeAllViews();
		graphView.getLayoutParams().height = scrollViewLayout.getHeight() - textViewHeight - 20;
		layout.addView(graphView);
		layout.getLayoutParams().height = scrollViewLayout.getHeight() - textViewHeight - 20;
	}

	private void setAverages(List<RawAirvoiceData> rawAirvoiceDatas, String type) {
		if (rawAirvoiceDatas.size() > 0) {
			Calendar oldest = Calendar.getInstance();
			
			if(type.equals("Day")){
				oldest.add(Calendar.HOUR_OF_DAY, -24);
			} else if(type.equals("Week")){
				oldest.add(Calendar.DAY_OF_MONTH, -7);
			} else if(type.equals("Month")){
				oldest.add(Calendar.MONTH, -1);
			} else if(type.equals("All")){
				oldest.add(Calendar.YEAR, -2000);
			}
			
			double totalAmountDecreased = getTotalAmountDecreased(rawAirvoiceDatas, oldest);
			
			DecimalFormat df = new DecimalFormat("#0.00");

			((TextView) findViewById(R.id.average_amount))
					.setText("Amount: $" + df.format(totalAmountDecreased));
		} else {

			((TextView) findViewById(R.id.average_amount))
					.setText("Daily: $0.00");
		}
	}
	
	private double getTotalAmountDecreased(
			List<RawAirvoiceData> rawAirvoiceDatas, Calendar oldest) {
		double previousValue = 0.0;
		double amountDecreased = 0.0;
		List<RawAirvoiceData> filteredRawAirvoiceDatas = filter(rawAirvoiceDatas, oldest);
		for (RawAirvoiceData rawAirvoiceData : filteredRawAirvoiceDatas) {
			if (rawAirvoiceData.getDollarValue() < previousValue) {
				amountDecreased += previousValue
						- rawAirvoiceData.getDollarValue();
			} else {

			}
			previousValue = rawAirvoiceData.getDollarValue();
		}

		return amountDecreased;
	}
	
	private List<RawAirvoiceData> filter(List<RawAirvoiceData> rawAirvoiceDatas, 
			Calendar oldest){
		ArrayList<RawAirvoiceData> filteredRawAirvoiceDatas = new ArrayList<RawAirvoiceData>();
		for(RawAirvoiceData rawAirvoiceData : rawAirvoiceDatas){
			if(rawAirvoiceData.getObservedDate().after(oldest)){
				filteredRawAirvoiceDatas.add(rawAirvoiceData);
			}
		}
		
		return filteredRawAirvoiceDatas;
	}

	private void setFields(String type) {
		String phoneNumber = sharedStorage.getPhoneNumber(this,
				getAppWidgetId());
		List<RawAirvoiceData> rawAirvoiceDatas = getRawAirvoiceDatas(phoneNumber);

		setAverages(rawAirvoiceDatas, type);
		setName();
		createGraph(rawAirvoiceDatas, type);
	}
}
