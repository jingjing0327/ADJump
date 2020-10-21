package com.lqcode.adjump.entity.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.lqcode.adjump.entity.db.DBAppConfig;
import com.lqcode.adjump.entity.db.DBAutoJumpConfig;

import java.util.List;

@Dao
public interface DBAutoJumpConfigDao {

    @Query("select * from DBAutoJumpConfig")
    List<DBAutoJumpConfig> getAll();

    @Insert
    void addAutoJumpConfig(DBAutoJumpConfig appConfig);

    @Query("DELETE FROM DBAppConfig")
    void delAll();

    @Query("select * from DBAutoJumpConfig where packageActivity ==:packageActivity")
    List<DBAutoJumpConfig> getByPackageActivity(String packageActivity);

}
