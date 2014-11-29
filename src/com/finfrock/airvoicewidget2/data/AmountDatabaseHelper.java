package com.finfrock.airvoicewidget2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AmountDatabaseHelper extends SQLiteOpenHelper  {

    private static final int DATABASE_VERSION = 1;

    public AmountDatabaseHelper(Context context) {
        super(context, AmountDbAdapter.DB_TABLE, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
    	AmountTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
            int newVersion) {
    	AmountTable.onUpgrade(database, oldVersion, newVersion);
    }
}
