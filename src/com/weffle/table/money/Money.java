package com.weffle.table.money;

import com.weffle.Database;
import com.weffle.WebApplication;
import com.weffle.object.Base;
import com.weffle.object.BaseObject;
import com.weffle.table.payment.Payment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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

    public Money[] getAll(Payment payment) {
        try (Database database = WebApplication.getDatabase()) {
            database.connect();
            ArrayList<BaseObject> list = new ArrayList<>();

            String sql = String.format("SELECT * FROM %s WHERE '%s' = ?",
                    getName(), MoneyData.payment.name());
            PreparedStatement statement = database.prepareStatement(sql);
            statement.setObject(1, payment.getKey().getValue());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Base money = new Money();
                list.add((BaseObject) money.parse(resultSet));
            }
            return (Money[]) list.toArray();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
