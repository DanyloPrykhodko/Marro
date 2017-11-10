package com.weffle.object;

import com.weffle.Database;
import com.weffle.WebApplication;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * The BaseObject is abstract class to simplify working with database objects.
 *
 * @param <E> The enum of all required fields in database. The enum contains
 *           names of fields.
 * @author Danylo Prykhodko
 */
public abstract class BaseObject<E extends Enum<E>> implements Base {

    /**
     * The <E> class.
     * To gain access to all fields.
     */
    private Class<E> e;

    /**
     * The object key.
     */
    private ObjectKey<E> key;

    /**
     * The object data.
     */
    private HashMap<E, Object> data;

    /**
     * The object children. The <E> is key for child object.
     *
     * @see BaseChild
     */
    private HashMap<E, BaseChild> children;

    /**
     * The key is auto increment.
     */
    private boolean autoKey;

    /**
     * Private constructor. Only for implementation other constructors.
     *
     * @param e The <E> class of object's data.
     */
    public BaseObject(Class<E> e) {
        this.e = e;
        this.data = new HashMap<>();
        this.children = new HashMap<>();
    }

    /**
     * That constructor for implementation with unknown key. Also for auto
     * increment key.
     *
     * @param e The <E> for key field.
     */
    public BaseObject(E e) {
        this(e.getDeclaringClass());
        setKey(e, null);
    }

    /**
     * That constructor for implementation with key.
     *
     * @param e The <E> indication for key filed.
     * @param key The key of element.
     */
    public BaseObject(E e, Object key) {
        this(e.getDeclaringClass());
        setKey(e, key);
    }

    /**
     * Get object. Load data and children from the database.
     *
     * @return The loaded instance.
     */
    @Override
    public BaseObject<E> get() {
        checkMissing();
        try (Database database = WebApplication.getDatabase()) {
            database.connect();

            String sql = String.format("SELECT * FROM %s WHERE %s = ?",
                    getName(), key.getEnum().name());
            PreparedStatement statement = database.prepareStatement(sql);
            statement.setObject(1, key.getValue());
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            for (E e : e.getEnumConstants()) {
                Object value = resultSet.getObject(e.name());
                if (value == null)
                    continue;
                data.put(e, value);
                if (children.containsKey(e))
                    children.get(e).get(value);
            }
            return this;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Post object. Add new object with current data to the database.
     *
     * @return The key of object in database.
     */
    @Override
    public Object post() {
        if (!autoKey && contains())
            throw new IllegalArgumentException(String.format("Database " +
                    "already contains an object where %s = %s",
                    key.getEnum().name(), key.getValue()));
        try (Database database = WebApplication.getDatabase()) {
            database.connect();
            StringBuilder names = new StringBuilder();
            StringBuilder values = new StringBuilder();
            if (!autoKey) {
                names.append(key.getEnum().name());
                values.append("?");
            }
            for (HashMap.Entry<E, Object> entry : data.entrySet()) {
                Object o = entry.getValue();
                if (o == null)
                    continue;
                if (names.length() > 0 && values.length() > 0) {
                    names.append(", ");
                    values.append(", ");
                }
                names.append(entry.getKey().name());
                values.append("?");
            }

            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                    getName(), names, values);
            PreparedStatement statement = database.prepareStatement(sql);
            if (!autoKey)
                statement.setObject(1, key.getValue());
            int i = 1;
            for (HashMap.Entry<E, Object> entry : data.entrySet()) {
                statement.setObject(autoKey ? i : i + 1, entry.getValue());
                i++;
            }
            statement.execute();

            if (autoKey)
                return getLastKey();
            return key.getValue();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Put object. Edit object data in the database.
     */
    @Override
    public void put(JSONObject json) {
        checkMissing();
        try (Database database = WebApplication.getDatabase()) {
            database.connect();
            Connection connection = database.getConnection();
            connection.setAutoCommit(false);
            for (E e : e.getEnumConstants()) {
                String name = e.name();
                if (!json.has(name))
                    continue;
                if (e.equals(key.getEnum()) && autoKey)
                    continue;

                String sql = String.format("UPDATE %s SET %s = ? WHERE %s = ?",
                        getName(), name, key.getEnum().name());
                PreparedStatement statement = database.prepareStatement(sql);
                statement.setObject(1, json.get(name));
                statement.setObject(2, key.getValue());
                statement.execute();
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete object. Delete object from the database.
     */
    @Override
    public void delete() {
        checkMissing();
        try (Database database = WebApplication.getDatabase()) {
            database.connect();
            String sql = String.format("DELETE FROM %s WHERE %s = ?",
                    getName(), key.getEnum().name());
            PreparedStatement statement = database.prepareStatement(sql);
            statement.setObject(1, key.getValue());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get object's key.
     *
     * @return The object's key.
     */
    @Override
    public ObjectKey<E> getKey() {
        return key;
    }

    /**
     * Get object's data.
     *
     * @return The object's data.
     */
    @Override
    public HashMap<E, Object> getData() {
        return data;
    }

    /**
     * Get object's data as JSON.
     *
     * @return JSON of data.
     */
    @Override
    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        for (HashMap.Entry<E, Object> entry : data.entrySet())
            json.put(entry.getKey().name(), entry.getValue());
        for (HashMap.Entry<E, BaseChild> child : children.entrySet()) {
            Base object = child.getValue().getChildren();
            if (object != null)
                json.put(child.getKey().name(), object.getJSON());
        }
        return json;
    }

    /**
     * Parse object's data from the JSON.
     *
     * @param json JSON of object's data.
     * @return The parsed instance.
     */
    @Override
    public BaseObject<E> parse(JSONObject json) {
        for (E e : e.getEnumConstants()) {
            String name = e.name();
            if (!json.has(name))
                continue;
            Object value = json.get(name);
            if (e.equals(key.getEnum())) {
                if (!autoKey)
                    key.setValue(value);
            } else
                data.put(e, value);
        }
        return this;
    }

    /**
     * Parse object's data from the ResultSet.
     *
     * @param resultSet ResultSet with object's data.
     * @return The parsed instance.
     */
    public BaseObject<E> parse(ResultSet resultSet) {
        for (E en : e.getEnumConstants()) {
            String name = en.name();
            try {
                Object value = resultSet.getObject(name);
                if (en.equals(key.getEnum())) {
                    if (!autoKey)
                        key.setValue(value);
                } else
                    data.put(en, value);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * Check contains of object's key in the database.
     *
     * @return The object's key already in use.
     */
    private boolean contains() {
        try (Database database = WebApplication.getDatabase()) {
            database.connect();
            String keyName = key.getEnum().name();

            String sql = String.format("SELECT %s FROM %s WHERE %s = ?",
                    keyName, getName(), keyName);
            PreparedStatement statement = database.prepareStatement(sql);
            statement.setObject(1, key.getValue());

            return statement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * The method gives an error message if object's key is does't contains
     * in the database.
     */
    private void checkMissing() {
        if (!contains()) {
            String message = String.format("Database don't contains an" +
                    " object where %s = %s", key.getEnum().name(),
                    key.getValue());
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Get simple class name for other requests.
     *
     * @return Object's name.
     */
    private String getName() {
        String simpleName = getClass().getSimpleName();
        return String.valueOf(simpleName.charAt(0)).toLowerCase() +
                simpleName.substring(1);
    }

    /**
     * Put child object.
     *
     * @param e The <E> child's key.
     * @param c The class of child.
     * @param <B> The object extends by BaseObject class.
     */
    protected <B extends BaseObject> void putChild(E e, Class<B> c) {
        children.put(e, new BaseChild<>(c));
    }

    /**
     * Get last key in database.
     *
     * @return The last key of objects in database.
     */
    private Object getLastKey() {
        try (Database database = WebApplication.getDatabase()) {
            database.connect();
            String sql = String.format("SELECT %s FROM %s ORDER BY %s DESC LIMIT 1",
                    key.getEnum().name(), getName(), key.getEnum().name());
            ResultSet resultSet = database.executeQuery(sql);
            if (!resultSet.next())
                throw new NullPointerException();
            return resultSet.getObject(key.getEnum().name());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Set object's key as auto increment.
     */
    protected void setAutoKey() {
        this.autoKey = true;
    }

    /**
     * Set object's key.
     *
     * @param e The <E> field of object's key.
     * @param key The object's key value.
     */
    private void setKey(E e, Object key) {
        this.key = new ObjectKey<>(e, key);
    }
}
