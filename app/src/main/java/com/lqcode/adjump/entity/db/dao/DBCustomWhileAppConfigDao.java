package com.lqcode.adjump.entity.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lqcode.adjump.entity.db.DBCustomWhileAppEntity;

import java.util.List;

@Dao
public interface DBCustomWhileAppConfigDao {

    @Insert
    void add(DBCustomWhileAppEntity appConfig);

    @Query("DELETE FROM DBCustomWhileAppEntity where packageName==:packageName")
    void delByPackageName(String packageName);

    @Query("select count(*) from DBCustomWhileAppEntity where packageName==:packageName")
    int getCountByPackage(String packageName);

    @Query("select * from DBCustomWhileAppEntity")
    List<DBCustomWhileAppEntity> getAll();

}
