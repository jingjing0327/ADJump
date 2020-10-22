package com.lqcode.adjump.entity.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.lqcode.adjump.entity.db.dao.DBAppConfigDao;
import com.lqcode.adjump.entity.db.dao.DBAutoJumpConfigDao;

@Database(entities = {DBAppConfig.class, DBAutoJumpConfig.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DBAppConfigDao appConfigDao();

    public abstract DBAutoJumpConfigDao autoJumpConfigDao();
}
