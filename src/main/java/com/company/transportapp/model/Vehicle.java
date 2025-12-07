package com.company.transportapp.model;

public class Vehicle {
    private int id;
    private String model;
    private String type;
    private double capacity;

    public Vehicle(int id, String model, String type, double capacity) {
        this.id = id;
        this.model = model;
        this.type = type;
        this.capacity = capacity;
    }

    public int getId() { return id; }
    public String getModel() { return model; }
    public String getType() { return type; }
    public double getCapacity() { return capacity; }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | %s | Вместимость: %.1f", id, model, type, capacity);
    }
}
