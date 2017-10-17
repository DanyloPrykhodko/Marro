package com.weffle.object.unit;

import com.weffle.object.BaseObject;
import org.json.JSONArray;

import java.sql.SQLException;

public class Unit extends BaseObject<Data> {
    public Unit() {
        super(Data.barcode);
    }

    public Unit(String barcode) {
        super(Data.barcode, barcode);
    }

    @Deprecated
    public static JSONArray all() throws SQLException {
        Object[] allId = new Unit().allKeys();
        JSONArray array = new JSONArray();
        for (Object o : allId)
            array.put(new Unit(o.toString()).get());
        return array;
    }
}
