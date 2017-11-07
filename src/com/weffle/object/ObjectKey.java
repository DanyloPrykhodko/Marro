package com.weffle.object;

public class ObjectKey<E extends Enum> {
    private E e;
    private Object value;

    public ObjectKey(E e, Object value) {
        this.e = e;
        this.value = value;
    }

    public E getEnum() {
        return e;
    }

    public void setEnum(E e) {
        this.e = e;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
