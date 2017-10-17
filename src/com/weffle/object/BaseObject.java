package com.weffle.object;

import com.weffle.Database;
import com.weffle.WebApplication;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class BaseObject<E extends Enum> implements IBase {
    private boolean autoKey;
    private Class<E> eClass;
    private EnumTuple<E, Object> key;
    private ObjectData<E> data;

    private BaseObject(Class<E> eClass) {
        this.eClass = eClass;
        data = new ObjectData<>();
    }

    @SuppressWarnings("unchecked")
    public BaseObject(E e) {
        this((Class<E>) e.getClass());
        setKey(e, null);
    }

    @SuppressWarnings("unchecked")
    public BaseObject(E e, Object key) {
        this((Class<E>) e.getClass());
        setKey(e, key);
    }

    public JSONObject get() throws SQLException {
        return load().getJsonData();
    }

    public Object post() throws SQLException {
        if (!autoKey && contains())
            throw new IllegalArgumentException(String.format("Database already contains an object where %s = %s", key.getEnum().name(), key.getValue()));
        Database database = WebApplication.getDatabase();
        database.connect();
        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();
        if (!autoKey) {
            names.append(key.getEnum().name()).append(", ");
            values.append(valueToString(key.getValue())).append(", ");
        }
        for (E e : eClass.getEnumConstants()) {
            Object o = data.get(e);
            if (e.equals(key.getEnum()))
                continue;
            if (o == null)
                continue;
            names.append(e.name()).append(", ");
            values.append(valueToString(o)).append(", ");
        }
        if (names.toString().endsWith(", "))
            names.delete(names.length() - 2, names.length());
        if (values.toString().endsWith(", "))
            values.delete(values.length() - 2, values.length());
        database.getStatement().execute(String.format("INSERT INTO %s (%s) VALUES (%s)", getClassName(), names, values));
        database.close();
        if (autoKey)
            return lastKey();
        return key.getValue();
    }

    public void put(JSONObject json) throws SQLException {
        checkAvoidError();
        Database database = WebApplication.getDatabase();
        database.connect();
        for (E e : eClass.getEnumConstants()) {
            String name = e.name();
            if (!json.has(name))
                continue;
            database.getStatement().execute(String.format("UPDATE %s SET %s = %s WHERE %s = %s", getClassName(), name, valueToString(json.get(name)), key.getEnum().name(), key.getValue().toString()));
        }
        database.close();
    }

    public void delete() throws SQLException {
        checkAvoidError();
        Database database = WebApplication.getDatabase();
        database.connect();
        database.getStatement().execute(String.format("DELETE FROM %s WHERE %s = %s", getClassName(), key.getEnum().name(), valueToString(key.getValue())));
        database.close();
    }

    public BaseObject<E> parse(JSONObject json) {
        for (E e : eClass.getEnumConstants()) {
            String name = e.name();
            if (!json.has(name))
                continue;
            Object value = json.get(name);
            if (e.equals(key.getEnum())) {
                if (!autoKey)
                    key.setValue(value);
            } else
                dataPut(e, value);
        }
        return this;
    }

    public BaseObject<E> load() throws SQLException {
        checkAvoidError();
        Database database = WebApplication.getDatabase();
        database.connect();
        ResultSet resultSet = database.getStatement().executeQuery(String.format("SELECT * FROM %s WHERE %s = %s", getClassName(), key.getEnum().name(), valueToString(key.getValue())));
        resultSet.next();
        for (E e : eClass.getEnumConstants())
            dataPut(e, resultSet.getObject(e.name()));
        database.close();
        return this;
    }

    public boolean contains() throws SQLException {
        Database database = WebApplication.getDatabase();
        database.connect();
        String keyName = key.getEnum().name();
        ResultSet resultSet = database.getStatement().executeQuery(String.format("SELECT %s FROM %s WHERE %s = %s", keyName, getClassName(), keyName, valueToString(key.getValue())));
        boolean contains = resultSet.next();
        database.close();
        return contains;
    }

    private void checkAvoidError() throws SQLException {
        if (!contains())
            throw new IllegalArgumentException(String.format("Database don't contains an object where %s = %s", key.getEnum().name(), key.getValue()));
    }

    private String getClassName() {
        return getClass().getSimpleName().toLowerCase();
    }

    /** Key manage. */

    public void setAutoKey() {
        this.autoKey = true;
    }

    public EnumTuple<E, Object> getKey() {
        return key;
    }

    public void setKey(E e, Object key) {
        this.key = new EnumTuple<>(e, key);
    }

    public Object lastKey() throws SQLException {
        Database database = WebApplication.getDatabase();
        database.connect();
        ResultSet resultSet = database.getStatement().executeQuery(String.format("SELECT %s FROM %s ORDER BY %s DESC LIMIT 1", key.getEnum().name(), getClassName(), key.getEnum().name()));
        if (!resultSet.next())
            throw new NullPointerException();
        Object last = resultSet.getObject(key.getEnum().name());
        database.close();
        return last;
    }

    public Object[] allKeys() throws SQLException {
        Database database = WebApplication.getDatabase();
        database.connect();
        String keyName = key.getEnum().name();
        ResultSet resultSet = database.getStatement().executeQuery(String.format("SELECT %s FROM %s", keyName, getClassName()));
        ArrayList<Object> keys = new ArrayList<>();
        while (resultSet.next())
            keys.add(resultSet.getObject(keyName));
        database.close();
        return keys.toArray();
    }

    /** ObjectData manage. */

    public ObjectData<E> getData() {
        return data;
    }

    public void dataPut(E e, Object o) {
        data.put(e, o);
    }

    public Object dataRemove(E e) {
        return data.remove(e);
    }

    private JSONObject getJsonData() {
        JSONObject json = new JSONObject();
        for (ObjectData.Entry<E, Object> entry : data.entrySet()) {
            if (entry.getValue() == null)
                continue;
            json.put(entry.getKey().name(), entry.getValue());
        }
        return json;
    }

    private String valueToString(Object o) {
        return (o instanceof String) ? "\'" + o + "\'" : o.toString();
    }
}
