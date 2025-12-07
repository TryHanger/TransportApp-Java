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
            initDatabase(); // создаём таблицы при первом запуске
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации базы данных", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

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

            // Таблица водителей (snake_case column name)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS drivers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    license_category TEXT
                );
            """);

            // Таблица маршрутов (snake_case column names)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS routes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    start_point TEXT NOT NULL,
                    end_point TEXT NOT NULL,
                    distance REAL NOT NULL,
                    vehicle_id INTEGER,
                    driver_id INTEGER,
                    FOREIGN KEY (vehicle_id) REFERENCES vehicles (id),
                    FOREIGN KEY (driver_id) REFERENCES drivers (id)
                );
            """);

            // Таблица связей водитель -> категории (многие-к-одному)
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS driver_categories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    driver_id INTEGER NOT NULL,
                    category TEXT NOT NULL,
                    FOREIGN KEY (driver_id) REFERENCES drivers (id)
                );
            """);
        }
    }
}
