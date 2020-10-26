package com.lqcode.adjump.entity.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DBCustomAppConfig {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    public String packageActivity;

    @ColumnInfo
    private float lastChooseX;

    @ColumnInfo
    private float lastChooseY;

    public float getLastChooseX() {
        return lastChooseX;
    }

    public void setLastChooseX(float lastChooseX) {
        this.lastChooseX = lastChooseX;
    }

    public float getLastChooseY() {
        return lastChooseY;
    }

    public void setLastChooseY(float lastChooseY) {
        this.lastChooseY = lastChooseY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageActivity() {
        return packageActivity;
    }

    public void setPackageActivity(String packageActivity) {
        this.packageActivity = packageActivity;
    }


}
