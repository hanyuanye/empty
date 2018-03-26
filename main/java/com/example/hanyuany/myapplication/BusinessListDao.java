package com.example.hanyuany.myapplication2;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by hanyuany on 14/03/2018.
 */
@Dao
public interface BusinessListDao {
    @Query("Select * FROM BusinessListEntity")
    List<BusinessListEntity> getList();

    @Insert
    void insertListEntity(BusinessListEntity b);

    @Delete

    void deleteListEntity(BusinessListEntity d);

}
