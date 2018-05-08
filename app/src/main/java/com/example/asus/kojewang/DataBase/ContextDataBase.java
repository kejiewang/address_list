package com.example.asus.kojewang.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by asus on 2018/1/15.
 */

public class ContextDataBase extends SQLiteOpenHelper {

    public static final  String DATABASE_NAME = "CONTEXTDATABASE";
    public static final int version = 1;
    public static final  String TABLE_NAME = "context_table";
    public static final String NAME = "name";
    public  static  final  String NUM = "num";
    public   static final  String GROUP = "groupname";
    public ContextDataBase(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    //创建相应的数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE if not exists " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, " + NUM + " TEXT," + GROUP + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXITS" + TABLE_NAME);
        onCreate(db);
    }
}
