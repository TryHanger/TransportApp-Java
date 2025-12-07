package com.company.transportapp.service;

import com.company.transportapp.model.Route;
import com.company.transportapp.repository.RouteRepository;

import java.sql.SQLException;
import java.util.List;

public class RouteService {

    private final RouteRepository repository;

    public RouteService() {
        this.repository = new RouteRepository();
    }

    public void addRoute(String startPoint, String endPoint, double distance, int vehicleId, int driverId) {
        if (startPoint == null || startPoint.isBlank() || endPoint == null || endPoint.isBlank()) {
            throw new IllegalArgumentException("Начальная и конечная точки обязательны");
        }
        if (distance <= 0) {
            throw new IllegalArgumentException("Расстояние должно быть больше нуля");
        }
        if (vehicleId <= 0) {
            throw new IllegalArgumentException("ID транспорта должен быть положительным");
        }
        if (driverId <= 0) {
            throw new IllegalArgumentException("ID водителя должен быть положительным");
        }

        try {
            repository.save(new Route(0, startPoint, endPoint, distance, vehicleId, driverId));
            System.out.println("✅ Маршрут успешно добавлен.");
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при добавлении маршрута: " + e.getMessage());
        }
    }

    public List<Route> getAllRoutes() {
        try {
            return repository.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении маршрутов", e);
        }
    }

    public void deleteRoute(int id) {
        try {
            repository.deleteById(id);
            System.out.println("🗺️ Маршрут с ID " + id + " удалён.");
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при удалении маршрута: " + e.getMessage());
        }
    }
}
