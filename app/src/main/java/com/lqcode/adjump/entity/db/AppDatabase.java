package com.lqcode.adjump.entity.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.lqcode.adjump.entity.db.dao.DBAppConfigDao;
import com.lqcode.adjump.entity.db.dao.DBAutoJumpConfigDao;
import com.lqcode.adjump.entity.db.dao.DBCustomAppConfigDao;
import com.lqcode.adjump.entity.db.dao.DBCustomWhileAppConfigDao;
import com.lqcode.adjump.entity.db.dao.DBSundryConfigDao;

@Database(entities = {DBAppConfig.class, DBAutoJumpEntity.class, DBCustomAppEntity.class, DBCustomWhileAppEntity.class, DBSundryConfig.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DBAppConfigDao appConfigDao();

    public abstract DBAutoJumpConfigDao autoJumpConfigDao();

    public abstract DBCustomAppConfigDao customAppConfigDao();

    public abstract DBCustomWhileAppConfigDao whileAppConfigDao();

    public abstract DBSundryConfigDao sundryConfigDao();
}
