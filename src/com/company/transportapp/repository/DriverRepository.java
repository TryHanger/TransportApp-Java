package com.company.transportapp.repository;

import com.company.transportapp.model.Driver;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverRepository {

    public DriverRepository() {
        // Таблица создается автоматически в DatabaseConnection
    }

    public void save(Driver driver) throws SQLException {
        String sql = "INSERT INTO drivers (name, license_category) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, driver.getName());
            stmt.setString(2, driver.getLicenseCategory());
            stmt.executeUpdate();
        }
    }

    public List<Driver> findAll() throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                drivers.add(new Driver(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("license_category")
                ));
            }
        }
        return drivers;
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM drivers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Driver findById(int id) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Driver(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("license_category")
                );
            }
        }
        return null;
    }
}
