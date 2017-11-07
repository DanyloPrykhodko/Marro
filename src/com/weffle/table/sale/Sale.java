package com.weffle.table.sale;

import com.weffle.object.BaseObject;
import com.weffle.table.customer.Customer;
import com.weffle.table.payment.Payment;
import com.weffle.table.point.Point;
import com.weffle.table.seller.Seller;

public class Sale extends BaseObject<SaleData> {
    public Sale() {
        super(SaleData.id);
        setAutoKey();
        putChild(SaleData.payment, Payment.class);
        putChild(SaleData.customer, Customer.class);
        putChild(SaleData.seller, Seller.class);
        putChild(SaleData.point, Point.class);
    }

    public Sale(int id) {
        super(SaleData.id, id);
        setAutoKey();
        putChild(SaleData.payment, Payment.class);
        putChild(SaleData.customer, Customer.class);
        putChild(SaleData.seller, Seller.class);
        putChild(SaleData.point, Point.class);
    }
}
