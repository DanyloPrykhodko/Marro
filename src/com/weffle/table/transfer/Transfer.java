package com.weffle.table.transfer;

import com.weffle.object.BaseObject;
import com.weffle.table.point.Point;
import com.weffle.table.unit.Unit;

public class Transfer extends BaseObject<TransferData> {
    public Transfer() {
        super(TransferData.id);
        putChild(TransferData.unit, Unit.class);
        putChild(TransferData.from, Point.class);
        putChild(TransferData.to, Point.class);
    }

    public Transfer(int id) {
        super(TransferData.id, id);
        putChild(TransferData.unit, Unit.class);
        putChild(TransferData.from, Point.class);
        putChild(TransferData.to, Point.class);
    }
}
