package GarbageBin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GarbageHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "GARBAGE_TABLE";
    private static final String ID = "ID";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String STATUS = "status";
    private static final String EMPTIED = "emptied";
    private static final String IP = "ip";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME +" (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    LATITUDE + " REAL, " +
                    LONGITUDE + " REAL, " +
                    EMPTIED + " TEXT, " +
                    STATUS + " TEXT, " +
                    IP + " TEXT);";

    public GarbageHandler(Context context){
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE);
        ContentValues values = new ContentValues();
        db.insert(TABLE_NAME, null, values);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(CREATE_TABLE);
        onCreate(db);}

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);}

    public void setBin(GarbageBin Bin){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LATITUDE, Bin.getLat());
        values.put(LONGITUDE, Bin.getLon());
        values.put(STATUS, Bin.getStatus());
        values.put(EMPTIED, Bin.getEmptied());
        values.put(IP, Bin.getIp());
        if (checkData(Bin.getID())) {
            db.insert(TABLE_NAME, null, values);
            db.close();
        }
        else {
            System.out.println("Data exists");
            String where = ID + "=?";
            String[] whereArg = new String [] {String.valueOf(Bin.getID())};
            db.update(TABLE_NAME, values, where, whereArg);
            db.close();
        }
    }
    public GarbageBin getBin(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[] { ID, LATITUDE, LONGITUDE, EMPTIED, STATUS, IP};
        Cursor c = db.query(TABLE_NAME, columns, null, null, null, null, null);
        int iLat = c.getColumnIndex(LATITUDE);
        int iLon = c.getColumnIndex(LONGITUDE);
        int iStatus = c.getColumnIndex(STATUS);
        int iEmptied = c.getColumnIndex(EMPTIED);
        int iIp = c.getColumnIndex(IP);
        c.moveToFirst();
        GarbageBin Bin = new GarbageBin(id, c.getDouble(iLat), c.getDouble(iLon), c.getString(iStatus), c.getString(iEmptied), c.getString(iIp));
        c.close();
        db.close();
        return Bin;
    }
    private boolean checkData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_NAME + " where " + ID + " = " + id;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
}
