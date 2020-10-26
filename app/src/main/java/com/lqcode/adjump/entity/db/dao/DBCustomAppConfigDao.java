package com.lqcode.adjump.entity.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lqcode.adjump.entity.db.DBCustomAppConfig;

import java.util.List;

@Dao
public interface DBCustomAppConfigDao {

    @Query("select * from DBCustomAppConfig")
    List<DBCustomAppConfig> getAll();

    @Insert
    void addAppConfig(DBCustomAppConfig appConfig);

}
