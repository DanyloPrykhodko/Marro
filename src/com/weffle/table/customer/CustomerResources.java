package com.weffle.table.customer;

import com.weffle.object.Base;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("customer")
public class CustomerResources {
    /**
     * Get all customers.
     *
     * @return All customers.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Base[] customers = new Customer().getAll();
        JSONArray get = new JSONArray();
        for (Base b : customers)
            get.put(b.getJSON());
        return Response.ok().entity(get.toString()).build();
    }

    /**
     * Get customer by id.
     *
     * @param id Customer id.
     * @return Customer by id.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") int id) {
        return Response.ok().entity(new Customer(id).get().toString()).build();
    }

    /**
     * Post new customer.
     *
     * @param json Customer data.
     * @return Customer id.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response post(String json) {
        return Response.ok().entity(
                new Customer().parse(new JSONObject(json)).post()).build();
    }
}
