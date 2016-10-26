package com.finfrock.airvoicewidget2.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AmountTable {
    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table " 
    		+ AmountDbAdapter.DB_TABLE + " ( "
    		+ AmountDbAdapter.KEY_ROWID + " integer primary key autoincrement, "
            + AmountDbAdapter.KEY_PHONE_NUMBER + " text not null, "
            + AmountDbAdapter.KEY_PLAN_TEXT + " text not null, "
            + AmountDbAdapter.KEY_DOLLAR_VALUE + " float not null, "
            + AmountDbAdapter.KEY_EXPIRE_DATE + " INTEGER not null, "
            + AmountDbAdapter.KEY_OBSERVED_DATE + " INTEGER not null "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
            int newVersion) {
        Log.w(AmountTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + AmountDbAdapter.DB_TABLE);
        onCreate(database);
    }
}
