package com.weffle.table.money;

import com.weffle.object.BaseObject;
import com.weffle.table.payment.Payment;

public class Money extends BaseObject<MoneyData> {
    public Money() {
        super(MoneyData.id);
        setAutoKey();
        putChild(MoneyData.payment, Payment.class);
    }

    public Money(int id) {
        super(MoneyData.id, id);
        setAutoKey();
        putChild(MoneyData.payment, Payment.class);
    }
}
