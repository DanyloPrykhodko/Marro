package com.weffle.table.point;

import com.weffle.object.BaseObject;

public class Point extends BaseObject<PointData>{
    public Point() {
        super(PointData.id);
        setAutoKey();
    }

    public Point(int id) {
        super(PointData.id, id);
        setAutoKey();
    }
}
