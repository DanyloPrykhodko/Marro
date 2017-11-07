package com.weffle.table.storage;

import com.weffle.object.BaseObject;
import com.weffle.table.point.Point;
import com.weffle.table.unit.Unit;

public class Storage extends BaseObject<StorageData> {
    public Storage() {
        super(StorageData.id);
        putChild(StorageData.unit, Unit.class);
        putChild(StorageData.point, Point.class);
    }

    public Storage(int id) {
        super(StorageData.id, id);
        putChild(StorageData.unit, Unit.class);
        putChild(StorageData.point, Point.class);
    }
}
