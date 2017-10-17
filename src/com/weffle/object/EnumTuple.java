package com.weffle.object;

@SuppressWarnings("WeakerAccess")
public class EnumTuple<E extends Enum, V> {
    private E e;
    private V value;

    public EnumTuple(E e, V value) {
        this.e = e;
        this.value = value;
    }

    public E getEnum() {
        return e;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
