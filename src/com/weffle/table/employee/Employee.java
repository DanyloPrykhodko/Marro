package com.weffle.table.employee;

import com.weffle.object.Base;
import com.weffle.object.BaseObject;
import com.weffle.table.admin.Admin;
import com.weffle.table.admin.AdminData;
import com.weffle.table.admin.AdminRank;
import com.weffle.table.person.Person;
import com.weffle.table.point.Point;
import com.weffle.table.work.Journal;
import com.weffle.table.work.JournalData;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Map;

public class Employee extends BaseObject<EmployeeData> {
    public Employee() {
        super(EmployeeData.id);
        setAutoKey();
        putChild(EmployeeData.person, Person.class);
        putChild(EmployeeData.point, Point.class);
    }

    public Employee(int id) {
        super(EmployeeData.id, id);
        setAutoKey();
        putChild(EmployeeData.person, Person.class);
        putChild(EmployeeData.point, Point.class);
    }

    public static void checkLocation(Employee employee, int pointId) {
        employee.get();
        Base employeePoint = (Base) employee.getData().get(EmployeeData.point);
        if (employeePoint == null)
            throw new RuntimeException("Location is not set!");
        if (!employeePoint.getKey().getValue().equals(pointId))
            throw new RuntimeException("Location is not match!");
    }

    public static void checkLocation(Admin admin, int pointId) {
        if (!AdminRank.valueOf((String) admin.getData().get(AdminData.rank))
                .equals(AdminRank.Global))
            checkLocation((Employee) admin.getData().get(AdminData.employee),
                    pointId);
    }

    public JSONObject calculateSalary() {
        Timestamp last = (Timestamp) getData().get(EmployeeData.lastDate);
        Base[] allJournals = new Journal().getAll();
        long duration = 0;
        for (Base work : allJournals) {
            Map data = work.getData();
            if (!((Employee) data.get(JournalData.employee)).getKey().getValue()
                    .equals(getKey().getValue()))
                continue;
            Timestamp came = (Timestamp) data.get(JournalData.came);
            Timestamp went = (Timestamp) data.get(JournalData.went);
            if (last.before(came))
                duration += (went == null ? System.currentTimeMillis() :
                        went.getTime()) - came.getTime();
            else if (last.before(went))
                duration += (went == null ? System.currentTimeMillis() :
                        went.getTime()) - last.getTime();
        }
        double balance = (double) getData().get(EmployeeData.balance);
        balance += duration / (1000.0 * 3600.0)
                * (double) getData().get(EmployeeData.rate);
        JSONObject json = new JSONObject();
        json.put(EmployeeData.balance.name(), balance);
        json.put(EmployeeData.lastDate.name(),
                new Timestamp(System.currentTimeMillis()));
        return put(json);
    }

    public JSONObject paySalary(double amount) {
        double balance = (double) getData().get(EmployeeData.balance);
        if (amount > balance)
            throw new RuntimeException("The salary amount bigger than " +
                    "employee's balance!");
        if (amount < 0)
            throw new RuntimeException("The salary amount is negative!");
        return put(new JSONObject().put(EmployeeData.balance.name(),
                balance - amount));
    }

    public JSONObject fine(double amount) {
        double balance = (double) getData().get(EmployeeData.balance);
        if (amount < 0)
            throw new RuntimeException("The fine amount is negative!");
        return put(new JSONObject().put(EmployeeData.balance.name(),
                balance - amount));
    }

}
