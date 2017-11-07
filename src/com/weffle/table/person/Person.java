package com.weffle.table.person;

import com.weffle.object.BaseObject;

public class Person extends BaseObject<PersonData> {
    public Person() {
        super(PersonData.id);
        setAutoKey();
    }

    public Person(int id) {
        super(PersonData.id, id);
        setAutoKey();
    }
}