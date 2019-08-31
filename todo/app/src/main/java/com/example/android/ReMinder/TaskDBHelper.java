package com.example.android.ReMinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ToDoDBHelper.db";
    private static final String CONTACTS_TABLE_NAME = "reminder";

    public TaskDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 7);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL(
              "CREATE TABLE IF NOT EXISTS "+ CONTACTS_TABLE_NAME +
                      "(id INTEGER PRIMARY KEY, task TEXT, dateStr INTEGER, des TEXT, chB0 INTEGER, chB1 INTEGER, chB2 INTEGER, chB3 INTEGER, phone TEXT, email TEXT, defPhone TEXT, defEmail TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        db.execSQL("DROP TABLE IF EXISTS "+CONTACTS_TABLE_NAME);
        onCreate(db);
    }

    private long getDate(String day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        try {
            date = dateFormat.parse(day);
        } catch (ParseException e) {}
        return date.getTime();
    }

    public boolean insertContact  (String task, String dateStr, String des, int chB0, int chB1, int chB2, int chB3, String phone, String email)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task", task);
        contentValues.put("dateStr", getDate(dateStr));
        contentValues.put("des", des);
        contentValues.put("chB0", chB0);
        contentValues.put("chB1", chB1);
        contentValues.put("chB2", chB2);
        contentValues.put("chB3", chB3);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        db.insert(CONTACTS_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateContact (String id, String task, String dateStr, String des, int chB0, int chB1, int chB2, int chB3, String phone, String email)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("task", task);
        contentValues.put("dateStr", getDate(dateStr));
        contentValues.put("des", des);
        contentValues.put("chB0", chB0);
        contentValues.put("chB1", chB1);
        contentValues.put("chB2", chB2);
        contentValues.put("chB3", chB3);
        contentValues.put("phone", phone);
        contentValues.put("email", email);

        db.update(CONTACTS_TABLE_NAME, contentValues, "id = ? ", new String[] { id } );
        return true;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+" order by id desc", null);
        return res;
    }

    public Cursor getDataSpecific(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+" WHERE id = '"+id+"' order by id desc", null);
        return res;
    }

    public Cursor getDataToday(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+
                " WHERE date(datetime(dateStr / 1000 , 'unixepoch', 'localtime')) = date('now', 'localtime') order by id desc", null);
        return res;

    }

    public Cursor getDataTomorrow(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+
                " WHERE date(datetime(dateStr / 1000 , 'unixepoch', 'localtime')) = date('now', '+1 day', 'localtime')  order by id desc", null);
        return res;

    }

    public Cursor getDataUpcoming(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from "+CONTACTS_TABLE_NAME+
                " WHERE date(datetime(dateStr / 1000 , 'unixepoch', 'localtime')) > date('now', '+1 day', 'localtime') order by id desc", null);
        return res;

    }

    public Integer deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CONTACTS_TABLE_NAME, "id=?", new String[] { id });
    }
}