package com.weffle.table.storage;

import com.weffle.object.Base;
import com.weffle.object.BaseObject;
import com.weffle.table.point.Point;
import com.weffle.table.unit.Unit;
import org.json.JSONObject;

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
        for (Storage storage : (Storage[]) allStorage) {
            if (!storage.get(StorageData.point).equals(point))
                continue;
            if (!storage.get(StorageData.unit).equals(unit))
                continue;
            storage.put(new JSONObject().put(StorageData.quantity.name(),
                    ((int) storage.get(StorageData.quantity)) + 1));
            return storage.getKey().getValue();
        }
        Storage storage = new Storage();
        storage.put(StorageData.point, point.getKey().getValue());
        storage.put(StorageData.unit, unit.getKey().getValue());
        storage.put(StorageData.quantity, 1);
        return storage.post();
    }

    public static Object decrease(Point point, Unit unit) {
        point.check();
        unit.check();
        Base[] allStorage = new Storage().getAll();
        for (Storage storage : (Storage[]) allStorage) {
            if (!storage.get(StorageData.point).equals(point))
                continue;
            if (!storage.get(StorageData.unit).equals(unit))
                continue;
            int quantity = (int) storage.get(StorageData.quantity);
            if (quantity <= 0)
                throw new RuntimeException("The point do not contains unit!");
            storage.put(new JSONObject().put(StorageData.quantity.name(),
                    quantity - 1));
            return storage.getKey().getValue();
        }
        throw new RuntimeException("The point do not contains unit!");
    }
}
