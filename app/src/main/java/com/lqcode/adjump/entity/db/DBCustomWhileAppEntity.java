package com.lqcode.adjump.entity.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DBCustomWhileAppEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    public String packageName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String toString() {
        return "DBWhileListAppEntity{" +
                "id=" + id +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}
