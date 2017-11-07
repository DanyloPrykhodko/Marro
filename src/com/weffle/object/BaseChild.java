package com.weffle.object;

public class BaseChild<B extends Base> {
    private Class<B> childrenClass;
    private B children;

    public BaseChild(Class<B> childrenClass) {
        this.childrenClass = childrenClass;
    }

    public B get(Object key) {
        try {
            children = childrenClass.newInstance();
            children.getKey().setValue(key);
            children.get();
            return children;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public B getChildren() {
        return children;
    }
}