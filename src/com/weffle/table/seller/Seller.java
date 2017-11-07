package com.weffle.table.seller;

import com.weffle.object.BaseObject;
import com.weffle.table.employee.Employee;

public class Seller extends BaseObject<SellerData> {
    public Seller() {
        super(SellerData.id);
        setAutoKey();
        putChild(SellerData.employee, Employee.class);
    }

    public Seller(int id) {
        super(SellerData.id, id);
        setAutoKey();
        putChild(SellerData.employee, Employee.class);
    }
}
