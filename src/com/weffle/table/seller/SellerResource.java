package com.weffle.table.seller;

import com.weffle.table.customer.Customer;
import com.weffle.table.customer.CustomerData;
import com.weffle.table.employee.Employee;
import com.weffle.table.employee.EmployeeData;
import com.weffle.table.payment.Payment;
import com.weffle.table.point.Point;
import com.weffle.table.refund.Refund;
import com.weffle.table.refund.RefundData;
import com.weffle.table.sale.Sale;
import com.weffle.table.sale.SaleData;
import com.weffle.table.storage.Storage;
import com.weffle.table.unit.Unit;
import com.weffle.table.unit.UnitData;
import org.json.JSONObject;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;

@Path("seller")
public class SellerResource {

    /**
     * Sell unit.
     *
     * @param unitBarcode Unit barcode.
     * @param sellerId Seller id.
     * @param customerId Customer id.
     * @param paymentId Payment id.
     * @return Sale id.
     */
    @POST
    @Path("sell")
    @Produces(MediaType.TEXT_PLAIN)
    public Response sell(@QueryParam("unit") String unitBarcode,
                         @QueryParam("seller") int sellerId,
                         @QueryParam("customer") int customerId,
                         @QueryParam("payment") int paymentId) {
        Unit unit = (Unit) new Unit(unitBarcode).get();
        Seller seller = (Seller) new Seller(sellerId).get();
        Customer customer = (Customer) new Customer(customerId).get();
        Employee employee = (Employee) seller
                .get(SellerData.employee);
        Point point = (Point) employee.get(EmployeeData.point);
        new Payment(paymentId).check();
        int discount = (int) unit.get(UnitData.discount);

        Sale sale = new Sale();
        sale.put(SaleData.unit, unitBarcode);
        sale.put(SaleData.seller, sellerId);
        sale.put(SaleData.customer, customerId);
        sale.put(SaleData.payment, paymentId);
        sale.put(SaleData.discount, discount);
        sale.put(SaleData.point, point.getKey().getValue());
        sale.put(SaleData.date, new Timestamp(System.currentTimeMillis()));
        Response response = Response.ok().entity(sale.post()).build();
        Storage.decrease(point, unit);

        double price = ((double) unit.get(UnitData.price));
        price *= 1.0 - (discount / 100.0);
        employee.offset(price * (((int) seller
                .get(SellerData.percent)) / 100.0));
        customer.put(new JSONObject().put(CustomerData.total.name(),
                ((double) customer.get(CustomerData.total)) + price));
        return response;
    }

    /**
     * Refund unit.
     *
     * @param saleId Sale id.
     * @param paymentId Payment id.
     * @return Refund id.
     */
    @POST
    @Path("refund")
    @Produces(MediaType.TEXT_PLAIN)
    public Response refund(@QueryParam("sale") int saleId,
                           @QueryParam("payment") int paymentId) {
        Sale sale = (Sale) new Sale(saleId).get();
        Refund refund = new Refund();
        Customer customer = (Customer) sale.get(SaleData.customer);
        Seller seller = (Seller) sale.get(SaleData.seller);
        Employee employee = (Employee) seller
                .get(SellerData.employee);
        Unit unit = (Unit) sale.get(SaleData.unit);
        new Payment(paymentId).check();

        refund.put(RefundData.sale, saleId);
        refund.put(RefundData.payment, paymentId);
        Response response = Response.ok().entity(refund.post()).build();

        Storage.increase(((Point) employee.get(EmployeeData.point)),
                unit);
        int discount = (int) sale.get(SaleData.discount);
        double price = (double) unit.get(UnitData.price);
        price *= 1.0 - (discount / 100.0);
        employee.offset(-1 * price * (((int) seller
                .get(SellerData.percent)) / 100.0));
        customer.put(new JSONObject().put(CustomerData.total.name(),
                ((double) customer.get(CustomerData.total)) - price));
        return response;
    }
}
