package com.weffle.object;

import org.json.JSONObject;

import java.sql.SQLException;

public interface IBase {
    JSONObject get() throws SQLException;

    Object post() throws SQLException;

    void put(JSONObject json) throws SQLException;

    void delete() throws SQLException;
}
