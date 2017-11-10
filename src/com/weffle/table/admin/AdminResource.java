package com.weffle.table.admin;

import com.weffle.object.Base;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("admin")
public class AdminResource {
    @GET
    @Path("token")
    @Produces(MediaType.TEXT_PLAIN)
    public String getToken(@QueryParam("hash") String hash) {
        return Admin.getToken(hash);
    }

    @GET
    @Path("checkToken")
    @Produces(MediaType.TEXT_PLAIN)
    public String checkToken(@QueryParam("token") String token) {
        return Admin.checkToken(token);
    }

    @GET
    @Path("{table}/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public String get(@PathParam("table") String table,
                      @PathParam("key") String key,
                      @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Global);
        Base base = Base.createBase(table);
        if (base == null)
            return null;
        base.getKey().setValue(key);
        return base.get().getJSON().toString();
    }

    @POST
    @Path("{table}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String post(@PathParam("table") String table,
                       @QueryParam("token") String token,
                       String json) {
        Admin.checkAccess(token, AdminRank.Global);
        Base base = Base.createBase(table);
        if (base == null)
            return null;
        return (String) base.parse(new JSONObject(json)).post();
    }

    @POST
    @Path("{table}/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String post(@PathParam("table") String table,
                       @PathParam("key") String key,
                       @QueryParam("token") String token,
                       String json) {
        Admin.checkAccess(token, AdminRank.Global);
        Base base = Base.createBase(table);
        if (base == null)
            return null;
        base.getKey().setValue(key);
        return (String) base.parse(new JSONObject(json)).post();
    }

    @PUT
    @Path("{table}/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public void putToken(@PathParam("table") String table,
                         @PathParam("key") int key,
                         @QueryParam("token") String token, String json) {
        Admin.checkAccess(token, AdminRank.Global);
        Base base = Base.createBase(table);
        if (base == null)
            return;
        base.getKey().setValue(key);
        base.put(new JSONObject(json));
    }

    @DELETE
    @Path("{table}/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteToken(@PathParam("table") String table,
                              @PathParam("key") int key,
                              @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Global);
        Base base = Base.createBase(table);
        if (base == null)
            return null;
        base.getKey().setValue(key);
        String json = base.get().getJSON().toString();
        base.delete();
        return json;
    }
}
