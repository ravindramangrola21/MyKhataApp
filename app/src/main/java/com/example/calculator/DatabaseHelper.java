package com.example.calculator;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String TAG= DatabaseHelper.class.getSimpleName();
    private static String DATABASE_NAME="MyDiary.db";
    private static int VERSION=1;
    private static String TABLE_NAME1="SellerTable";
    private static String TABLE_NAME2="CustomerNameTable";
    private static String TABLE_NAME3="CustomerEntriesTable";
    private static String TABLE_NAME4="CustomerEntryDate";
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("DatabaseHelper", "onCreate: this is onCreate Metho from ");
        sqLiteDatabase.execSQL("create table "+TABLE_NAME1+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT,ITEMNAME TEXT, WEIGHT TEXT, RATE TEXT, DATE_ID INTEGER);");
        sqLiteDatabase.execSQL("create table "+TABLE_NAME2+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT);");
        sqLiteDatabase.execSQL("create table "+TABLE_NAME3+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, C_ID INTEGER, VEG_NAME TEXT, VEG_WEIGHT TEXT, VEG_RATE TEXT, DATE_ID INTEGER);");
        sqLiteDatabase.execSQL("create table "+TABLE_NAME4+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT UNIQUE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME2);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME3);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME4);
        onCreate(sqLiteDatabase);
    }

    public boolean insertIntoSellerTable(String name, String itemName, String weight, String rate, int date_id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("ITEMNAME", itemName);
        contentValues.put("WEIGHT", weight);
        contentValues.put("RATE", rate);
        contentValues.put("DATE_ID", date_id);
        Long result = sqLiteDatabase.insert(TABLE_NAME1, null, contentValues);
        if(result==-1)
            return false;
        else
            return true;
    }
    public boolean insertIntoCustomerNameTable(String cname)
    {
        SQLiteDatabase sql =this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", cname);

        long result= sql.insert(TABLE_NAME2, null, contentValues);

        if(result==-1)
            return false;
        else
            return true;

    }
    public boolean insertIntoCustomerEntriesTable(int id, String vegName, String vegWeight, String vegRate, int dateId)
    {
        Log.d(TAG, "insertIntoCustomerEntriesTable: DateId "+dateId);
        SQLiteDatabase sql =this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("C_ID", id);
        contentValues.put("VEG_NAME", vegName);
        contentValues.put("VEG_WEIGHT", vegWeight);
        contentValues.put("VEG_RATE", vegRate);

        contentValues.put("DATE_ID", dateId);

        long result= sql.insert(TABLE_NAME3, null, contentValues);

        if(result==-1)
            return false;
        else
            return true;

    }
    public boolean insertIntoCustomerDateTable(String edate)
    {
        long result=-1;
        SQLiteDatabase sql =this.getWritableDatabase();


        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("date", edate);
            result = sql.insert(TABLE_NAME4, null, contentValues);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        if(result==-1)
            return false;
        else
            return true;

    }
    public Cursor getDataFromSellerTable(int date_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cusrsor = db.rawQuery("select * from "+TABLE_NAME1+" where DATE_ID="+date_id, null);
        return cusrsor;
    }
    public Cursor getDataFromCustomerNameTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cusrsor = db.rawQuery("select * from "+TABLE_NAME2, null);
        return cusrsor;
    }
    public Cursor getDataFromCustomerEntriesTable(int date_id, int name_id)
    {
        Log.d("DatabaseHelper", "getDataFromCustomerEntriesTable: "+date_id+" "+name_id);
        SQLiteDatabase db =this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME3+" where C_ID="+name_id+" AND DATE_ID="+date_id, null);
        return cursor;
    }
    public Cursor getDateFromCustomerDateTable(String dateFilter, Boolean filerValue)
    {
        Log.d(TAG, "getDateFromCustomerDateTable: Date state :"+filerValue);
        SQLiteDatabase db =this.getWritableDatabase();
        if(filerValue)
        {
            Cursor cursor1= db.rawQuery("select * from "+TABLE_NAME4+" where date = ?", new String[]{dateFilter});
            return cursor1;
        }
        else {
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME4, null);
            return cursor;
        }
    }
    public Cursor getDateID(String date)
    {
        Log.d(TAG, "getDateID: Date"+date);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select ID from "+TABLE_NAME4+" where date =?", new String[]{date});
        return cursor;
    }

    public boolean deleteItemFromTable3(int id)
    {
        SQLiteDatabase db = getWritableDatabase();
        if(db.delete(TABLE_NAME3, "ID="+id,null)>0)
            return true;
        else
            return false;

    }
    public boolean deleteItemFromTable2and3(int id)
    {
        SQLiteDatabase db = getWritableDatabase();
        int table2= db.delete(TABLE_NAME2, "ID="+id,null);
        if(db.rawQuery("select * from "+TABLE_NAME3+" where C_ID="+id, null).getCount()>0) {
            int table3 = db.delete(TABLE_NAME3, "C_ID=" + id, null);
            if(table2>0 && table3>0)
                return true;
        }
        if(table2>0)
            return true;
        else
            return false;

    }
    public boolean deleteItemFromTable1(int id, boolean dateFilter)
    {
        SQLiteDatabase db = getWritableDatabase();
        int table1=0;
        if(dateFilter)
            table1=db.delete(TABLE_NAME1, "DATE_ID="+id,null);
        else
            table1=db.delete(TABLE_NAME1, "ID="+id,null);
        if(table1>0)
            return true;
        else
            return false;
    }

    public boolean UpdateNameInTable2(int id, String newName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues newData = new ContentValues();
        newData.put("NAME", newName);
        if(db.update(TABLE_NAME2, newData," ID="+id, null)>0)
            return true;
        else
            return false;
    }

    public boolean deleteItemFromTable3ForDate(Integer id, Integer c_id) {
        SQLiteDatabase db = getWritableDatabase();

        if(db.delete(TABLE_NAME3, "DATE_ID="+id+" and C_ID="+c_id,null)>0)
            return true;
        else
            return false;
    }

    public void checkTable1AndTable3() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME4,"ID not in (select distinct DATE_ID from "+TABLE_NAME1+") and ID not in (select distinct DATE_ID from "+TABLE_NAME3+")",null);
    }
}
