package com.company.transportapp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final List<Map<String, Object>> vehicles = new ArrayList<>();

    public VehicleController() {
        // Тестовые данные
        vehicles.add(Map.of("id", 1, "model", "MAN TGS", "type", "Грузовик", "capacity", 12000));
        vehicles.add(Map.of("id", 2, "model", "Mercedes Sprinter", "type", "Микроавтобус", "capacity", 3500));
    }

    @GetMapping
    public List<Map<String, Object>> getAllVehicles() {
        return vehicles;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getVehicleById(@PathVariable int id) {
        return vehicles.stream()
                .filter(v -> (int) v.get("id") == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }
}
