package com.weffle.table.unit;

import com.weffle.object.BaseObject;

public class Unit extends BaseObject<UnitData> {
    public Unit() {
        super(UnitData.barcode);
    }

    public Unit(String barcode) {
        super(UnitData.barcode, barcode);
    }
}
