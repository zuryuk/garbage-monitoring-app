package com.pierre.biojoux.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecurityDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "LOGIN_TABLE";
    private static final String ID = "ID";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String DEFAULT_NAME = "admin";
    private static final String DEFAULT_PASS = "1234";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +" (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    USERNAME + " TEXT, " +
                    PASSWORD + " TEXT);";

    public SecurityDB(Context context){
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE);
        ContentValues values = new ContentValues();
        values.put(USERNAME, DEFAULT_NAME);
        values.put(PASSWORD, DEFAULT_PASS);
        db.insert(TABLE_NAME, null, values);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(CREATE_TABLE);
        onCreate(db);}

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);}

    private boolean checkPass(String string){
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
    public boolean login(String uname, String pass) {
        if (checkPass(uname) && checkPass(pass)) {
            SQLiteDatabase db = getWritableDatabase();
            String[] columns = new String[]{ID, USERNAME, PASSWORD};
            Cursor c = db.query(TABLE_NAME, columns, USERNAME + "='" + uname + "'", null, null, null, null);
            int iPass = c.getColumnIndex(PASSWORD);
            if (c.moveToFirst()){
            Boolean b = pass.equals(c.getString(iPass));
            c.close();
            return b;}
            else {return false;}

        } else {
            System.out.println("Only alphabet and numbers");
            return false;
        }
    }
}
