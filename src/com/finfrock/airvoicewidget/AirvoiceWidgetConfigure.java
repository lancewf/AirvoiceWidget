package com.finfrock.airvoicewidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AirvoiceWidgetConfigure extends Activity {
	
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private EditText mAppWidgetPrefix;
	private RadioGroup displayTypeRadioGroup;
	private static final String PHONE_NAME_KEY = "phoneNumber_";
	private static final String DISPLAY_TYPE_KEY = "displayType_";
    private static final String PREFS_NAME = "com.finfrock.airvoicewidget.AirvoiceDisplay";
	
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        setResult(RESULT_CANCELED);
        
        mAppWidgetPrefix = (EditText)findViewById(R.id.editTextPhoneNumber);
        displayTypeRadioGroup = (RadioGroup)findViewById(R.id.radioGroupDisplayType);
        
        findViewById(R.id.saveButton).setOnClickListener(mOnClickListener);
        
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, 
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        
        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        
        TelephonyManager tMgr =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        mAppWidgetPrefix.setText(mPhoneNumber);
    }
    
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = AirvoiceWidgetConfigure.this;

            // When the button is clicked, save the string in our prefs and return that they
            // clicked OK.
            String phoneNumber = mAppWidgetPrefix.getText().toString();
            RadioButton selectedRadioButton = (RadioButton)
            		findViewById(displayTypeRadioGroup.getCheckedRadioButtonId());
            savePrefs(context, mAppWidgetId, phoneNumber, selectedRadioButton.getText().toString());
            
            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            AirvoiceDisplay.updateAppWidget(context, appWidgetManager,
                    mAppWidgetId, phoneNumber);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
    
    static String phoneNumberPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		if (prefs != null) {
			String value = prefs.getString(PHONE_NAME_KEY + appWidgetId,
					"phone number not found");
			return value;
		} else {
			return "not found";
		}
    }
    
    static String displayType(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		if (prefs != null) {
			String value = prefs.getString(DISPLAY_TYPE_KEY + appWidgetId,
					"no display type found");
			return value;
		} else {
			return "not found";
		}
    }
    
    // Write the prefix to the SharedPreferences object for this widget
    static void savePrefs(Context context, int appWidgetId, String phoneNumber, String displayType) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PHONE_NAME_KEY + appWidgetId, phoneNumber);
        prefs.putString(DISPLAY_TYPE_KEY + appWidgetId, displayType);
        prefs.commit();
    }
}
