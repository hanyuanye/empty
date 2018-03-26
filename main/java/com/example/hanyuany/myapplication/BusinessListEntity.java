package com.example.hanyuany.myapplication2;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by hanyuany on 14/03/2018.
 */
@Entity
public class BusinessListEntity {
    @PrimaryKey(autoGenerate = true)
    public final int id;
    public final String name;

    public BusinessListEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
