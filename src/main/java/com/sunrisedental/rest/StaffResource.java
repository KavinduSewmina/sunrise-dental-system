package com.sunrisedental.rest;

import com.sunrisedental.dao.StaffDAO;
import com.sunrisedental.model.Staff;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class StaffResource {

    private final StaffDAO staffDAO = new StaffDAO();

    // Simple DTO for login request body
    public static class LoginRequest {
        public String username;
        public String password;
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request) {
        Staff staff = staffDAO.getByUsername(request.username);

        if (staff == null || !staff.getPassword().equals(request.password)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Invalid username or password\"}")
                    .build();
        }

        // Don't send the password hash back to the client
        staff.setPassword(null);
        return Response.ok(staff).build();
    }
}