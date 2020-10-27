package com.lqcode.adjump.entity.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lqcode.adjump.entity.db.DBAutoJumpEntity;

import java.util.List;

@Dao
public interface DBAutoJumpConfigDao {

    @Query("select * from DBAutoJumpEntity")
    List<DBAutoJumpEntity> getAll();

    @Insert
    void addAutoJumpConfig(DBAutoJumpEntity appConfig);

    @Query("DELETE FROM DBAppConfig")
    void delAll();

    @Query("select * from DBAutoJumpEntity where packageActivity ==:packageActivity")
    List<DBAutoJumpEntity> getByPackageActivity(String packageActivity);


    @Query("select count(*) from DBAutoJumpEntity")
    int getCount();


}
