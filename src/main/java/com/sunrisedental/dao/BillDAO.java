package com.sunrisedental.dao;

import com.sunrisedental.model.Bill;
import com.sunrisedental.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO implements GenericDAO<Bill> {

    @Override
    public void add(Bill bill) {
        String sql = "INSERT INTO bills (appointment_id, total_amount, payment_status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, bill.getAppointmentId());
            stmt.setBigDecimal(2, bill.getTotalAmount());
            stmt.setString(3, bill.getPaymentStatus());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) bill.setBillId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding bill", e);
        }
    }

    @Override
    public Bill getById(int id) {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bill by id", e);
        }
        return null;
    }

    public Bill getByAppointmentId(int appointmentId) {
        String sql = "SELECT * FROM bills WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching bill by appointment id", e);
        }
        return null;
    }

    @Override
    public List<Bill> getAll() {
        List<Bill> bills = new ArrayList<>();
        String sql = "SELECT * FROM bills";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) bills.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all bills", e);
        }
        return bills;
    }

    @Override
    public void update(Bill bill) {
        String sql = "UPDATE bills SET total_amount = ?, payment_status = ? WHERE bill_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, bill.getTotalAmount());
            stmt.setString(2, bill.getPaymentStatus());
            stmt.setInt(3, bill.getBillId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating bill", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM bills WHERE bill_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting bill", e);
        }
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setAppointmentId(rs.getInt("appointment_id"));
        b.setTotalAmount(rs.getBigDecimal("total_amount"));
        b.setBillDate(rs.getTimestamp("bill_date"));
        b.setPaymentStatus(rs.getString("payment_status"));
        return b;
    }
}