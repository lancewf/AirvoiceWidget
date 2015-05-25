package com.finfrock.airvoicewidget2;

import com.finfrock.airvoicewidget2.R;
import com.finfrock.airvoicewidget2.plans.Plan;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AirvoiceWidgetEdit extends ActionBarActivity {
	// -------------------------------------------------------------------------
	// Private Data
	// -------------------------------------------------------------------------

	private SharedStorage sharedStorage = new SharedStorage();
    
	// -------------------------------------------------------------------------
	// Activity Member
	// -------------------------------------------------------------------------

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config);
		setResult(RESULT_CANCELED);
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		if (getAppWidgetId() != AppWidgetManager.INVALID_APPWIDGET_ID) {
			setFields();

			findViewById(R.id.saveButton).setOnClickListener(
					new View.OnClickListener() {
						public void onClick(View v) {
							saveButtonPressed();
						}
					});
		} else {
			finish();
		}
	}
    
    @Override
    public Intent getSupportParentActivityIntent() {
        Intent newIntent=null;
        try {
             //you need to define the class with package name
             newIntent = new Intent(this,Class.forName("com.finfrock.airvoicewidget2.AirvoiceInfoView"));
             
             newIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getAppWidgetId());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newIntent;
    }
    
    @Override
	public void onStart() {
    	super.onStart();
    	Log.i("info", "AirvoiceWidgetEdit-onStart id " + getAppWidgetId() + 
    			" name: " + sharedStorage.getNameLabel(this, getAppWidgetId()));
    }
    
    @Override
	public void onRestart() {
    	super.onRestart();
    	Log.i("info", "AirvoiceWidgetEdit-onRestart id " + getAppWidgetId() + 
    			" name: " + sharedStorage.getNameLabel(this, getAppWidgetId()));
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
	
    private void setFields(){
    	String phoneNumber = sharedStorage.getPhoneNumber(this, getAppWidgetId());
    	setPhoneNumber(phoneNumber);
    	int warningLimit = sharedStorage.getWarningLimit(this, getAppWidgetId());
    	setWarningLimit(warningLimit);
    	int warningDays = sharedStorage.getWarningDays(this, getAppWidgetId());
    	setWarningDays(warningDays);
    	String name = sharedStorage.getNameLabel(this, getAppWidgetId());
    	setNameText(name);
    	String displayType = sharedStorage.getDisplayType(this, getAppWidgetId());
    	setRadioButton(displayType);
    }
    
    private void setRadioButton(String displayType){
    	RadioGroup displayTypeRadioGroup = (RadioGroup) findViewById(R.id.radioGroupDisplayType);
    	
		if (displayType.equals(Plan.MONEY_DISPLAY_TYPE)) {
			displayTypeRadioGroup.check(R.id.moneyRadio);
		} else if (displayType.equals(Plan.DATA_DISPLAY_TYPE)) {
			displayTypeRadioGroup.check(R.id.dataRadio);
		} else if (displayType.equals(Plan.MINUTES_DISPLAY_TYPE)) {
			displayTypeRadioGroup.check(R.id.minutesRadio);
		}
    }
    
    private String getEnteredNameText(){
    	EditText mAppWidgetPrefix = (EditText) findViewById(R.id.nameTextBox);
    	return mAppWidgetPrefix.getText().toString();
    }
    
    private void setNameText(String name){
    	EditText mAppWidgetPrefix = (EditText) findViewById(R.id.nameTextBox);
    	mAppWidgetPrefix.setText(name);
    }
    
    private String getSelectedRadioButtonText(){
    	RadioGroup displayTypeRadioGroup = (RadioGroup) findViewById(R.id.radioGroupDisplayType);
		RadioButton selectedRadioButton = (RadioButton) findViewById(displayTypeRadioGroup
				.getCheckedRadioButtonId());
		
		return selectedRadioButton.getText().toString();
    }
    
    private String getEnteredPhoneNumber(){
    	EditText mAppWidgetPrefix = (EditText) findViewById(R.id.editTextPhoneNumber);
    	return mAppWidgetPrefix.getText().toString();
    }
    
    private void setPhoneNumber(String phoneNumber){
    	EditText mAppWidgetPrefix = (EditText) findViewById(R.id.editTextPhoneNumber);
    	mAppWidgetPrefix.setText(phoneNumber);
    }
    
    private int getWarningLimit(){
    	EditText mAppWidgetPrefix = (EditText) findViewById(R.id.warningLimitText);
    	String rawText = mAppWidgetPrefix.getText().toString();
    	return Integer.parseInt(rawText);
    }
    
    private void setWarningLimit(int warningLimit){
    	EditText mAppWidgetPrefix = (EditText) findViewById(R.id.warningLimitText);
    	mAppWidgetPrefix.setText(warningLimit + "");
    }
    
    private void setWarningDays(int warningLimit){
    	EditText mAppWidgetPrefix = (EditText) findViewById(R.id.warningDaysEditText);
    	mAppWidgetPrefix.setText(warningLimit + "");
    }
    
    private int getWarningDays(){
    	EditText mAppWidgetPrefix = (EditText) findViewById(R.id.warningDaysEditText);
    	String rawText = mAppWidgetPrefix.getText().toString();
    	return Integer.parseInt(rawText);
    }
    
	private void saveButtonPressed() {
		String phoneNumber = getEnteredPhoneNumber();
		
		String displayType = getSelectedRadioButtonText();
		
		String name = getEnteredNameText();
		
		int warningLimit = getWarningLimit();

		int warningDays = getWarningDays();
		
		sharedStorage.saveInformation(this, getAppWidgetId(), phoneNumber, 
				displayType, name, warningLimit, warningDays);
 
		WidgetRefresher widgetRefresher = new WidgetRefresher();
		
		widgetRefresher.updateWidgetFromCache(this, getAppWidgetId());
		
        sendUpdateRequestToWidgets();

		// Make sure we pass back the original appWidgetId
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getAppWidgetId());
		setResult(RESULT_OK, resultValue);
		finish();
	}
	
	private void sendUpdateRequestToWidgets(){
		int[] ids = new int[]{getAppWidgetId()};
		
		Intent updateIntent = new Intent();
		updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updateIntent.putExtra(AirvoiceDisplay.WIDGET_IDS_KEY, ids);
		sendBroadcast(updateIntent);
	}
}
