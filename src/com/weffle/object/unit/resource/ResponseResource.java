package com.weffle.object.unit.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("unit/response")
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
    @Path("{barcode}")
    @Produces(MediaType.APPLICATION_JSON)
    public static Response getResponse(@PathParam("barcode") String barcode) {
        try {
            return Response.ok().status(200).entity(Resource.get(barcode)).header("Access-Control-Allow-Origin", "*").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().status(500).header("Access-Control-Allow-Origin", "*").build();
        }
    }

    @POST
    @Path("{barcode}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public static Response postResponse(@PathParam("barcode") String barcode, String json) {
        try {
            return Response.ok().status(200).entity(Resource.post(barcode, json)).header("Access-Control-Allow-Origin", "*").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().status(500).header("Access-Control-Allow-Origin", "*").build();
        }
    }

    @PUT
    @Path("{barcode}")
    @Consumes(MediaType.APPLICATION_JSON)
    @SuppressWarnings("Duplicates")
    public static Response putResponse(@PathParam("barcode") String barcode, String json) {
        try {
            Resource.put(barcode, json);
            return Response.ok().status(200).header("Access-Control-Allow-Origin", "*").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().status(500).header("Access-Control-Allow-Origin", "*").build();
        }
    }

    @DELETE
    @Path("{barcode}")
    @Produces(MediaType.APPLICATION_JSON)
    public static Response deleteResponse(@PathParam("barcode") String barcode) {
        try {
            return Response.ok().status(200).entity(Resource.delete(barcode)).header("Access-Control-Allow-Origin", "*").build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().status(500).header("Access-Control-Allow-Origin", "*").build();
        }
    }
}
