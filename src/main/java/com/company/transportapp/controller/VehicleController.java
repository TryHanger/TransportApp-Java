package com.company.transportapp.controller;

import com.company.transportapp.model.Vehicle;
import com.company.transportapp.repository.VehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleRepository repo = new VehicleRepository();

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        try {
            return ResponseEntity.ok(repo.findAll());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable int id) {
        try {
            Vehicle v = repo.findById(id);
            return v != null ? ResponseEntity.ok(v) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createVehicle(@RequestBody Map<String, Object> payload) {
        try {
            String model = String.valueOf(payload.getOrDefault("model", "")).trim();
            String type = String.valueOf(payload.getOrDefault("type", "")).trim();
            double capacity = Double.parseDouble(String.valueOf(payload.getOrDefault("capacity", 0)));
            if (model.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Поле 'model' обязательно"));
            if (type.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Поле 'type' обязательно"));
            if (!(capacity>0)) return ResponseEntity.badRequest().body(Map.of("error","Поле 'capacity' обязательно и должно быть > 0"));
            Vehicle v = new Vehicle(0, model, type, capacity);
            repo.save(v);
            Vehicle created = repo.findAll().stream().max((a,b)->Integer.compare(a.getId(), b.getId())).orElse(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable int id) {
        try {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable int id, @RequestBody Map<String, Object> payload) {
        try {
            Vehicle existing = repo.findById(id);
            if (existing == null) return ResponseEntity.notFound().build();
            String model = String.valueOf(payload.getOrDefault("model", existing.getModel()));
            String type = String.valueOf(payload.getOrDefault("type", existing.getType()));
            double capacity = Double.parseDouble(String.valueOf(payload.getOrDefault("capacity", existing.getCapacity())));
            // Обновляем через репозиторий
            repo.update(id, new Vehicle(id, model, type, capacity));
            Vehicle updated = repo.findById(id);
            return ResponseEntity.ok(updated);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
