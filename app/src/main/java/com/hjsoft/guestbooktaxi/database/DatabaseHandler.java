package com.hjsoft.guestbooktaxi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hjsoft.guestbooktaxi.adapter.DBAdapter;

/**
 * Created by hjsoft on 16/11/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(DBAdapter.TABLE_USER_STATUS);
        sqLiteDatabase.execSQL(DBAdapter.TABLE_USER_LOCATIONS);
        sqLiteDatabase.execSQL(DBAdapter.TABLE_WALLET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "USER_STATUS");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "USER_LOCATIONS");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "USER_WALLET");
        // Create a new one.
        onCreate(sqLiteDatabase);

    }
}
