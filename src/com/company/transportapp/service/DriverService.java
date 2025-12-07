package com.company.transportapp.service;

import com.company.transportapp.model.Driver;
import com.company.transportapp.repository.DriverRepository;

import java.sql.SQLException;
import java.util.List;

public class DriverService {

    private final DriverRepository repository;

    public DriverService() {
        this.repository = new DriverRepository();
    }

    public void addDriver(String name, String licenseCategory) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя водителя не может быть пустым");
        }
        if (licenseCategory == null || licenseCategory.isBlank()) {
            throw new IllegalArgumentException("Категория прав обязательна");
        }

        try {
            repository.save(new Driver(0, name, licenseCategory));
            System.out.println("✅ Водитель успешно добавлен.");
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при добавлении водителя: " + e.getMessage());
        }
    }

    public List<Driver> getAllDrivers() {
        try {
            return repository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка водителей", e);
        }
    }

    public void deleteDriver(int id) {
        try {
            repository.deleteById(id);
            System.out.println("🧍 Водитель с ID " + id + " удалён.");
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при удалении водителя: " + e.getMessage());
        }
    }

    public Driver getDriverById(int id) {
        try {
            return repository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении водителя", e);
        }
    }
}
