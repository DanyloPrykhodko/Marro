package com.weffle.table.storage;

import com.weffle.object.Base;
import com.weffle.object.BaseObject;
import com.weffle.table.point.Point;
import com.weffle.table.unit.Unit;
import org.json.JSONObject;

import java.util.Map;

public class Storage extends BaseObject<StorageData> {
    public Storage() {
        super(StorageData.id);
        setAutoKey();
        putChild(StorageData.unit, Unit.class);
        putChild(StorageData.point, Point.class);
    }

    public Storage(int id) {
        super(StorageData.id, id);
        setAutoKey();
        putChild(StorageData.unit, Unit.class);
        putChild(StorageData.point, Point.class);
    }

    public static Object increase(Point point, Unit unit) {
        point.check();
        unit.check();
        Base[] allStorage = new Storage().getAll();
        for (Base storage : allStorage) {
            Map data = storage.getData();
            if (!data.get(StorageData.point).equals(point.getKey().getValue()))
                continue;
            if (!data.get(StorageData.unit).equals(unit.getKey().getValue()))
                continue;
            storage.put(new JSONObject(
                    ((int) data.get(StorageData.quantity)) + 1));
            return storage.getKey().getValue();
        }
        Storage storage = new Storage();
        storage.putData(StorageData.point, point.getKey().getValue());
        storage.putData(StorageData.unit, unit.getKey().getValue());
        storage.putData(StorageData.quantity, 1);
        return storage.post();
    }

    public static Object decrease(Point point, Unit unit) {
        point.check();
        unit.check();
        Base[] allStorage = new Storage().getAll();
        for (Base storage : allStorage) {
            Map data = storage.getData();
            if (!data.get(StorageData.point).equals(point.getKey().getValue()))
                continue;
            if (!data.get(StorageData.unit).equals(unit.getKey().getValue()))
                continue;
            int quantity = (int) data.get(StorageData.quantity);
            if (quantity <= 0)
                throw new RuntimeException("The point do not contains unit!");
            storage.put(new JSONObject(quantity - 1));
            return storage.getKey().getValue();
        }
        throw new RuntimeException("The point do not contains unit!");
    }
}
