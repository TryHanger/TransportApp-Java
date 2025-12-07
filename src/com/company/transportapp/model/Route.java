package com.company.transportapp.model;

public class Route {
    private int id;
    private String startPoint;
    private String endPoint;
    private double distance;
    private Vehicle vehicle;
    private Driver driver;
    private Integer vehicleId;
    private Integer driverId;

    public Route(int id, String startPoint, String endPoint, double distance) {
        this.id = id;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.distance = distance;
    }

    public Route(int id, String startPoint, String endPoint, double distance, int vehicleId, int driverId) {
        this.id = id;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.distance = distance;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
    }

    public int getId() { return id; }
    public String getStartPoint() { return startPoint; }
    public String getEndPoint() { return endPoint; }
    public double getDistance() { return distance; }

    public Vehicle getVehicle() { return vehicle; }
    public Driver getDriver() { return driver; }
    public Integer getVehicleId() { return vehicleId; }
    public Integer getDriverId() { return driverId; }

    public void assign(Vehicle vehicle, Driver driver) {
        this.vehicle = vehicle;
        this.driver = driver;
    }

    public double estimateTime() {
        if (vehicle == null) return distance / 60;
        double avgSpeed = switch (vehicle.getType().toLowerCase()) {
            case "грузовик" -> 70;
            case "автобус" -> 60;
            default -> 80;
        };
        return distance / avgSpeed;
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %d | %s → %s | %.1f км | Транспорт #%s | Водитель #%s | Время: %.1f ч",
                id,
                startPoint,
                endPoint,
                distance,
                (vehicle != null ? vehicle.getModel() : (vehicleId != null ? vehicleId : "—")),
                (driver != null ? driver.getName() : (driverId != null ? driverId : "—")),
                estimateTime()
        );
    }
}
