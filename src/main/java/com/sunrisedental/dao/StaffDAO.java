package com.sunrisedental.dao;

import com.sunrisedental.model.Staff;
import com.sunrisedental.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO implements GenericDAO<Staff> {

    @Override
    public void add(Staff staff) {
        String sql = "INSERT INTO staff (username, password, full_name, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, staff.getUsername());
            stmt.setString(2, staff.getPassword());
            stmt.setString(3, staff.getFullName());
            stmt.setString(4, staff.getRole());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) staff.setStaffId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding staff", e);
        }
    }

    @Override
    public Staff getById(int id) {
        String sql = "SELECT * FROM staff WHERE staff_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching staff by id", e);
        }
        return null;
    }

    // Used by the login/authentication feature (Task 1 in the brief)
    public Staff getByUsername(String username) {
        String sql = "SELECT * FROM staff WHERE username = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching staff by username", e);
        }
        return null;
    }

    @Override
    public List<Staff> getAll() {
        List<Staff> staffList = new ArrayList<>();
        String sql = "SELECT * FROM staff";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) staffList.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all staff", e);
        }
        return staffList;
    }

    @Override
    public void update(Staff staff) {
        String sql = "UPDATE staff SET username = ?, password = ?, full_name = ?, role = ? WHERE staff_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staff.getUsername());
            stmt.setString(2, staff.getPassword());
            stmt.setString(3, staff.getFullName());
            stmt.setString(4, staff.getRole());
            stmt.setInt(5, staff.getStaffId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating staff", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM staff WHERE staff_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting staff", e);
        }
    }

    private Staff mapRow(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setStaffId(rs.getInt("staff_id"));
        s.setUsername(rs.getString("username"));
        s.setPassword(rs.getString("password"));
        s.setFullName(rs.getString("full_name"));
        s.setRole(rs.getString("role"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}