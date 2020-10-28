package com.lqcode.adjump.entity.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.lqcode.adjump.entity.db.DBCustomAppEntity;

import java.util.List;

@Dao
public interface DBCustomAppConfigDao {

    @Query("select * from DBCustomAppEntity")
    List<DBCustomAppEntity> getAll();

    @Query("select * from DBCustomAppEntity where packageActivity==:key")
    List<DBCustomAppEntity> getById(String key);

    @Insert
    void addAppConfig(DBCustomAppEntity appConfig);


    @Delete
    void delete(DBCustomAppEntity appConfig);

}
