package com.ramich.rsslist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db {

    private static final String DB_NAME = "rsslist.DB";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "rsslist";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_LINK = "link";

    private static final String DB_CREATE =
            "create table " + TABLE_NAME + "(" +
                    KEY_ID + " integer primary key autoincrement, " +
                    KEY_NAME + " text, " +
                    KEY_LINK + " text" +
                    ");";

    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public Db(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return mDB.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public Cursor getOneData(long id) {
        return mDB.query(TABLE_NAME, new String[]{KEY_LINK}, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    // добавить запись в DB_TABLE
    public boolean addRec(String name, String link) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_LINK, link);
        long result = mDB.insert(TABLE_NAME, null, cv);
        return result != -1;
    }

    // удалить запись из DB_TABLE
    public void delRec(long id) {
        mDB.delete(TABLE_NAME, KEY_ID + "= ?",new String[]{String.valueOf(id)});
    }

    public void updateRec(String newText, long id){
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, newText);
        mDB.update(TABLE_NAME, cv, KEY_ID + "=?", new String[]{String.valueOf(id)});
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }
}
