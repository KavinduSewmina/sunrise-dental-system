package com.sunrisedental.rest;

import com.sunrisedental.dao.AppointmentDAO;
import com.sunrisedental.model.Appointment;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

@Path("/appointments")
public class AppointmentResource {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    // Task brief: "Register New Appointment"
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Appointment registerAppointment(Appointment appt) {
        // Auto-generate a human-readable appointment number
        appt.setAppointmentNumber("APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        appointmentDAO.add(appt);
        return appt;
    }

    // Task brief: "Display Appointment Details - Search using the appointment number"
    @GET
    @Path("/number/{appointmentNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Appointment getByAppointmentNumber(@PathParam("appointmentNumber") String appointmentNumber) {
        Appointment appt = appointmentDAO.getByAppointmentNumber(appointmentNumber);
        if (appt == null) {
            throw new WebApplicationException("Appointment not found", 404);
        }
        return appt;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Appointment getAppointment(@PathParam("id") int id) {
        return appointmentDAO.getById(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAll();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Appointment updateAppointment(@PathParam("id") int id, Appointment appt) {
        appt.setAppointmentId(id);
        appointmentDAO.update(appt);
        return appt;
    }

    @DELETE
    @Path("/{id}")
    public void cancelAppointment(@PathParam("id") int id) {
        appointmentDAO.delete(id);
    }
}