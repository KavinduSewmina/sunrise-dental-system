package com.sunrisedental.rest;

import com.sunrisedental.dao.BillDAO;
import com.sunrisedental.model.Bill;
import com.sunrisedental.service.BillingService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/bills")
public class BillResource {

    private final BillingService billingService = new BillingService();
    private final BillDAO billDAO = new BillDAO();

    // Task brief: "Calculate and Print Bill"
    @POST
    @Path("/generate/{appointmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Bill generateBill(@PathParam("appointmentId") int appointmentId) {
        return billingService.generateBill(appointmentId);
    }

    @GET
    @Path("/appointment/{appointmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Bill getBillByAppointment(@PathParam("appointmentId") int appointmentId) {
        Bill bill = billDAO.getByAppointmentId(appointmentId);
        if (bill == null) {
            throw new WebApplicationException("Bill not found", 404);
        }
        return bill;
    }
}