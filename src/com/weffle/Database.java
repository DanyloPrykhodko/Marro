package com.weffle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Database implements Cloneable {
    private String address;
    private String username;
    private String password;
    private Connection connection;
    private Statement statement;

    public Database(String address, String username, String password) {
        this.address = address;
        this.username = username;
        this.password = password;
    }

    public void connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = DriverManager.getConnection(address, username, password);
        statement = connection.createStatement();
    }

    public void close() throws SQLException {
        connection.close();
        statement.close();
    }

    public static String getAddress(String host, int port, String scheme, String... args) {
        StringBuilder builder = new StringBuilder(String.format("jdbc:mysql://%s:%d/%s", host, port, scheme));
        if (args.length > 0)
            builder.append('?');
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
            if (i < args.length - 1)
                builder.append('&');
        }
        return builder.toString();
    }

    public Statement getStatement() {
        return statement;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
