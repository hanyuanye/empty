package com.example.hanyuany.myapplication2;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by hanyuany on 13/03/2018.
 */
@Database(entities = {BusinessEntity.class}, version = 1)
public abstract class BusinessDatabase extends RoomDatabase {

    private static final String DB_NAME = "businessDatabase.db";
    private static volatile BusinessDatabase instance;

    static synchronized BusinessDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static BusinessDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                BusinessDatabase.class,
                DB_NAME).build();
    }

    public abstract BusinessDao getBusinessDao();
}
