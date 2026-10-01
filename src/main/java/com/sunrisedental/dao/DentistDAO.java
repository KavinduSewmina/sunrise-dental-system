package com.sunrisedental.dao;

import com.sunrisedental.model.Dentist;
import com.sunrisedental.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DentistDAO implements GenericDAO<Dentist> {

    @Override
    public void add(Dentist dentist) {
        String sql = "INSERT INTO dentists (dentist_name, specialization, contact_number) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, dentist.getDentistName());
            stmt.setString(2, dentist.getSpecialization());
            stmt.setString(3, dentist.getContactNumber());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) dentist.setDentistId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding dentist", e);
        }
    }

    @Override
    public Dentist getById(int id) {
        String sql = "SELECT * FROM dentists WHERE dentist_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching dentist by id", e);
        }
        return null;
    }

    @Override
    public List<Dentist> getAll() {
        List<Dentist> dentists = new ArrayList<>();
        String sql = "SELECT * FROM dentists";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) dentists.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all dentists", e);
        }
        return dentists;
    }

    @Override
    public void update(Dentist dentist) {
        String sql = "UPDATE dentists SET dentist_name = ?, specialization = ?, contact_number = ? WHERE dentist_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dentist.getDentistName());
            stmt.setString(2, dentist.getSpecialization());
            stmt.setString(3, dentist.getContactNumber());
            stmt.setInt(4, dentist.getDentistId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating dentist", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM dentists WHERE dentist_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting dentist", e);
        }
    }

    private Dentist mapRow(ResultSet rs) throws SQLException {
        Dentist d = new Dentist();
        d.setDentistId(rs.getInt("dentist_id"));
        d.setDentistName(rs.getString("dentist_name"));
        d.setSpecialization(rs.getString("specialization"));
        d.setContactNumber(rs.getString("contact_number"));
        return d;
    }
}