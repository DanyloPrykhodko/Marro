package com.weffle.table.work;

import com.weffle.object.BaseObject;
import com.weffle.table.employee.Employee;

public class Work extends BaseObject<WorkData> {
    public Work() {
        super(WorkData.id);
        putChild(WorkData.employee, Employee.class);
    }

    public Work(int id) {
        super(WorkData.id, id);
        putChild(WorkData.employee, Employee.class);
    }
}
