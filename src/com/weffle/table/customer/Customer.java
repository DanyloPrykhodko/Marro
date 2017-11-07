package com.weffle.table.customer;

import com.weffle.object.BaseObject;
import com.weffle.table.person.Person;

public class Customer extends BaseObject<CustomerData>{
    public Customer() {
        super(CustomerData.id);
        setAutoKey();
        putChild(CustomerData.person, Person.class);
    }

    public Customer(int id) {
        super(CustomerData.id, id);
        setAutoKey();
        putChild(CustomerData.person, Person.class);
    }
}
