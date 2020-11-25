package com.lqcode.adjump.entity.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DBSundryConfig {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String key;
    private String values;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "DBSundryConfig{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", values='" + values + '\'' +
                '}';
    }
}
