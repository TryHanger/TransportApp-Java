package com.company.transportapp.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:transport.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            initDatabase(); // ✅ создаём таблицы при первом запуске
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации базы данных", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ✅ метод для автоматического создания таблиц
    private static void initDatabase() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Таблица транспорта
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS vehicles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    model TEXT NOT NULL,
                    type TEXT NOT NULL,
                    capacity REAL NOT NULL
                );
            """);

            // Таблица водителей
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS drivers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    licenseCategory TEXT NOT NULL
                );
            """);

            // Таблица маршрутов
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS routes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    startPoint TEXT NOT NULL,
                    endPoint TEXT NOT NULL,
                    distance REAL NOT NULL,
                    vehicleId INTEGER,
                    driverId INTEGER,
                    FOREIGN KEY (vehicleId) REFERENCES vehicles (id),
                    FOREIGN KEY (driverId) REFERENCES drivers (id)
                );
            """);
        }
    }
}
