package com.sunrisedental.rest;

import com.sunrisedental.dao.PatientDAO;
import com.sunrisedental.model.Patient;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/patients")
public class PatientResource {

    private final PatientDAO patientDAO = new PatientDAO();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Patient addPatient(Patient patient) {
        patientDAO.add(patient);
        return patient;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Patient getPatient(@PathParam("id") int id) {
        Patient p = patientDAO.getById(id);
        if (p == null) {
            throw new WebApplicationException("Patient not found", 404);
        }
        return p;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Patient> getAllPatients() {
        return patientDAO.getAll();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Patient updatePatient(@PathParam("id") int id, Patient patient) {
        patient.setPatientId(id);
        patientDAO.update(patient);
        return patient;
    }

    @DELETE
    @Path("/{id}")
    public void deletePatient(@PathParam("id") int id) {
        patientDAO.delete(id);
    }
}