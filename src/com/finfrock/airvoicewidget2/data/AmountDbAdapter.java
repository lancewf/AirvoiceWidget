package com.finfrock.airvoicewidget2.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retiever.RawAirvoiceData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

//http://stackoverflow.com/questions/10600670/sqlitedatabase-query-method
public class AmountDbAdapter {
	 // Database fields
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DOLLAR_VALUE = "dollar_value";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_PLAN_TEXT = "plan_text";
    public static final String KEY_EXPIRE_DATE = "expire_date";
    public static final String KEY_OBSERVED_DATE = "observed_date";
    public static final String DB_TABLE = "amount";
    private Context context;
    private SQLiteDatabase db;
    private AmountDatabaseHelper dbHelper;
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

    public AmountDbAdapter(Context context) {
        this.context = context;
    }

    public AmountDbAdapter open() throws SQLException {
        dbHelper = new AmountDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long addRawAirvoiceData(RawAirvoiceData rawAirvoiceData) {
        ContentValues values = createContentValues(rawAirvoiceData);

        return db.insert(DB_TABLE, null, values);
    }
    
    public List<RawAirvoiceData> getAllRawAirvoiceDatas(){
        List<RawAirvoiceData> rawAirvoiceDatas = new ArrayList<RawAirvoiceData>();
        Cursor cursor = fetchAllRawAirvoiceDataCursor();
        
        if (cursor != null && cursor.moveToFirst()) {
            do{
                float dollarValue = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_DOLLAR_VALUE));
                String planText = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLAN_TEXT));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE_NUMBER));
                long exprireDateLong = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_EXPIRE_DATE));
                long observedDateLong = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_OBSERVED_DATE));
                Calendar observedDate = Calendar.getInstance();
                observedDate.setTimeInMillis(observedDateLong);
                Calendar expireCalendar = Calendar.getInstance();
                expireCalendar.setTimeInMillis(exprireDateLong);
                
                RawAirvoiceData rawAirvoiceData = 
                		new RawAirvoiceData(phoneNumber, dollarValue, 
                				expireCalendar, planText, observedDate);
                rawAirvoiceDatas.add(rawAirvoiceData);
            }while(cursor.moveToNext());
            
            cursor.close();
        }
        return rawAirvoiceDatas;
    }
    
    public List<RawAirvoiceData> getAllRawAirvoiceDatas(String givenPhoneNumber){
        List<RawAirvoiceData> rawAirvoiceDatas = new ArrayList<RawAirvoiceData>();
        Cursor cursor = fetchRawAirvoiceDataWithPhoneNumberCursor(givenPhoneNumber);
        
        if (cursor != null && cursor.moveToFirst()) {
            do{
                float dollarValue = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_DOLLAR_VALUE));
                String planText = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PLAN_TEXT));
                String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PHONE_NUMBER));
                long exprireDateLong = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_EXPIRE_DATE));
                long observedDateLong = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_OBSERVED_DATE));
                Calendar observedDate = Calendar.getInstance();
                observedDate.setTimeInMillis(observedDateLong);
                Calendar expireCalendar = Calendar.getInstance();
                expireCalendar.setTimeInMillis(exprireDateLong);
                
                RawAirvoiceData rawAirvoiceData = 
                		new RawAirvoiceData(phoneNumber, dollarValue, 
                				expireCalendar, planText, observedDate);
                
                if(givenPhoneNumber.contains(phoneNumber)){
                	rawAirvoiceDatas.add(rawAirvoiceData);
                }
            }while(cursor.moveToNext());
            
            cursor.close();
        }
        return rawAirvoiceDatas;
    }
    
    private Cursor fetchRawAirvoiceDataWithPhoneNumberCursor(String phoneNumber) {
    	String orderBy = KEY_OBSERVED_DATE ;
    	String whereClause = KEY_PHONE_NUMBER + " = ?";
    	String[] whereArgs = new String[] {phoneNumber};
//    	String[] tableColumns = new String[] { KEY_ROWID, KEY_PLAN_TEXT, KEY_DOLLAR_VALUE, 
//        		KEY_PHONE_NUMBER, KEY_EXPIRE_DATE, KEY_OBSERVED_DATE };
    	
        return db.query(DB_TABLE, null, whereClause, whereArgs, 
        		null, null, orderBy);
    }

    private Cursor fetchAllRawAirvoiceDataCursor() {
    	String orderBy = KEY_OBSERVED_DATE ;
        return db.query(DB_TABLE, null, null, null, null, null, orderBy);
    }
    
    private ContentValues createContentValues(RawAirvoiceData rawAirvoiceData) {
        ContentValues values = new ContentValues();
        values.put(KEY_DOLLAR_VALUE, rawAirvoiceData.getDollarValue());
        values.put(KEY_PLAN_TEXT, rawAirvoiceData.getPlanText());
        values.put(KEY_PHONE_NUMBER, rawAirvoiceData.getPhoneNumber());
        values.put(KEY_EXPIRE_DATE, rawAirvoiceData.getExpireDate().getTimeInMillis());
        values.put(KEY_OBSERVED_DATE, rawAirvoiceData.getObservedDate().getTimeInMillis());
        
        return values;
    }
}
