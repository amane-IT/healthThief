package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.myapplication.DataBases.CreateDB._TABLENAME0;

public class DbOpenHelper {

    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DataBases.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+ _TABLENAME0);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }


    // insert data
    public long insertColumn(String date, String meal, String food, String calorie, String carbon, String protein, String fat, String diary){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.DATE, date);
        values.put(DataBases.CreateDB.MEAL, meal);
        values.put(DataBases.CreateDB.FOOD, food);
        values.put(DataBases.CreateDB.CAL, calorie);
        values.put(DataBases.CreateDB.PROTEIN, protein);
        values.put(DataBases.CreateDB.FAT, fat);
        values.put(DataBases.CreateDB.DIARY, diary);
        return mDB.insert(DataBases.CreateDB._TABLENAME0,null,values);
    }

    // select data
    public Cursor selectColumns(){
        return mDB.query(DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    // update data
    public boolean updateColumn(long id, String date, String meal, String food, String calorie, String carbon, String protein, String fat, String diary){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.DATE, date);
        values.put(DataBases.CreateDB.MEAL, meal);
        values.put(DataBases.CreateDB.FOOD, food);
        values.put(DataBases.CreateDB.CAL, calorie);
        values.put(DataBases.CreateDB.PROTEIN, protein);
        values.put(DataBases.CreateDB.FAT, fat);
        values.put(DataBases.CreateDB.DIARY, diary);
        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }

    // delete data
    public boolean deleteColumn(long id){
        return mDB.delete(DataBases.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }

}
