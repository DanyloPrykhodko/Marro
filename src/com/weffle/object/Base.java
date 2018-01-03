package com.weffle.object;

import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.Map;

/**
 * The Base is interface to simplify working with database objects.
 *
 * @param <E> The enum of all required fields in database. The enum contains
 *           names of fields.
 * @author Danylo Pryhodko
 */
public interface Base<E extends Enum> {
    /**
     * Create instance of base object by name.
     *
     * @param name The object's name.
     * @return The instance of table object.
     */
    static Base createBase(String name) {
        try {
            String className = String.valueOf(name.charAt(0)).toUpperCase() +
                    name.substring(1);
            String classPath = String.format("com.weffle.table.%s.%s",
                    name, className);
            return  (BaseObject) Class.forName(classPath).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException |
                InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all object. Load data and children of all objects from the database.
     *
     * @return The array of loaded instances.
     */
    Base[] getAll();

    /**
     * Get object. Load data and children from the database.
     *
     * @return The loaded instance.
     */
    Base get();

    /**
     * Post object. Add new object with current data to the database.
     *
     * @return The key of object in database.
     */
    Object post();

    /**
     * Put object. Edit object data in the database.
     *
     * @return The JSON of edited data.
     */
    JSONObject put(JSONObject json);

    /**
     * Delete object. Delete object from the database.
     */
    void delete();

    /**
     * Check object. Verify object on key.
     */
    void check();

    /**
     * Get object's key.
     *
     * @return The object's key.
     */
    ObjectKey getKey();

    /**
     * Put object's data.
     */
    void put(E key, Object value);

    /**
     * Get object's data by key.
     *
     * @return The object's data by key.
     */
    Object get(E key);

    /**
     * Get object's data.
     *
     * @return The object's data.
     */
    Map getData();

    /**
     * Get object's data as JSON.
     *
     * @return JSON of data.
     */
    JSONObject getJSON();

    /**
     * Parse object's data from the JSON.
     *
     * @param json JSON of object's data.
     * @return The parsed instance.
     */
    Base parse(JSONObject json);

    /**
     * Parse object's data from the ResultSet.
     *
     * @param resultSet ResultSet with object's data.
     * @return The parsed instance.
     */
    Base parse(ResultSet resultSet);
}
