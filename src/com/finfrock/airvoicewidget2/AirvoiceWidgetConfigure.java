package com.finfrock.airvoicewidget2;

import com.finfrock.airvoicewidget2.R;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AirvoiceWidgetConfigure extends Activity {
	// -------------------------------------------------------------------------
	// Private Data
	// -------------------------------------------------------------------------

	private SharedStorage sharedStorage = new SharedStorage();
    private int appWidgetId = -9999;
    
	// -------------------------------------------------------------------------
	// Activity Member
	// -------------------------------------------------------------------------

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config);
		setResult(RESULT_CANCELED);

		if (getAppWidgetId() != AppWidgetManager.INVALID_APPWIDGET_ID) {
			setPhonesPhoneNumberInPhoneNumberField();

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
    
	// -------------------------------------------------------------------------
	// Private Members
	// -------------------------------------------------------------------------

    private void setPhonesPhoneNumberInPhoneNumberField(){
    	EditText mAppWidgetPrefix = (EditText) findViewById(R.id.editTextPhoneNumber);
    	mAppWidgetPrefix.setText(getPhonesPhoneNumber());
    }
    
	private int getAppWidgetId() {
		if (appWidgetId == -9999) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				appWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			} else {
				appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
			}
		}

		return appWidgetId;
	}
    
    private String getPhonesPhoneNumber(){
        TelephonyManager tMgr =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        
        return mPhoneNumber;
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
    
	private void saveButtonPressed() {
		String phoneNumber = getEnteredPhoneNumber();
		
		String displayType = getSelectedRadioButtonText();

		sharedStorage.saveInformation(this, getAppWidgetId(), phoneNumber, displayType);
 
		updateAppWidget();

		// Make sure we pass back the original appWidgetId
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getAppWidgetId());
		setResult(RESULT_OK, resultValue);
		finish();
	}
	
	private void updateAppWidget(){
		int[] ids = new int[]{getAppWidgetId()};
		
		Intent updateIntent = new Intent();
		updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updateIntent.putExtra(AirvoiceDisplay.WIDGET_IDS_KEY, ids);
		sendBroadcast(updateIntent);
	}
}
