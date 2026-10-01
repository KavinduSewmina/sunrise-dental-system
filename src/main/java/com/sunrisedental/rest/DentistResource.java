package com.sunrisedental.rest;

import com.sunrisedental.dao.DentistDAO;
import com.sunrisedental.model.Dentist;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/dentists")
public class DentistResource {

    private final DentistDAO dentistDAO = new DentistDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Dentist addDentist(Dentist dentist) {
        dentistDAO.add(dentist);
        return dentist;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Dentist getDentist(@PathParam("id") int id) {
        return dentistDAO.getById(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Dentist> getAllDentists() {
        return dentistDAO.getAll();
    }
}