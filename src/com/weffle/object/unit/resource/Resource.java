package com.weffle.object.unit.resource;

import com.weffle.object.unit.Unit;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;

@Path("unit")
public class Resource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static String get() throws SQLException {
        Object[] allId = new Unit().allKeys();
        JSONArray array = new JSONArray();
        for (Object o : allId)
            array.put(new Unit((String) o).get());
        return array.toString();
    }

    @GET
    @Path("{barcode}")
    @Produces(MediaType.APPLICATION_JSON)
    public static String get(@PathParam("barcode") String barcode) throws SQLException {
        return new Unit(barcode).get().toString();
    }

    @POST
    @Path("{barcode}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public static Object post(@PathParam("barcode") String barcode, String json) throws SQLException {
        return new Unit(barcode).parse(new JSONObject(json)).post();
    }

    @PUT
    @Path("{barcode}")
    @Consumes(MediaType.APPLICATION_JSON)
    public static void put(@PathParam("barcode") String barcode, String json) throws SQLException {
        new Unit(barcode).put(new JSONObject(json));
    }

    @DELETE
    @Path("{barcode}")
    @Produces(MediaType.APPLICATION_JSON)
    public static String delete(@PathParam("barcode") String barcode) throws SQLException {
        Unit unit = new Unit(barcode);
        JSONObject json = unit.get();
        unit.delete();
        return json.toString();
    }
}
