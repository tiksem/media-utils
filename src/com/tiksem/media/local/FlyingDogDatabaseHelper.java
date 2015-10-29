package com.tiksem.media.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by stykhonenko on 29.10.15.
 */
public class FlyingDogDatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    private static FlyingDogDatabaseHelper instance;

    public static FlyingDogDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new FlyingDogDatabaseHelper(context);
        }

        return instance;
    }

    public FlyingDogDatabaseHelper(Context context) {
        super(context, FlyingDogAudioDatabase.class.getName(), null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FlyingDogAudioDatabase.CREATE_AUDIO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
