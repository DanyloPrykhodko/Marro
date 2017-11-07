package com.weffle.object;

import org.json.JSONObject;

import java.util.Map;

public interface Base {
    static Base createBase(String table) {
        try {
            String className = String.valueOf(table.charAt(0)).toUpperCase() +
                    table.substring(1);
            String classPath = String.format("com.weffle.table.%s.%s",
                    table, className);
            return  (Base) Class.forName(classPath).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException |
                InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    Base get();

    Object post();

    void put(JSONObject json);

    void delete();

    Base parse(JSONObject json);

    ObjectKey getKey();

    Map getData();

    JSONObject getJSON();
}
