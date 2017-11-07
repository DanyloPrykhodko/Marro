package com.weffle.table.refund;

import com.weffle.object.BaseObject;
import com.weffle.table.payment.Payment;
import com.weffle.table.sale.Sale;

public class Refund extends BaseObject<RefundData> {
    public Refund() {
        super(RefundData.id);
        putChild(RefundData.sale, Sale.class);
        putChild(RefundData.payment, Payment.class);
    }

    public Refund(int id) {
        super(RefundData.id, id);
        putChild(RefundData.sale, Sale.class);
        putChild(RefundData.payment, Payment.class);
    }
}
