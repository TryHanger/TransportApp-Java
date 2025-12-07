package com.company.transportapp.controller;

import com.company.transportapp.model.Route;
import com.company.transportapp.repository.RouteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteRepository repo = new RouteRepository();

    @GetMapping
    public ResponseEntity<List<Route>> getAll() {
        try {
            return ResponseEntity.ok(repo.findAll());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Route> getById(@PathVariable int id) {
        try {
            List<Route> all = repo.findAll();
            return all.stream().filter(r -> r.getId() == id).findFirst()
                    .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        try {
            String start = String.valueOf(payload.getOrDefault("startPoint", "")).trim();
            String end = String.valueOf(payload.getOrDefault("endPoint", "")).trim();
            double distance = Double.parseDouble(String.valueOf(payload.getOrDefault("distance", 0)));
            Integer vehicleId = payload.get("vehicleId") == null || String.valueOf(payload.get("vehicleId")).isEmpty() ? null : Integer.valueOf(String.valueOf(payload.get("vehicleId")));
            Integer driverId = payload.get("driverId") == null || String.valueOf(payload.get("driverId")).isEmpty() ? null : Integer.valueOf(String.valueOf(payload.get("driverId")));
            if (start.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Поле 'startPoint' обязательно"));
            if (end.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Поле 'endPoint' обязательно"));
            if (!(distance>0)) return ResponseEntity.badRequest().body(Map.of("error","Поле 'distance' обязательно и должно быть > 0"));
            if (vehicleId == null) return ResponseEntity.badRequest().body(Map.of("error","Выберите транспорт"));
            if (driverId == null) return ResponseEntity.badRequest().body(Map.of("error","Выберите водителя"));
            Route r = new Route(0, start, end, distance, vehicleId, driverId);
            repo.save(r);
            Route created = repo.findAll().stream().max((a,b)->Integer.compare(a.getId(), b.getId())).orElse(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        try {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Map<String, Object> payload) {
        try {
            List<Route> all = repo.findAll();
            Route existing = all.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
            if (existing == null) return ResponseEntity.notFound().build();
            String start = String.valueOf(payload.getOrDefault("startPoint", existing.getStartPoint())).trim();
            String end = String.valueOf(payload.getOrDefault("endPoint", existing.getEndPoint())).trim();
            double distance = Double.parseDouble(String.valueOf(payload.getOrDefault("distance", existing.getDistance())));
            Integer vehicleId = payload.get("vehicleId") == null || String.valueOf(payload.get("vehicleId")).isEmpty() ? existing.getVehicleId() : Integer.valueOf(String.valueOf(payload.get("vehicleId")));
            Integer driverId = payload.get("driverId") == null || String.valueOf(payload.get("driverId")).isEmpty() ? existing.getDriverId() : Integer.valueOf(String.valueOf(payload.get("driverId")));
            if (start.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Поле 'startPoint' обязательно"));
            if (end.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Поле 'endPoint' обязательно"));
            if (!(distance>0)) return ResponseEntity.badRequest().body(Map.of("error","Поле 'distance' обязательно и должно быть > 0"));
            if (vehicleId == null) return ResponseEntity.badRequest().body(Map.of("error","Выберите транспорт"));
            if (driverId == null) return ResponseEntity.badRequest().body(Map.of("error","Выберите водителя"));
            repo.update(id, new Route(id, start, end, distance, vehicleId, driverId));
            Route updated = repo.findAll().stream().filter(r -> r.getId() == id).findFirst().orElse(null);
            return ResponseEntity.ok(updated);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
