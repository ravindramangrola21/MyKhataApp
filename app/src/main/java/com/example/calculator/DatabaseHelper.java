package com.example.calculator;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.CpuUsageInfo;
import android.text.Editable;
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
    private static String TABLE_NAME4="CustomerEntryDateTable";
    private static String TABLE_NAME5="CustomerTransactionTable";
    private static String TABLE_NAME6="SellersNameTable";
    private static String TABLE_NAME7="SellersTransactionTable";
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("DatabaseHelper", "onCreate: this is onCreate Metho from ");
        sqLiteDatabase.execSQL("create table "+TABLE_NAME1+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, SELLER_ID INTEGER, ITEMNAME TEXT," +
                " WEIGHT TEXT, RATE TEXT, DATE_ID INTEGER, NANG_COUNT INTEGER DEFAULT 1, CHUNGI DOUBLE DEFAULT 5, TAX DOUBLE DEFAULT 0, IS_DEPOSITED BOOLEAN NOT NULL DEFAULT 0);");
        sqLiteDatabase.execSQL("create table "+TABLE_NAME2+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT);");
        sqLiteDatabase.execSQL("create table "+TABLE_NAME3+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, C_ID INTEGER, VEG_NAME TEXT," +
                " VEG_WEIGHT TEXT, VEG_RATE TEXT, DATE_ID INTEGER);");
        sqLiteDatabase.execSQL("create table "+TABLE_NAME4+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT UNIQUE);");
        sqLiteDatabase.execSQL("create table "+TABLE_NAME5+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, DEPOSIT DOUBLE DEFAULT 0, " +
                "DATE_ID INTEGER, C_ID INTEGER);");
        sqLiteDatabase.execSQL("create table "+TABLE_NAME6+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT);");
        sqLiteDatabase.execSQL("create table "+TABLE_NAME7+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, DEPOSIT DOUBLE DEFAULT 0, " +
                "DATE_ID INTEGER, SELLER_ID INTEGER);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME2);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME3);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME4);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME5);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME6);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME7);
        onCreate(sqLiteDatabase);
    }

    public boolean insertIntoSellerTable(int sellerID, String itemName, String weight, String rate, int date_id, int nangCount, Double chungi, Double tax){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("SELLER_ID", sellerID);
        contentValues.put("ITEMNAME", itemName);
        contentValues.put("WEIGHT", weight);
        contentValues.put("RATE", rate);
        contentValues.put("DATE_ID", date_id);
        contentValues.put("NANG_COUNT",nangCount);
        contentValues.put("CHUNGI", chungi);
        contentValues.put("TAX", tax);
        contentValues.put("IS_DEPOSITED", false);
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
    public Cursor getDataFromSellerTable(int date_id, int seller_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cusrsor = db.rawQuery("select * from "+TABLE_NAME1+" where DATE_ID="+date_id+" and SELLER_ID="+seller_id, null);
        return cusrsor;
    }
    public Cursor getSellerDataCount(int seller_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cusrsor = db.rawQuery("select * from "+TABLE_NAME1+" where ID="+seller_id, null);
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

    public boolean deleteItemFromTable3(int entryId, double vegAmount, int c_Id, int date_id)
    {
        SQLiteDatabase db = getWritableDatabase();
        if(db.delete(TABLE_NAME3, "ID="+entryId,null)>0) {
            Cursor cursor = db.rawQuery("select ID from "+TABLE_NAME3+" where DATE_ID="+date_id+" and C_ID="+c_Id, null);
            if(cursor.getCount()==0)
                deleteDataFromTable5(date_id, c_Id);
            return true;
        }
        else {
            return false;
        }

    }

    private void updateCustomerTrans(double vegAmount, int c_id, int date_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("update "+TABLE_NAME5+" set DEPOSIT= DEPOSIT-"+vegAmount+" where C_ID="+c_id+" and DATE_ID="+date_id);

    }

    public boolean deleteItemFromTable2and3(int id)
    {
        SQLiteDatabase db = getWritableDatabase();
        int table2= db.delete(TABLE_NAME2, "ID="+id,null);
        int table5 = db.delete(TABLE_NAME5, "C_ID="+id, null);
        int table3 = db.delete(TABLE_NAME3, "C_ID=" + id, null);

        if(table2>0)
                return true;
        else
            return false;

    }
    public boolean deleteItemFromTable1(int date_id, boolean dateFilter, int seller_id, int item_id, double item_amount)
    {
        SQLiteDatabase db = getWritableDatabase();
        int table1=0;
        if(dateFilter) {
            table1 = db.delete(TABLE_NAME1, "DATE_ID=" + date_id + " and SELLER_ID =" + seller_id, null);
            deleteSellerTrans(date_id, seller_id);
        }
        else {
            table1 = db.delete(TABLE_NAME1, "ID="+item_id, null);
            Cursor cursor = db.rawQuery("select IS_DEPOSITED from "+TABLE_NAME1+" where DATE_ID="+date_id+" and SELLER_ID="+seller_id, null);
            if(cursor.getCount()==0){
                deleteSellerTrans(date_id, seller_id);
            }
        }
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

    public boolean deleteItemFromTable3ForDate(Integer date_id, Integer c_id) {
        SQLiteDatabase db = getWritableDatabase();

        if(db.delete(TABLE_NAME3, "DATE_ID="+date_id+" and C_ID="+c_id,null)>0) {
            deleteDataFromTable5(date_id, c_id);
            return true;
        }
        else
            return false;
    }

    public void checkTable1AndTable3() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME4,"ID not in (select distinct DATE_ID from "+TABLE_NAME1+") and ID not in (select distinct DATE_ID from "+TABLE_NAME3+")",null);
    }

    public boolean insertIntoCustomerTransTable(int date_id, int c_id, Double depositAmount) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select ID, DEPOSIT from "+TABLE_NAME5+" where DATE_ID="+date_id+" and C_ID="+c_id,null);
        ContentValues table5 = new ContentValues();
            if (cursor.getCount() == 0) {
                table5.put("DEPOSIT", depositAmount);
                table5.put("DATE_ID", date_id);
                table5.put("C_ID", c_id);
                long result = db.insert(TABLE_NAME5, null, table5);
                if (result != -1)
                    return true;
                else
                    return false;


            } else {
                int id = 1;
                Double oldDeposit = 0.0;
                while (cursor.moveToNext()) {
                    id = cursor.getInt(0);
                    oldDeposit = cursor.getDouble(1);
                }
                table5.put("DEPOSIT", oldDeposit + depositAmount);
                if (db.update(TABLE_NAME5, table5, "ID=" + id+" and DATE_ID="+date_id, null) > 0)
                    return true;
                else
                    return false;
            }
    }

    public Cursor getDataFromCustomerTrans(Integer date_id, Integer c_id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME5+" where DATE_ID="+date_id+" and C_ID="+c_id, null);
        return  cursor;
    }

    public void deleteDataFromTable5(int date_id, int c_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME5, "DATE_ID="+date_id+" and C_ID="+c_id, null);
    }

    public boolean insertIntoSellerNameTable(String sellerName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put("NAME", sellerName);
        long result = db.insert(TABLE_NAME6, null, data);
        if(result==-1 )
            return false;
        else
            return true;
    }

    public Cursor getDataFromTable6() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME6, null);
        return cursor;
    }

    public boolean deleteSellerName(int sellerId) {
        SQLiteDatabase db = getWritableDatabase();
        if(db.delete(TABLE_NAME6, "ID="+sellerId,null)>0){
            db.delete(TABLE_NAME1,"SELLER_ID="+sellerId, null);
            db.delete(TABLE_NAME7,"SELLER_ID="+sellerId, null);
            return true;
        }
        else
            return false;
    }
    public boolean updateSellerName(int sellerId, String newName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues newData = new ContentValues();
        newData.put("NAME", newName);
        if(db.update(TABLE_NAME6, newData, "ID="+sellerId, null)>0)
            return true;
        else
            return false;
    }
    public boolean insertIntoSellerTransTable(int date_id, int s_id, Double depositAmount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues updateDeposit= new ContentValues();
        updateDeposit.put("IS_DEPOSITED", true);
        db.update(TABLE_NAME1, updateDeposit, "DATE_ID="+date_id+" and SELLER_ID="+s_id, null);
        Cursor cursor = db.rawQuery("select ID, DEPOSIT from "+TABLE_NAME7+" where DATE_ID="+date_id+" and SELLER_ID="+s_id,null);
        ContentValues table7 = new ContentValues();
        if (cursor.getCount() == 0) {
            table7.put("DEPOSIT", depositAmount);
            table7.put("DATE_ID", date_id);
            table7.put("SELLER_ID", s_id);
            long result = db.insert(TABLE_NAME7, null, table7);
            if (result != -1)
                return true;
            else
                return false;


        } else {
            int id = 1;
            Double oldDeposit = 0.0;
            while (cursor.moveToNext()) {
                id = cursor.getInt(0);
                oldDeposit = cursor.getDouble(1);
            }

            table7.put("DEPOSIT", oldDeposit + depositAmount);
            if (db.update(TABLE_NAME7, table7, "ID=" +id+" and DATE_ID="+date_id, null) > 0)
                return true;
            else
                return false;
        }
    }
    public Cursor sellerDataFromTable7(Integer date_id, int sellerId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select ID, DEPOSIT from "+TABLE_NAME7+" where SELLER_ID="+sellerId+" and DATE_ID="+date_id, null);
        return cursor;
    }

    public void deleteSellerTrans(int date_id, int seller_id)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME7, "DATE_ID="+date_id+" and SELLER_ID="+seller_id, null);
    }
}
