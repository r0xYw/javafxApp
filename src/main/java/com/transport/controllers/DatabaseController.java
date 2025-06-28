package com.transport.controllers;

import com.transport.models.Route;
import com.transport.models.Trip;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.transport.models.User;
import com.transport.models.Driver; // Добавляем импорт для Driver
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseController {
    private static DatabaseController instance;
    private Connection connection;

    // SQL-запросы в виде констант для удобства
    private static final String SELECT_USER_QUERY = "SELECT id, username, role, password FROM users WHERE username = ?";
    private static final String SELECT_DRIVERS_QUERY = "SELECT * FROM drivers";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

    // Приватный конструктор для Singleton
    private DatabaseController() {
        try {
            // Явно загружаем MySQL драйвер
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL драйвер загружен успешно!");

            // Загружаем свойства из файла
            Properties props = new Properties();
            InputStream input = getClass().getResourceAsStream("/config.properties");

            if (input == null) {
                // Если файл не найден, используем значения по умолчанию для XAMPP
                props.setProperty("db.url", "jdbc:mysql://localhost:3306/transport_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                props.setProperty("db.user", "root");
                props.setProperty("db.password", "");
                System.out.println("Используются настройки по умолчанию для XAMPP");
            } else {
                props.load(input);
                input.close();
            }

            // Проверяем соединение
            System.out.println("Пытаемся подключиться к: " + props.getProperty("db.url"));

            // Устанавливаем соединение
            connection = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
            );

            System.out.println("Успешное подключение к базе данных!");

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL драйвер не найден! Проверьте зависимости в pom.xml");
            e.printStackTrace();
            throw new RuntimeException("MySQL драйвер не найден", e);
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД: " + e.getMessage());
            System.err.println("Убедитесь что:");
            System.err.println("1. XAMPP запущен");
            System.err.println("2. MySQL сервер работает на порту 3306");
            System.err.println("3. База данных 'transport_management' существует");
            e.printStackTrace();
            throw new RuntimeException("Ошибка подключения к БД", e);
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Ошибка подключения к БД", e);
        }
    }

    // Метод для получения единственного экземпляра DatabaseController
    public static synchronized DatabaseController getInstance() {
        if (instance == null) {
            instance = new DatabaseController();
        }
        return instance;
    }

    // Метод для аутентификации пользователя
    public User authenticate(String username, String password) {
        String query = "SELECT id, username, role FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Ошибка аутентификации: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Метод для получения всех водителей
    // Вместо QueryResult<Driver> используйте List<Driver>
    public List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT id, last_name, first_name, middle_name, experience FROM drivers";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Driver driver = new Driver(
                        rs.getInt("id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getInt("experience")
                );
                drivers.add(driver);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения водителей: " + e.getMessage());
            e.printStackTrace();
        }
        return drivers;
    }

    // И для поездок
    public List<Trip> getAllTrips() {
        List<Trip> trips = new ArrayList<>();
        String query = "SELECT id, route_id, driver_id, start_time, end_time, status, cargo_description, final_cost, cargo_weight FROM trips ORDER BY start_time DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Trip trip = new Trip();
                trip.setId(rs.getInt("id"));
                trip.setRouteId(rs.getInt("route_id"));
                trip.setDriverId(rs.getInt("driver_id"));

                Timestamp startTs = rs.getTimestamp("start_time");
                if (startTs != null) {
                    trip.setStartTime(startTs.toLocalDateTime());
                }

                Timestamp endTs = rs.getTimestamp("end_time");
                if (endTs != null) {
                    trip.setEndTime(endTs.toLocalDateTime());
                }

                trip.setStatus(rs.getString("status"));
                trip.setCargoDescription(rs.getString("cargo_description"));
                trip.setFinalCost(rs.getBigDecimal("final_cost"));
                trip.setCargoWeight(rs.getBigDecimal("cargo_weight"));

                trips.add(trip);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения поездок: " + e.getMessage());
            e.printStackTrace();
        }
        return trips;
    }
    // Метод для добавления нового водителя
    public boolean addDriver(String lastName, String firstName, String middleName, int experience) {
        String query = "INSERT INTO drivers (last_name, first_name, middle_name, experience, user_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, lastName);
            stmt.setString(2, firstName);
            stmt.setString(3, middleName);
            stmt.setInt(4, experience);
            stmt.setObject(5, null); // Устанавливаем user_id как NULL

            int result = stmt.executeUpdate();
            System.out.println("Водитель добавлен, затронуто строк: " + result);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка добавления водителя: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Метод для обновления данных водителя
    public boolean updateDriver(int id, String lastName, String firstName, String middleName, int experience) {
        String query = "UPDATE drivers SET last_name = ?, first_name = ?, middle_name = ?, experience = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, lastName);
            stmt.setString(2, firstName);
            stmt.setString(3, middleName);
            stmt.setInt(4, experience);
            stmt.setInt(5, id);

            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка обновления водителя: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Метод для удаления водителя
    public boolean deleteDriver(int id) {
        System.out.println("=== БД: Удаляем водителя с ID: " + id); // ДОБАВЬТЕ
        String query = "DELETE FROM drivers WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка удаления водителя: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Метод для поиска водителей по фамилии
    public List<Driver> searchDriversByLastName(String lastName) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT id, last_name, first_name, middle_name, experience FROM drivers WHERE last_name LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "%" + lastName + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Driver driver = new Driver(
                        rs.getInt("id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getInt("experience")
                );
                drivers.add(driver);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка поиска водителей: " + e.getMessage());
            e.printStackTrace();
        }
        return drivers;
    }

    // Метод для закрытия соединения с базой данных
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение с БД закрыто");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии соединения с БД: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        String query = "SELECT id, start_point, end_point, distance_km, estimated_time_hours, difficulty, base_cost FROM routes";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Route route = new Route(
                        rs.getInt("id"),
                        rs.getString("start_point"),
                        rs.getString("end_point"),
                        rs.getBigDecimal("distance_km"),
                        rs.getBigDecimal("estimated_time_hours"),
                        rs.getString("difficulty"),
                        rs.getBigDecimal("base_cost")
                );
                routes.add(route);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка получения маршрутов: " + e.getMessage());
            e.printStackTrace();
        }
        return routes;
    }

    public boolean addRoute(String startPoint, String endPoint, BigDecimal distanceKm,
                            BigDecimal estimatedTimeHours, String difficulty, BigDecimal baseCost) {
        String query = "INSERT INTO routes (start_point, end_point, distance_km, estimated_time_hours, difficulty, base_cost) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, startPoint);
            stmt.setString(2, endPoint);
            stmt.setBigDecimal(3, distanceKm);
            stmt.setBigDecimal(4, estimatedTimeHours);
            stmt.setString(5, difficulty);
            stmt.setBigDecimal(6, baseCost);

            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка добавления маршрута: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRoute(int id, String startPoint, String endPoint, BigDecimal distanceKm,
                               BigDecimal estimatedTimeHours, String difficulty, BigDecimal baseCost) {
        String query = "UPDATE routes SET start_point = ?, end_point = ?, distance_km = ?, estimated_time_hours = ?, difficulty = ?, base_cost = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, startPoint);
            stmt.setString(2, endPoint);
            stmt.setBigDecimal(3, distanceKm);
            stmt.setBigDecimal(4, estimatedTimeHours);
            stmt.setString(5, difficulty);
            stmt.setBigDecimal(6, baseCost);
            stmt.setInt(7, id);

            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка обновления маршрута: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoute(int id) {
        String query = "DELETE FROM routes WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка удаления маршрута: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

// ===== МЕТОДЫ ДЛЯ РАБОТЫ С ПОЕЗДКАМИ =====


    public boolean addTrip(int routeId, int driverId, LocalDateTime startTime,
                           String status, String cargoDescription, BigDecimal finalCost, BigDecimal cargoWeight) {

        // ОТЛАДКА - проверим все параметры
        System.out.println("=== ПАРАМЕТРЫ addTrip ===");
        System.out.println("routeId: " + routeId);
        System.out.println("driverId: " + driverId);
        System.out.println("startTime: " + startTime);
        System.out.println("status: " + status);
        System.out.println("cargoDescription: " + cargoDescription);
        System.out.println("finalCost: " + finalCost);
        System.out.println("cargoWeight: " + cargoWeight);

        String query = "INSERT INTO trips (route_id, driver_id, start_time, status, cargo_description, final_cost, cargo_weight) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, routeId);
            stmt.setInt(2, driverId);

            // ПРОВЕРКА startTime
            if (startTime != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(startTime));
            } else {
                System.err.println("ОШИБКА: startTime равен null!");
                stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now())); // Текущее время по умолчанию
            }

            // ПРОВЕРКА status
            if (status != null && !status.trim().isEmpty()) {
                stmt.setString(4, status);
            } else {
                stmt.setString(4, "planned"); // По умолчанию
            }

            // ПРОВЕРКА cargoDescription
            stmt.setString(5, cargoDescription != null ? cargoDescription : "");

            // ПРОВЕРКА finalCost
            if (finalCost != null) {
                stmt.setBigDecimal(6, finalCost);
            } else {
                stmt.setBigDecimal(6, new BigDecimal("0.00"));
            }

            // ПРОВЕРКА cargoWeight
            if (cargoWeight != null) {
                stmt.setBigDecimal(7, cargoWeight);
            } else {
                stmt.setBigDecimal(7, new BigDecimal("0.00"));
            }

            System.out.println("Выполняем SQL: " + query);
            int result = stmt.executeUpdate();
            System.out.println("Затронуто строк: " + result);
            return result > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка добавления поездки: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrip(int id, int routeId, int driverId, LocalDateTime startTime,
                              LocalDateTime endTime, String status, String cargoDescription,
                              BigDecimal finalCost, BigDecimal cargoWeight) {
        String query = "UPDATE trips SET route_id = ?, vehicle_id = ?, driver_id = ?, start_time = ?, end_time = ?, status = ?, cargo_description = ?, final_cost = ?, cargo_weight = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, routeId);
            stmt.setInt(3, driverId);
            stmt.setTimestamp(4, Timestamp.valueOf(startTime));
            stmt.setTimestamp(5, endTime != null ? Timestamp.valueOf(endTime) : null);
            stmt.setString(6, status);
            stmt.setString(7, cargoDescription);
            stmt.setBigDecimal(8, finalCost);
            stmt.setBigDecimal(9, cargoWeight);
            stmt.setInt(10, id);

            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка обновления поездки: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTrip(int id) {
        String query = "DELETE FROM trips WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Ошибка удаления поездки: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}