package com.company.transportapp.repository;

import com.company.transportapp.model.Vehicle;
import com.company.transportapp.model.Driver;
import com.company.transportapp.model.Route;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RouteRepository {

    public RouteRepository() {
        // Таблица создается автоматически в DatabaseConnection
    }

    public void save(Route route) throws SQLException {
        String sql = "INSERT INTO routes (start_point, end_point, distance, vehicle_id, driver_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, route.getStartPoint());
            stmt.setString(2, route.getEndPoint());
            stmt.setDouble(3, route.getDistance());
            stmt.setObject(4, route.getVehicleId());
            stmt.setObject(5, route.getDriverId());
            stmt.executeUpdate();
        }
    }

    public List<Route> findAll() throws SQLException {
        List<Route> routes = new ArrayList<>();

        String sql = "SELECT * FROM routes";
        try (Connection conn = com.company.transportapp.repository.DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            VehicleRepository vehicleRepo = new VehicleRepository();
            DriverRepository driverRepo = new DriverRepository();

            while (rs.next()) {
                int id = rs.getInt("id");
                String start = rs.getString("start_point");
                String end = rs.getString("end_point");
                double distance = rs.getDouble("distance");
                
                Integer vehicleId = null;
                Integer driverId = null;
                
                int vehicleIdInt = rs.getInt("vehicle_id");
                int driverIdInt = rs.getInt("driver_id");
                
                if (!rs.wasNull()) {
                    vehicleId = vehicleIdInt;
                }
                if (!rs.wasNull()) {
                    driverId = driverIdInt;
                }

                Vehicle vehicle = null;
                Driver driver = null;
                
                if (vehicleId != null) {
                    vehicle = vehicleRepo.findById(vehicleId);
                }
                if (driverId != null) {
                    driver = driverRepo.findById(driverId);
                }

                Route route = new Route(id, start, end, distance, vehicleId, driverId);
                if (vehicle != null && driver != null) {
                    route.assign(vehicle, driver);
                }
                routes.add(route);
            }
        }

        return routes;
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM routes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
