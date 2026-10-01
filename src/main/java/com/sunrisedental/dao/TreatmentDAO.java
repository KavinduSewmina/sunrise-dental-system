package com.sunrisedental.dao;

import com.sunrisedental.model.Treatment;
import com.sunrisedental.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAO implements GenericDAO<Treatment> {

    @Override
    public void add(Treatment treatment) {
        String sql = "INSERT INTO treatments (treatment_name, consultation_fee, treatment_fee) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, treatment.getTreatmentName());
            stmt.setBigDecimal(2, treatment.getConsultationFee());
            stmt.setBigDecimal(3, treatment.getTreatmentFee());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) treatment.setTreatmentId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding treatment", e);
        }
    }

    @Override
    public Treatment getById(int id) {
        String sql = "SELECT * FROM treatments WHERE treatment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching treatment by id", e);
        }
        return null;
    }

    @Override
    public List<Treatment> getAll() {
        List<Treatment> treatments = new ArrayList<>();
        String sql = "SELECT * FROM treatments";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) treatments.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all treatments", e);
        }
        return treatments;
    }

    @Override
    public void update(Treatment treatment) {
        String sql = "UPDATE treatments SET treatment_name = ?, consultation_fee = ?, treatment_fee = ? WHERE treatment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, treatment.getTreatmentName());
            stmt.setBigDecimal(2, treatment.getConsultationFee());
            stmt.setBigDecimal(3, treatment.getTreatmentFee());
            stmt.setInt(4, treatment.getTreatmentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating treatment", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM treatments WHERE treatment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting treatment", e);
        }
    }

    private Treatment mapRow(ResultSet rs) throws SQLException {
        Treatment t = new Treatment();
        t.setTreatmentId(rs.getInt("treatment_id"));
        t.setTreatmentName(rs.getString("treatment_name"));
        t.setConsultationFee(rs.getBigDecimal("consultation_fee"));
        t.setTreatmentFee(rs.getBigDecimal("treatment_fee"));
        return t;
    }
}