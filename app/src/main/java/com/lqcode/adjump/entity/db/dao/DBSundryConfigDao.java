package com.lqcode.adjump.entity.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lqcode.adjump.entity.db.DBAppConfig;
import com.lqcode.adjump.entity.db.DBSundryConfig;

import java.util.List;

@Dao
public interface DBSundryConfigDao {

    @Query("select * from DBSundryConfig")
    List<DBSundryConfig> getAll();

    @Insert
    void addAppConfig(DBSundryConfig appConfig);

    @Query("DELETE FROM DBSundryConfig")
    void delAll();

}
