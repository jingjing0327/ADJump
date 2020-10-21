package com.lqcode.adjump.entity;

import java.util.Map;

public class NetApps extends Result {
    private Map<String, String> data;

    @Override
    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
