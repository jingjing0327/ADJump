package com.lqcode.adjump.entity.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lqcode.adjump.entity.db.DBCustomAppEntity;

import java.util.List;

@Dao
public interface DBCustomAppConfigDao {

    @Query("select * from DBCustomAppEntity")
    List<DBCustomAppEntity> getAll();

    @Insert
    void addAppConfig(DBCustomAppEntity appConfig);

}
