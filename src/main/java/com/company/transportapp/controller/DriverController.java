package com.company.transportapp.controller;

import com.company.transportapp.model.Driver;
import com.company.transportapp.repository.DriverRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverRepository repo = new DriverRepository();

    @GetMapping
    public ResponseEntity<List<Driver>> getAll() {
        try {
            return ResponseEntity.ok(repo.findAll());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Driver> getById(@PathVariable int id) {
        try {
            Driver d = repo.findById(id);
            return d != null ? ResponseEntity.ok(d) : ResponseEntity.notFound().build();
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> payload) {
        try {
            String name = String.valueOf(payload.getOrDefault("name", "")).trim();
            Object rawCat = payload.get("licenseCategory");
            List<String> categories = null;
            if (rawCat instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) rawCat;
                categories = list.stream().map(Object::toString).collect(Collectors.toList());
            } else if (rawCat != null && !String.valueOf(rawCat).isEmpty()) {
                categories = List.of(String.valueOf(rawCat));
            }
            if (name.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Поле 'name' обязательно"));
            if (categories == null || categories.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Выберите хотя бы одну категорию"));
            Driver d = new Driver(0, name, categories);
            int id = repo.save(d);
            Driver created = repo.findById(id);
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
            Driver exists = repo.findById(id);
            if (exists == null) return ResponseEntity.notFound().build();
            String name = String.valueOf(payload.getOrDefault("name", exists.getName())).trim();
            Object rawCat = payload.get("licenseCategories");
            List<String> categories = null;
            if (rawCat instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) rawCat;
                categories = list.stream().map(Object::toString).collect(Collectors.toList());
            } else if (payload.get("licenseCategory") != null) {
                Object rc = payload.get("licenseCategory");
                if (rc instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> list = (List<Object>) rc;
                    categories = list.stream().map(Object::toString).collect(Collectors.toList());
                } else categories = List.of(String.valueOf(rc));
            } else {
                categories = exists.getLicenseCategories();
            }
            if (name.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Поле 'name' обязательно"));
            if (categories == null || categories.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","Выберите хотя бы одну категорию"));
            repo.update(id, new Driver(id, name, categories));
            Driver updated = repo.findById(id);
            return ResponseEntity.ok(updated);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
