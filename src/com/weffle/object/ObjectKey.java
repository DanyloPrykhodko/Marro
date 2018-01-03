package com.weffle.object;

/**
 * This class for using as key with Enum and Object value.
 *
 * @param <E> Enum class.
 * @author Danylo Prykhodko
 */
public class ObjectKey<E extends Enum> {
    /**
     * Enum of key.
     */
    private E e;

    /**
     * Value of key.
     */
    private Object value;

    /**
     * Constructor for implementation.  
     * 
     * @param e Enum of key.
     * @param value Value of key.
     */
    ObjectKey(E e, Object value) {
        this.e = e;
        this.value = value;
    }

    /**
     * Get enum of key.
     * 
     * @return Enum of key.
     */
    public E getEnum() {
        return e;
    }

    /**
     * Set enum of key.
     * 
     * @param e Enum of key.
     */
    public void setEnum(E e) {
        this.e = e;
    }

    /**
     * Get value of key.
     * 
     * @return Value of key.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Set value of key.
     *
     * @param value Value of key.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || value == null ||
                !(o instanceof ObjectKey)) return false;
        ObjectKey key = (ObjectKey) o;
        return key.getValue() != null && e.equals(key.getEnum()) &&
                value.equals(key.getValue());
    }
}
