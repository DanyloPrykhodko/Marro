package com.weffle.table.admin;

import com.weffle.object.Base;
import com.weffle.table.employee.Employee;
import com.weffle.table.employee.EmployeeData;
import com.weffle.table.employee.EmployeeStatus;
import com.weffle.table.journal.Journal;
import com.weffle.table.journal.JournalData;
import com.weffle.table.point.Point;
import com.weffle.table.storage.Storage;
import com.weffle.table.transfer.Transfer;
import com.weffle.table.transfer.TransferData;
import com.weffle.table.transfer.TransferStatus;
import com.weffle.table.unit.Unit;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.List;

@Path("admin")
public class AdminResource {
    /**
     * Get admin token.
     * Only global admin access.
     *
     * @param hash Admin password hash in MD5.
     * @return Admin token and available time.
     */
    @PUT
    @Path("token")
    @Produces(MediaType.APPLICATION_JSON)
    public Response token(@QueryParam("hash") String hash) {
        return Response.ok().entity(Admin.getToken(hash).toString()).build();
    }

    /**
     * Check admin token on valid.
     * Only global admin access.
     *
     * @param token Admin token.
     * @return Status of token.
     */
    @GET
    @Path("checkToken")
    @Produces(MediaType.TEXT_PLAIN)
    public Response checkToken(@QueryParam("token") String token) {
        return Response.ok().entity(Admin.checkToken(token)).build();
    }

    /**
     * Get all table objects.
     * Only global admin access.
     *
     * @param table Table name.
     * @param token Admin token.
     * @return All table objects.
     */
    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("name") String table,
                        @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Global);
        Base base = Base.createBase(table);
        if (base == null)
            return Response.serverError().entity("Unknown table name!").build();
        JSONArray get = new JSONArray();
        for (Base b : base.getAll())
            get.put(b.getJSON());
        return Response.ok().entity(get.toString()).build();
    }

    /**
     * Get object from table by key.
     * Only global admin access.
     *
     * @param table Table name.
     * @param key Object key.
     * @param token Admin token.
     * @return Table object by key.
     */
    @GET
    @Path("{name}/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("name") String table,
                        @PathParam("key") String key,
                        @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Global);
        Base base = Base.createBase(table);
        if (base == null)
            return Response.serverError().entity("Unknown table name!").build();
        base.getKey().setValue(key);
        return Response.ok().entity(base.get().getJSON().toString()).build();
    }

    /**
     * Post object to table with auto key.
     * Only global admin access.
     *
     * @param table Table name.
     * @param token Admin token.
     * @param json Object data.
     * @return Table object key.
     */
    @POST
    @Path("{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response post(@PathParam("name") String table,
                         @QueryParam("token") String token,
                         String json) {
        Admin.checkAccess(token, AdminRank.Global);
        Base base = Base.createBase(table);
        if (base == null)
            return Response.serverError().entity("Unknown table name!").build();
        return Response.ok().entity(
                base.parse(new JSONObject(json)).post()).build();
    }

    /**
     * Post object to table with key.
     * Only global admin access.
     *
     * @param table Table name.
     * @param key Object key.
     * @param token Admin token.
     * @param json Object data.
     * @return Table object key.
     */
    @POST
    @Path("{name}/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response post(@PathParam("name") String table,
                         @PathParam("key") String key,
                         @QueryParam("token") String token,
                       String json) {
        Admin.checkAccess(token, AdminRank.Global);
        Base base = Base.createBase(table);
        if (base == null)
            return Response.serverError().entity("Unknown table name!").build();
        base.getKey().setValue(key);
        return Response.ok().entity(
                base.parse(new JSONObject(json)).post()).build();
    }

    /**
     * Put object data.
     * Only global admin access.
     *
     * @param table Table name.
     * @param key Object key.
     * @param token Admin token.
     * @param json Edited object data.
     * @return Edited data.
     */
    @PUT
    @Path("{name}/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response putToken(@PathParam("name") String table,
                             @PathParam("key") int key,
                             @QueryParam("token") String token, String json) {
        Admin.checkAccess(token, AdminRank.Global);
        Base base = Base.createBase(table);
        if (base == null)
            return Response.serverError().entity("Unknown table name!").build();
        base.getKey().setValue(key);
        return Response.ok()
                .entity(base.put(new JSONObject(json)).toString()).build();
    }

    /**
     * Delete object form table.
     * Only global admin access.
     *
     * @param table Table name.
     * @param key Object key.
     * @param token Admin token.
     * @return Data of deleted object.
     */
    @DELETE
    @Path("{name}/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteToken(@PathParam("name") String table,
                                @PathParam("key") int key,
                                @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Global);
        Base base = Base.createBase(table);
        if (base == null)
            return Response.serverError().entity("Unknown table name!").build();
        base.getKey().setValue(key);
        String json = base.get().getJSON().toString();
        base.delete();
        return Response.ok().entity(json).build();
    }

    /**
     * Set employee status.
     *
     * @param employeeId Employee id.
     * @param status New employee status.
     * @param token Admin token.
     * @return Edited employee status.
     */
    @PUT
    @Path("employee/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setStatusEmployee(@QueryParam("employee") int employeeId,
                                      @QueryParam("status") String status,
                                      @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Local);
        Base employee = new Employee(employeeId);
        employee.check();
        try {
            EmployeeStatus employeeStatus = EmployeeStatus.valueOf(status);
            JSONObject json = new JSONObject();
            json.put(EmployeeData.status.name(), employeeStatus.name());
            return Response.ok().entity(employee.put(json).toString()).build();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Response.serverError().entity("Unknown status!").build();
        }
    }

    /**
     * Add to journal employee came event.
     *
     * @param employeeId Employee id.
     * @param token Admin token.
     * @return Journal event id.
     */
    @POST
    @Path("employee/came")
    @Produces(MediaType.TEXT_PLAIN)
    public Response employeeCame(@QueryParam("employee") int employeeId,
                                 @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Local);
        new Employee(employeeId).check();
        Journal journal = new Journal();
        journal.put(JournalData.employee, employeeId);
        journal.put(JournalData.came,
                new Timestamp(System.currentTimeMillis()));
        return Response.ok().entity(journal.post()).build();
    }

    /**
     * Add to journal employee went event.
     *
     * @param employeeId Employee id.
     * @param token Admin token.
     * @return Date of went.
     */
    @PUT
    @Path("employee/went")
    @Produces(MediaType.APPLICATION_JSON)
    public Response employeeLeave(@QueryParam("employee") int employeeId,
                                  @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Local);
        new Employee(employeeId).check();
        Base[] allJournals = new Journal().getAll();
        for (int i = allJournals.length - 1; i >= 0; i--) {
            Journal journal = (Journal) allJournals[i];
            Base employee = (Base) journal.get(JournalData.employee);
            if (employee.getKey().getValue().equals(employeeId)) {
                if (journal.getData().containsKey(JournalData.went))
                    return Response.serverError().entity("Do not contains " +
                            "suitable journal event!").build();
                JSONObject json = new JSONObject().put(JournalData.went.name(),
                        new Timestamp(System.currentTimeMillis()));
                return Response.ok().entity(
                        journal.put(json).toString()).build();
            }
        }
        return Response.serverError().entity("Do not contains suitable " +
                "journal event!").build();
    }

    /**
     * Calculate employee salary.
     *
     * @param employeeId Employee id.
     * @param token Admin token.
     * @return Available employee salary and time of calculation.
     */
    @PUT
    @Path("employee/calculate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response employeeCalculate(@QueryParam("employee") int employeeId,
                                      @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Local);
        Employee employee = (Employee) new Employee(employeeId).get();
        return Response.ok()
                .entity(employee.calculateSalary().toString()).build();
    }

    /**
     * Pay salary to employee.
     *
     * @param employeeId Employee id.
     * @param amount Salary amount.
     * @param token Admin token.
     * @return Employee balance after salary.
     */
    @PUT
    @Path("employee/salary")
    @Produces(MediaType.APPLICATION_JSON)
    public Response payEmployeeSalary(@QueryParam("employee") int employeeId,
                                      @QueryParam("amount") double amount,
                                      @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Local);
        Employee employee = (Employee) new Employee(employeeId).get();
        return Response.ok().entity(
                employee.paySalary(amount).toString()).build();
    }

    /**
     * Fine employee.
     * @param employeeId Employee id.
     * @param amount Fine amount.
     * @param token Admin token.
     * @return Employee balance after fine.
     */
    @PUT
    @Path("employee/fine")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fineEmployee(@QueryParam("employee") int employeeId,
                                 @QueryParam("amount") double amount,
                                 @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Local);
        Employee employee = (Employee) new Employee(employeeId).get();
        return Response.ok().entity(
                employee.fine(amount).toString()).build();
    }

    /**
     * Request unit.
     *
     * @param unitBarcode Unit barcode.
     * @param pointsId Input and output points.
     * @param token Admin token.
     * @return Transfer id.
     */
    @POST
    @Path("point/request")
    @Produces(MediaType.TEXT_PLAIN)
    public Response storageRequest(@QueryParam("unit") String unitBarcode,
                                   @QueryParam("point") List<String> pointsId,
                                   @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Local);
        new Point(Integer.valueOf(pointsId.get(0))).check();
        new Point(Integer.valueOf(pointsId.get(1))).check();
        Employee.checkLocation(Admin.createFromToken(token), 
                new Point(Integer.parseInt(pointsId.get(1))));
        Transfer transfer = new Transfer();
        transfer.put(TransferData.departure,
                Integer.valueOf(pointsId.get(0)));
        transfer.put(TransferData.arrival,
                Integer.valueOf(pointsId.get(1)));
        transfer.put(TransferData.status,
                TransferStatus.Requested.name());
        return Response.ok().entity(transfer.post()).build();
    }

    /**
     * Send unit.
     *
     * @param transferId Transfer id.
     * @param token Admin token.
     * @return Transfer status and time of sand.
     */
    @PUT
    @Path("point/send/{transfer}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response storageSend(@PathParam("transfer") int transferId,
                                @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Local);
        Transfer transfer = (Transfer) new Transfer(transferId).get();
        Employee.checkLocation(Admin.createFromToken(token), 
                (Point) transfer.get(TransferData.departure));
        JSONObject json = new JSONObject();
        json.put(TransferData.sent.name(),
                new Timestamp(System.currentTimeMillis()));
        json.put(TransferData.status.name(), TransferStatus.Sent.name());
        Response response = Response.ok().entity(transfer.put(json)).build();
        Storage.decrease(((Point) transfer.get(TransferData.departure)),
                ((Unit) transfer.get(TransferData.unit)));
        return response;
    }

    /**
     * Receive unit.
     *
     * @param transferId Transfer id.
     * @param token Admin token.
     * @return Transfer status and time of sand.
     */
    @PUT
    @Path("point/receive/{transfer}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response storageReceive(@PathParam("transfer") int transferId,
                                   @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Local);
        Transfer transfer = (Transfer) new Transfer(transferId).get();
        Employee.checkLocation(Admin.createFromToken(token),
                (Point) transfer.get(TransferData.arrival));
        JSONObject json = new JSONObject();
        json.put(TransferData.received.name(),
                new Timestamp(System.currentTimeMillis()));
        json.put(TransferData.status.name(), TransferStatus.Received.name());
        Response response = Response.ok().entity(transfer.put(json)).build();
        Storage.increase((Point) transfer.get(TransferData.arrival),
                (Unit) transfer.get(TransferData.unit));
        return response;
    }

    /**
     * Add unit to stock storage.
     *
     * @param unitBarcode Unit barcode.
     * @param pointId Point id.
     * @param token Admin token.
     * @return Storage id.
     */
    @POST
    @Path("stock/add")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response storageRequest(@QueryParam("unit") String unitBarcode,
                                   @QueryParam("point") int pointId,
                                   @QueryParam("token") String token) {
        Admin.checkAccess(token, AdminRank.Local);
        Base point = new Point(pointId);
        Employee.checkLocation(Admin.createFromToken(token), (Point) point);
        point.check();
        Base unit = new Unit(unitBarcode);
        unit.check();
        return Response.ok().entity(
                Storage.increase((Point) point, (Unit) unit)).build();
    }
}
