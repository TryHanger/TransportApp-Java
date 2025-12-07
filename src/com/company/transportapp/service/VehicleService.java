package com.company.transportapp.service;

import com.company.transportapp.model.Vehicle;
import com.company.transportapp.repository.VehicleRepository;

import java.sql.SQLException;
import java.util.List;

public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService() {
        this.repository = new VehicleRepository();
    }

    public void addVehicle(String model, String type, double capacity) {
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Модель не может быть пустой");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Тип не может быть пустым");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Вместимость должна быть положительной");
        }

        try {
            repository.save(new Vehicle(0, model, type, capacity));
            System.out.println("✅ Транспорт успешно добавлен.");
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при добавлении транспорта: " + e.getMessage());
        }
    }

    public List<Vehicle> getAllVehicles() {
        try {
            return repository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении списка транспорта", e);
        }
    }

    public void deleteVehicle(int id) {
        try {
            repository.deleteById(id);
            System.out.println("🚗 Транспорт с ID " + id + " удалён.");
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при удалении транспорта: " + e.getMessage());
        }
    }

    public Vehicle getVehicleById(int id) {
        try {
            return repository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении транспорта", e);
        }
    }
}
