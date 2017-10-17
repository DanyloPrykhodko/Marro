package com.weffle.object.employee.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("employee/response")
public class ResponseResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public static Response getResponse() {
        try {
            return Response.ok().status(200).entity(Resource.get()).header("Access-Control-Allow-Origin", "*").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().status(500).header("Access-Control-Allow-Origin", "*").build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public static Response getResponse(@PathParam("id") int id) {
        try {
            return Response.ok().status(200).entity(Resource.get(id)).header("Access-Control-Allow-Origin", "*").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().status(500).header("Access-Control-Allow-Origin", "*").build();
        }
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public static Response postResponse(String json) {
        try {
            return Response.ok().status(200).entity(Resource.post(json)).header("Access-Control-Allow-Origin", "*").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().status(500).header("Access-Control-Allow-Origin", "*").build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @SuppressWarnings("Duplicates")
    public static Response putResponse(@PathParam("id") int id, String json) {
        try {
            Resource.put(id, json);
            return Response.ok().status(200).header("Access-Control-Allow-Origin", "*").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().status(500).header("Access-Control-Allow-Origin", "*").build();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public static Response deleteResponse(@PathParam("id") int id) {
        try {
            return Response.ok().status(200).entity(Resource.delete(id)).header("Access-Control-Allow-Origin", "*").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().status(500).header("Access-Control-Allow-Origin", "*").build();
        }
    }
}
