package com.weffle;

import java.sql.*;
import java.util.Properties;

/**
 * The Database class for basic service with database. It class is wrapper
 * for facilitate service with database.
 *
 * @author Danylo Prykhdko
 */
public class Database implements Cloneable, AutoCloseable {
    /**
     * Address.
     */
    private String address;

    /**
     * Username.
     */
    private String username;

    /**
     * Password.
     */
    private String password;

    /**
     * Connection.
     */
    private Connection connection;

    /**
     * Statement.
     */
    private Statement statement;

    /**
     * Constructor for implementation database.
     *
     * @param address Address.
     * @param username Username.
     * @param password Password.
     */
    private Database(String address, String username, String password) {
        this.address = address;
        this.username = username;
        this.password = password;
    }

    /**
     * Connect to database.
     *
     * @throws SQLException THe failed of connection.
     */
    public void connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = DriverManager.getConnection(address, username, password);
        statement = connection.createStatement();
    }

    /**
     * The address builder.
     *
     * @param host Address.
     * @param port Port.
     * @param scheme Schema name.
     * @param args The arguments to the connect to database.
     * @return Address for the connection.
     */
    private static String getAddress(String host, int port,
                                     String scheme, String... args) {
        StringBuilder builder = new StringBuilder(
                String.format("jdbc:mysql://%s:%d/%s", host, port, scheme));
        if (args.length > 0)
            builder.append('?');
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
            if (i < args.length - 1)
                builder.append('&');
        }
        return builder.toString();
    }


    /**
     * Execute query.
     *
     * @param sql SQL command.
     * @return ResultSet
     * @throws SQLException The failed of execution.
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        return statement.executeQuery(sql);
    }

    /**
     * Create PreparedStatement from SQL command.
     *
     * @param sql SQL command.
     * @return PreparedStatement.
     * @throws SQLException The failed of execution.
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    /**
     * Get connection.
     *
     * @return Connection.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Create database from properties.
     *
     * @param properties Loaded properties.
     * @return Database implementation.
     */
    public static Database createFromProperties(Properties properties) {
        String host = properties.getProperty("host");
        int port = Integer.parseInt(properties.getProperty("port"));
        String scheme = properties.getProperty("scheme");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String address = Database.getAddress(host, port, scheme,
                "autoReconnect=true", "useSSL=false",
                "characterEncoding=utf8", "serverTimezone=UTC");
        return new Database(address, user, password);
    }

    @Override
    public void close() throws SQLException {
        connection.close();
        statement.close();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
