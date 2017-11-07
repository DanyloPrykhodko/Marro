package com.weffle.table.admin;

import com.weffle.Database;
import com.weffle.SecurityAgent;
import com.weffle.WebApplication;
import com.weffle.object.BaseObject;
import com.weffle.table.employee.Employee;
import org.json.JSONObject;

import java.sql.*;

public class Admin extends BaseObject<AdminData> {
    public Admin() {
        super(AdminData.id);
        setAutoKey();
        putChild(AdminData.employee, Employee.class);
    }

    public Admin(int id) {
        super(AdminData.id, id);
        setAutoKey();
        putChild(AdminData.employee, Employee.class);
    }

    static String getToken(String hash) {
        try (Database database = WebApplication.getDatabase()) {
            database.connect();
            String sql = "SELECT id, password FROM admin";
            ResultSet resultSet = database.executeQuery(sql);

            while (resultSet.next()) {
                int id = resultSet.getInt(AdminData.id.name());
                String password = resultSet.getString(
                        AdminData.password.name());
                String hashedPassword = SecurityAgent.encodeMD5(password);

                if (hashedPassword.equals(hash.toLowerCase())) {
                    Admin admin = (Admin) new Admin(id).get();
                    if (admin.getData().get(AdminData.token) == null)
                        return admin.updateToken();
                    try {
                        admin.checkTokenDate();
                        return (String) admin.getJSON().get(
                                AdminData.token.name());
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        return admin.updateToken();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String updateToken() {
        JSONObject json = new JSONObject();
        json.put(AdminData.tokenDate.name(),
                new Timestamp(System.currentTimeMillis() + 1_800_000L));
        String token = SecurityAgent.createToken(16);
        json.put(AdminData.token.name(), token);
        put(json);
        return token;
    }

    private static Admin createFromToken(String token)
            throws RuntimeException {
        try (Database database = WebApplication.getDatabase()) {
            database.connect();
            String sql = "SELECT id FROM admin WHERE token = ?";
            PreparedStatement statement = database.prepareStatement(sql);
            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next())
                throw new RuntimeException("The token is not valid!");
            Admin admin = (Admin) new Admin(resultSet.getInt(
                    AdminData.id.name())).get();
            try {
                admin.checkTokenDate();
                return admin;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void checkAccess(String token, AdminRank rank)
            throws RuntimeException {
        Admin admin = Admin.createFromToken(token);
        if (admin == null)
            return;
        AdminRank adminRank = AdminRank.valueOf(
                (String) admin.getData().get(AdminData.rank));
        if (!(adminRank.equals(AdminRank.Global) || adminRank.equals(rank)))
            throw new RuntimeException("You don't have permissions!");
    }

    static String checkToken(String token) {
        try {
            Admin.createFromToken(token);
            return "Token is valid!";
        } catch (RuntimeException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
    
    private void checkTokenDate() throws RuntimeException {
        Timestamp timestamp = (Timestamp) getData().get(AdminData.tokenDate);
        long current = System.currentTimeMillis();
        if (timestamp.before(new Date(current)))
                throw new RuntimeException("The token is deprecated!");
    }
}
