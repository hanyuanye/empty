package com.example.hanyuany.myapplication;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by hanyuany on 14/03/2018.
 */

@Database(entities = {BusinessEntity.class}, version = 1)
public abstract class BusinessListDatabase extends RoomDatabase {

    private static final String DB_NAME = "businessListDatabase.db";
    private static volatile BusinessListDatabase instance;

    static synchronized BusinessListDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static BusinessListDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                BusinessListDatabase.class,
                DB_NAME).build();
    }

    public abstract BusinessDao getBusinessListDao();
}