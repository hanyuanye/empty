package com.example.hanyuany.myapplication;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by hanyuany on 13/03/2018.
 */
@Dao
public interface BusinessDao {
    @Query ("Select * FROM BusinessEntity WHERE parentList=:parentList")
    List<BusinessEntity> getBusinessesFromList(String parentList);

    @Query("Select * FROM BusinessEntity WHERE yelpBusinessName=:input")
    List<BusinessEntity> getBusiness(String input)

    @Query("DELETE From BusinessEntity")
    void deleteAll();

    @Insert
    void insertBusiness(BusinessEntity businessEntity);

    @Delete
    void deleteBusiness(BusinessEntity businessEntity);
}
