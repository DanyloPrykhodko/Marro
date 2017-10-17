package com.weffle;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

@ApplicationPath("restful")
public class WebApplication extends Application {
    private static Database database;

    public WebApplication() {
        super();
        Properties properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/properties/config.properties"));
            String host = properties.getProperty("database.host");
            int port = Integer.parseInt(properties.getProperty("database.port"));
            String scheme = properties.getProperty("database.scheme");
            String user = properties.getProperty("database.user");
            String password = properties.getProperty("database.password");
            String address = Database.getAddress(host, port, scheme, "autoReconnect=true", "useSSL=false", "characterEncoding=utf8", "serverTimezone=UTC");
            database = new Database(address, user, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Database getDatabase() throws SQLException {
        try {
            return (Database) database.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new SQLException("Could not get extension of the database.");
        }
    }
}
