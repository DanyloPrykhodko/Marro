package com.weffle.table.money;

import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("money")
public class MoneyResources {
    /**
     * Post new money.
     *
     * @param json Money data.
     * @return Money id.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response post(String json) {
        return Response.ok().entity(
                new Money().parse(new JSONObject(json)).post()).build();
    }
}
