package com.sunrisedental.dao;

import com.sunrisedental.model.Appointment;
import com.sunrisedental.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO implements GenericDAO<Appointment> {

    @Override
    public void add(Appointment appt) {
        String sql = "INSERT INTO appointments (appointment_number, patient_id, dentist_id, treatment_id, " +
                     "appointment_date, appointment_time, status, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, appt.getAppointmentNumber());
            stmt.setInt(2, appt.getPatientId());
            stmt.setInt(3, appt.getDentistId());
            stmt.setInt(4, appt.getTreatmentId());
            stmt.setDate(5, Date.valueOf(appt.getAppointmentDate()));
            stmt.setTime(6, Time.valueOf(appt.getAppointmentTime()));
            stmt.setString(7, appt.getStatus());
            stmt.setInt(8, appt.getCreatedBy());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) appt.setAppointmentId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding appointment", e);
        }
    }

    @Override
    public Appointment getById(int id) {
        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching appointment by id", e);
        }
        return null;
    }

    // This is the method Task 3 in the brief needs: "Search using the appointment number"
    public Appointment getByAppointmentNumber(String appointmentNumber) {
        String sql = "SELECT * FROM appointments WHERE appointment_number = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, appointmentNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching appointment by number", e);
        }
        return null;
    }

    @Override
    public List<Appointment> getAll() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) appointments.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all appointments", e);
        }
        return appointments;
    }

    @Override
    public void update(Appointment appt) {
        String sql = "UPDATE appointments SET patient_id = ?, dentist_id = ?, treatment_id = ?, " +
                     "appointment_date = ?, appointment_time = ?, status = ? WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appt.getPatientId());
            stmt.setInt(2, appt.getDentistId());
            stmt.setInt(3, appt.getTreatmentId());
            stmt.setDate(4, Date.valueOf(appt.getAppointmentDate()));
            stmt.setTime(5, Time.valueOf(appt.getAppointmentTime()));
            stmt.setString(6, appt.getStatus());
            stmt.setInt(7, appt.getAppointmentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating appointment", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting appointment", e);
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentId(rs.getInt("appointment_id"));
        a.setAppointmentNumber(rs.getString("appointment_number"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDentistId(rs.getInt("dentist_id"));
        a.setTreatmentId(rs.getInt("treatment_id"));
        a.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        a.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
        a.setStatus(rs.getString("status"));
        a.setCreatedBy(rs.getInt("created_by"));
        return a;
    }
}