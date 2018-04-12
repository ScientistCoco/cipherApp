package com.example.court.cipherapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "quadgram.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public int getFrequency(String word){
        SQLiteDatabase db = getReadableDatabase();
        Log.i("DATABASE", word);
        Cursor c = db.rawQuery("Select frequency from english_quadgrams where gram = ?", new String[]{word.toUpperCase()});
        Log.i("DATABASE", Integer.toString(c.getCount()));
        c.moveToFirst();
        if (c.getCount() == 0){
            c.close();
            return 0;
        }
        int count = c.getInt(0);
        c.close();
        return count;
    }
}