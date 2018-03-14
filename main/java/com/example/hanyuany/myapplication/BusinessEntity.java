package com.example.hanyuany.myapplication;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by hanyuany on 13/03/2018.
 */
@Entity
public class BusinessEntity {
    @PrimaryKey(autoGenerate = true)
    int id;

    public final String parentList;
    public final String yelpBusinessName;
    public final String yelpBusinessId;
    public final String yelpBusinessImageUrl;


    public BusinessEntity(String parentList, String yelpBusinessName, String yelpBusinessId, String yelpBusinessImageUrl) {
        this.parentList = parentList;
        this.yelpBusinessName = yelpBusinessName;
        this.yelpBusinessId = yelpBusinessId;
        this.yelpBusinessImageUrl = yelpBusinessImageUrl;
    }

    public boolean equals(BusinessEntity entity) {
        if (entity.parentList == this.parentList &&
            entity.yelpBusinessName == this.yelpBusinessName) {
            return true;
        }
        return false;
    }
}
