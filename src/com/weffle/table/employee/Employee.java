package com.weffle.table.employee;

import com.weffle.object.BaseObject;
import com.weffle.table.person.Person;
import com.weffle.table.point.Point;

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
}
