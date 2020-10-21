package com.lqcode.adjump.entity.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lqcode.adjump.entity.db.DBAppConfig;

import java.util.List;

@Dao
public interface DBAppConfigDao {

    @Query("select * from DBAppConfig")
    List<DBAppConfig> getAll();

    @Insert
    void addAppConfig(DBAppConfig appConfig);

    @Query("DELETE FROM DBAppConfig")
    void delAll();

}
