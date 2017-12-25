package com.weffle.table.work;

import com.weffle.object.BaseObject;
import com.weffle.table.employee.Employee;

public class Journal extends BaseObject<JournalData> {
    public Journal() {
        super(JournalData.id);
        setAutoKey();
        putChild(JournalData.employee, Employee.class);
    }

    public Journal(int id) {
        super(JournalData.id, id);
        setAutoKey();
        putChild(JournalData.employee, Employee.class);
    }
}
