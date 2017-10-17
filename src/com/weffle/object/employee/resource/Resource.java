package com.weffle.object.employee.resource;

import com.weffle.object.employee.Employee;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;

@Path("employee")
public class Resource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static String get() throws SQLException {
        Object[] allId = new Employee().allKeys();
        JSONArray array = new JSONArray();
        for (Object o : allId)
            array.put(new Employee((int) o).get());
        return array.toString();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public static String get(@PathParam("id") int id) throws SQLException {
        return new Employee(id).get().toString();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public static Object post(String json) throws SQLException {
        return new Employee().parse(new JSONObject(json)).post();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public static void put(@PathParam("id") int id, String json) throws SQLException {
        new Employee(id).put(new JSONObject(json));
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public static String delete(@PathParam("id") int id) throws SQLException {
        Employee employee = new Employee(id);
        JSONObject json = employee.get();
        employee.delete();
        return json.toString();
    }
}
