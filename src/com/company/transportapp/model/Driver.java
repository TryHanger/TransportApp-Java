package com.company.transportapp.model;

public class Driver {
    private int id;
    private String name;
    private String licenseCategory;

    public Driver(int id, String name, String licenseCategory) {
        this.id = id;
        this.name = name;
        this.licenseCategory = licenseCategory;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLicenseCategory() { return licenseCategory; }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | Категория: %s", id, name, licenseCategory);
    }
}
