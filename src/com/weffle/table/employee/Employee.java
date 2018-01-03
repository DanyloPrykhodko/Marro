package com.weffle.table.employee;

import com.weffle.object.Base;
import com.weffle.object.BaseObject;
import com.weffle.table.admin.Admin;
import com.weffle.table.admin.AdminData;
import com.weffle.table.admin.AdminRank;
import com.weffle.table.journal.Journal;
import com.weffle.table.journal.JournalData;
import com.weffle.table.person.Person;
import com.weffle.table.point.Point;
import org.json.JSONObject;

import java.sql.Timestamp;

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

    public static void checkLocation(Employee employee, Point point) {
        employee.get();
        Base employeePoint = (Base) employee.get(EmployeeData.point);
        if (employeePoint == null)
            throw new RuntimeException("Location is not set!");
        if (!employeePoint.equals(point))
            throw new RuntimeException("Location is not match!");
    }

    public static void checkLocation(Admin admin, Point point) {
        if (!AdminRank.valueOf((String) admin.get(AdminData.rank))
                .equals(AdminRank.Global))
            checkLocation((Employee) admin.get(AdminData.employee),
                    point);
    }

    public JSONObject calculateSalary() {
        Timestamp last = (Timestamp) get(EmployeeData.lastDate);
        Base[] allJournals = new Journal().getAll();
        long duration = 0;
        for (Journal journal : (Journal[]) allJournals) {
            if (!((Employee) journal.get(JournalData.employee))
                    .getKey().equals(getKey()))
                continue;
            Timestamp came = (Timestamp) journal.get(JournalData.came);
            Timestamp went = (Timestamp) journal.get(JournalData.went);
            if (last.before(came))
                duration += (went == null ? System.currentTimeMillis() :
                        went.getTime()) - came.getTime();
            else if (last.before(went))
                duration += (went == null ? System.currentTimeMillis() :
                        went.getTime()) - last.getTime();
        }
        double balance = (double) get(EmployeeData.balance);
        balance += duration / (1000.0 * 3600.0)
                * (double) get(EmployeeData.rate);
        JSONObject json = new JSONObject();
        json.put(EmployeeData.balance.name(), balance);
        json.put(EmployeeData.lastDate.name(),
                new Timestamp(System.currentTimeMillis()));
        return put(json);
    }

    public JSONObject offset(double amount) {
        double balance = (double) get(EmployeeData.balance);
        return put(new JSONObject().put(EmployeeData.balance.name(),
                balance + amount));
    }

    public JSONObject paySalary(double amount) {
        double balance = (double) get(EmployeeData.balance);
        if (amount > balance)
            throw new RuntimeException("The salary amount bigger than " +
                    "employee's balance!");
        if (amount < 0)
            throw new RuntimeException("The salary amount is negative!");
        return offset(-1.0 * amount);
    }

    public JSONObject fine(double amount) {
        if (amount < 0)
            throw new RuntimeException("The fine amount is negative!");
        return  offset(-1.0 * amount);
    }

}
