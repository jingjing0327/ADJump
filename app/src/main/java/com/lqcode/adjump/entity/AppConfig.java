package com.lqcode.adjump.entity;

import java.util.Map;

public class AppConfig {
    private Map<String, String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "map=" + map +
                '}';
    }
}
