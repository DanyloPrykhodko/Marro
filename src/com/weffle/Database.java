package com.weffle;

import java.sql.*;
import java.util.Properties;

public class Database implements Cloneable, AutoCloseable {
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

    public Database connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = DriverManager.getConnection(address, username, password);
        statement = connection.createStatement();
        return this;
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

    public boolean execute(String sql) throws SQLException {
        return statement.execute(sql);
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        return statement.executeQuery(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall(sql);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public Connection getConnection() {
        return connection;
    }

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
