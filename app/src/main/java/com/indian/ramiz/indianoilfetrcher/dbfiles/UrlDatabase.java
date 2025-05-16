package com.indian.ramiz.indianoilfetrcher.dbfiles;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {EntityPojo.class}, version = 1)
public abstract class UrlDatabase extends RoomDatabase {

    public abstract DaoFile urlDao();

    private static volatile UrlDatabase INSTANCE;

    public static UrlDatabase getDB(Context context){
        if(INSTANCE == null){
            synchronized (UrlDatabase.class){
                INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        UrlDatabase.class,
                        "url_database"
                ).build();
            }
        }
        return INSTANCE;
    }
}
