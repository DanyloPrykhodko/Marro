package com.weffle.table.transfer;

import com.weffle.object.BaseObject;
import com.weffle.table.point.Point;
import com.weffle.table.unit.Unit;

public class Transfer extends BaseObject<TransferData> {
    public Transfer() {
        super(TransferData.id);
        setAutoKey();
        putChild(TransferData.unit, Unit.class);
        putChild(TransferData.departure, Point.class);
        putChild(TransferData.arrival, Point.class);
    }

    public Transfer(int id) {
        super(TransferData.id, id);
        setAutoKey();
        putChild(TransferData.unit, Unit.class);
        putChild(TransferData.departure, Point.class);
        putChild(TransferData.arrival, Point.class);
    }
}
