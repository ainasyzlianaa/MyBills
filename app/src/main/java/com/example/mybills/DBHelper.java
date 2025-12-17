package com.example.mybills;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "MyBills.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bills(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "month TEXT," +
                "unit INTEGER," +
                "total REAL," +
                "rebate REAL," +
                "final REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS bills");
        onCreate(db);
    }

    public void insertBill(String month, int unit, double total, double rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("month", month);
        cv.put("unit", unit);
        cv.put("total", total);
        cv.put("rebate", rebate);
        cv.put("final", finalCost);
        db.insert("bills", null, cv);
    }

    public Cursor getAllBills() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM bills", null);
    }

    public Cursor getBillById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM bills WHERE id = ?",
                new String[]{String.valueOf(id)});
    }

    public void updateBill(int id, int unit, double total, double rebate, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("unit", unit);
        cv.put("total", total);
        cv.put("rebate", rebate);
        cv.put("final", finalCost);
        db.update("bills", cv, "id = ?", new String[]{String.valueOf(id)});
    }

    public int deleteBill(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("bills", "id=?", new String[]{String.valueOf(id)});
    }

}
