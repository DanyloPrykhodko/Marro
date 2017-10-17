package com.weffle.object.employee;

import com.weffle.object.BaseObject;

public class Employee extends BaseObject<Data> {
    public Employee() {
        super(Data.id);
        setAutoKey();
    }

    public Employee(int key) {
        super(Data.id, key);
        setAutoKey();
    }
}
