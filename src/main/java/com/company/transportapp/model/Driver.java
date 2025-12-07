package com.company.transportapp.model;

import java.util.List;

public class Driver {
    private int id;
    private String name;
    // Список категорий водителя
    private List<String> licenseCategories;

    public Driver(int id, String name, List<String> licenseCategories) {
        this.id = id;
        this.name = name;
        this.licenseCategories = licenseCategories;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public List<String> getLicenseCategories() { return licenseCategories; }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | Категории: %s", id, name, licenseCategories);
    }
}
