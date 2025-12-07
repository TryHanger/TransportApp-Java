package com.company.transportapp.repository;

import com.company.transportapp.model.Driver;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverRepository {

    public DriverRepository() {
        // Таблица создается автоматически в DatabaseConnection
    }

    // Сохраняет водителя и возвращает сгенерированный id
    public int save(Driver driver) throws SQLException {
        String sql = "INSERT INTO drivers (name, license_category) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, driver.getName());
            // В license_category сохраняем совместимый CSV (опционально)
            String csv = null;
            if (driver.getLicenseCategories() != null && !driver.getLicenseCategories().isEmpty()) {
                csv = String.join(",", driver.getLicenseCategories());
            }
            stmt.setString(2, csv);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            int id = -1;
            if (keys.next()) id = keys.getInt(1);
            // Сохраняем категории в отдельной таблице
            if (id != -1 && driver.getLicenseCategories() != null) {
                String ins = "INSERT INTO driver_categories (driver_id, category) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(ins)) {
                    for (String c : driver.getLicenseCategories()) {
                        ps.setInt(1, id);
                        ps.setString(2, c);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            return id;
        }
    }

    public List<Driver> findAll() throws SQLException {
        String sql = "SELECT * FROM drivers";
        Map<Integer, Driver> map = new HashMap<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                map.put(id, new Driver(id, name, new ArrayList<>()));
            }
            // Загрузим категории для всех водителей одной командой
            if (!map.isEmpty()) {
                String q = "SELECT driver_id, category FROM driver_categories WHERE driver_id IN (" + String.join(",", map.keySet().stream().map(String::valueOf).toArray(String[]::new)) + ")";
                try (ResultSet rs2 = stmt.executeQuery(q)) {
                    while (rs2.next()) {
                        int driverId = rs2.getInt("driver_id");
                        String cat = rs2.getString("category");
                        Driver d = map.get(driverId);
                        if (d != null) d.getLicenseCategories().add(cat);
                    }
                }
            }
        }
        return new ArrayList<>(map.values());
    }

    public void deleteById(int id) throws SQLException {
        String delCats = "DELETE FROM driver_categories WHERE driver_id = ?";
        String delDriver = "DELETE FROM drivers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps1 = conn.prepareStatement(delCats);
             PreparedStatement ps2 = conn.prepareStatement(delDriver)) {
            ps1.setInt(1, id);
            ps1.executeUpdate();
            ps2.setInt(1, id);
            ps2.executeUpdate();
        }
    }

    public void update(int id, Driver driver) throws SQLException {
        String sql = "UPDATE drivers SET name = ?, license_category = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, driver.getName());
            String csv = null;
            if (driver.getLicenseCategories() != null && !driver.getLicenseCategories().isEmpty()) csv = String.join(",", driver.getLicenseCategories());
            stmt.setString(2, csv);
            stmt.setInt(3, id);
            stmt.executeUpdate();
            // обновим категории: удалим старые и вставим новые
            String del = "DELETE FROM driver_categories WHERE driver_id = ?";
            try (PreparedStatement dps = conn.prepareStatement(del)) {
                dps.setInt(1, id);
                dps.executeUpdate();
            }
            if (driver.getLicenseCategories() != null && !driver.getLicenseCategories().isEmpty()) {
                String ins = "INSERT INTO driver_categories (driver_id, category) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(ins)) {
                    for (String c : driver.getLicenseCategories()) {
                        ps.setInt(1, id);
                        ps.setString(2, c);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        }
    }

    public Driver findById(int id) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Driver d = new Driver(rs.getInt("id"), rs.getString("name"), new ArrayList<>());
                String q = "SELECT category FROM driver_categories WHERE driver_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(q)) {
                    ps.setInt(1, id);
                    ResultSet r2 = ps.executeQuery();
                    while (r2.next()) d.getLicenseCategories().add(r2.getString("category"));
                }
                return d;
            }
        }
        return null;
    }
}
