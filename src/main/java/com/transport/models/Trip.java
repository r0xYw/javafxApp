package com.transport.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Trip {
    private int id;
    private int routeId;
    private int vehicleId;
    private int driverId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String cargoDescription;
    private BigDecimal finalCost;
    private BigDecimal cargoWeight;

    // Дополнительные поля для отображения (JOIN данные)
    private String routeDescription;
    private String driverName;
    private String startPoint;
    private String endPoint;
    private BigDecimal routeDistance;

    // Конструкторы
    public Trip() {}

    public Trip(int routeId, int vehicleId, int driverId, LocalDateTime startTime,
                String status, String cargoDescription, BigDecimal finalCost, BigDecimal cargoWeight) {
        this.routeId = routeId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.startTime = startTime;
        this.status = status;
        this.cargoDescription = cargoDescription;
        this.finalCost = finalCost;
        this.cargoWeight = cargoWeight;
    }

    // Полный конструктор
    public Trip(int id, int routeId, int vehicleId, int driverId, LocalDateTime startTime,
                LocalDateTime endTime, String status, String cargoDescription,
                BigDecimal finalCost, BigDecimal cargoWeight) {
        this.id = id;
        this.routeId = routeId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.cargoDescription = cargoDescription;
        this.finalCost = finalCost;
        this.cargoWeight = cargoWeight;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCargoDescription() {
        return cargoDescription;
    }

    public void setCargoDescription(String cargoDescription) {
        this.cargoDescription = cargoDescription;
    }

    public BigDecimal getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(BigDecimal finalCost) {
        this.finalCost = finalCost;
    }

    public BigDecimal getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(BigDecimal cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    // Дополнительные поля для отображения
    public String getRouteDescription() {
        return routeDescription;
    }

    public void setRouteDescription(String routeDescription) {
        this.routeDescription = routeDescription;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public BigDecimal getRouteDistance() {
        return routeDistance;
    }

    public void setRouteDistance(BigDecimal routeDistance) {
        this.routeDistance = routeDistance;
    }

    // Вспомогательные методы
    public String getFormattedCost() {
        return finalCost != null ? String.format("%.2f ₽", finalCost) : "N/A";
    }

    public String getFormattedStartTime() {
        return startTime != null ? startTime.toLocalDate().toString() + " " +
                startTime.toLocalTime().toString().substring(0, 5) : "N/A";
    }

    public String getFormattedEndTime() {
        return endTime != null ? endTime.toLocalDate().toString() + " " +
                endTime.toLocalTime().toString().substring(0, 5) : "Не завершено";
    }

    public String getStatusRussian() {
        switch (status != null ? status : "") {
            case "planned": return "Запланировано";
            case "in_progress": return "В пути";
            case "completed": return "Завершено";
            case "cancelled": return "Отменено";
            default: return status;
        }
    }

    @Override
    public String toString() {
        return "Поездка #" + id + " (" + getRouteDescription() + ", " +
                getFormattedStartTime() + ", " + getFormattedCost() + ")";
    }
}