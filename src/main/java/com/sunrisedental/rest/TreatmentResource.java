package com.sunrisedental.rest;

import com.sunrisedental.dao.TreatmentDAO;
import com.sunrisedental.model.Treatment;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/treatments")
public class TreatmentResource {

    private final TreatmentDAO treatmentDAO = new TreatmentDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Treatment addTreatment(Treatment treatment) {
        treatmentDAO.add(treatment);
        return treatment;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Treatment getTreatment(@PathParam("id") int id) {
        return treatmentDAO.getById(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Treatment> getAllTreatments() {
        return treatmentDAO.getAll();
    }
}