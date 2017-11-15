package com.studios.currencyconverter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Personal on 15.11.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private String LOG_TAG = "currency_log";

    public DBHelper(Context context) {
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(LOG_TAG, "--- onCreate database ---");
        // создаем таблицу с полями
        sqLiteDatabase.execSQL("CREATE TABLE myTable ("+ "id integer primary key autoincrement,"+ "name text,"+ "currency text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
