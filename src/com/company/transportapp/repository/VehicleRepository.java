package com.company.transportapp.repository;

import com.company.transportapp.model.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleRepository {

    public VehicleRepository() {
        // Таблица создается автоматически в DatabaseConnection
    }

    public void save(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (model, type, capacity) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getModel());
            stmt.setString(2, vehicle.getType());
            stmt.setDouble(3, vehicle.getCapacity());
            stmt.executeUpdate();
        }
    }

    public List<Vehicle> findAll() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(new Vehicle(
                        rs.getInt("id"),
                        rs.getString("model"),
                        rs.getString("type"),
                        rs.getDouble("capacity")
                ));
            }
        }
        return vehicles;
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Vehicle findById(int id) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Vehicle(
                        rs.getInt("id"),
                        rs.getString("model"),
                        rs.getString("type"),
                        rs.getDouble("capacity")
                );
            }
        }
        return null;
    }
}
