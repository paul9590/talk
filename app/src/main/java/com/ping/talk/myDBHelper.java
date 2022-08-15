package com.ping.talk;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;




public class myDBHelper extends SQLiteOpenHelper {

    public myDBHelper(Context context) {
        super(context, "Pingmo", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE TalkTB ( userID VARCHAR(10) PRIMARY KEY);");
        db.execSQL("CREATE TABLE ChatLengthTB (Length int default 0);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS TalkTB");
        db.execSQL("DROP TABLE IF EXISTS ChatLengthTB");
        onCreate(db);

    }
}