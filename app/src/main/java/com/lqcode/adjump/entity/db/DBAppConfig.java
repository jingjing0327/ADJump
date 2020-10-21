package com.lqcode.adjump.entity.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DBAppConfig {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    public String packageActivity;

    @ColumnInfo
    private String buttonName;

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

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    @Override
    public String toString() {
        return "DBAppConfig{" +
                "id=" + id +
                ", packageActivity='" + packageActivity + '\'' +
                ", buttonName='" + buttonName + '\'' +
                '}';
    }
}
