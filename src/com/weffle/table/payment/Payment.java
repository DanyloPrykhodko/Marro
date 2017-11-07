package com.weffle.table.payment;

import com.weffle.object.BaseObject;

public class Payment extends BaseObject<PaymentData> {
    public Payment() {
        super(PaymentData.id);
        setAutoKey();
    }

    public Payment(int id) {
        super(PaymentData.id, id);
        setAutoKey();
    }
}
