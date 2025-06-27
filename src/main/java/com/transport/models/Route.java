package com.transport.models;

import java.math.BigDecimal;

public class Route {
    private int id;
    private String startPoint;
    private String endPoint;
    private BigDecimal distanceKm;
    private BigDecimal estimatedTimeHours;
    private String difficulty;
    private BigDecimal baseCost;

    // Конструкторы
    public Route() {}

    public Route(int id, String startPoint, String endPoint, BigDecimal distanceKm,
                 BigDecimal estimatedTimeHours, String difficulty, BigDecimal baseCost) {
        this.id = id;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.distanceKm = distanceKm;
        this.estimatedTimeHours = estimatedTimeHours;
        this.difficulty = difficulty;
        this.baseCost = baseCost;
    }

    public Route(String startPoint, String endPoint, BigDecimal distanceKm,
                 BigDecimal estimatedTimeHours, String difficulty, BigDecimal baseCost) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.distanceKm = distanceKm;
        this.estimatedTimeHours = estimatedTimeHours;
        this.difficulty = difficulty;
        this.baseCost = baseCost;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public BigDecimal getEstimatedTimeHours() {
        return estimatedTimeHours;
    }

    public void setEstimatedTimeHours(BigDecimal estimatedTimeHours) {
        this.estimatedTimeHours = estimatedTimeHours;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(BigDecimal baseCost) {
        this.baseCost = baseCost;
    }

    // Вспомогательные методы
    public String getRouteDescription() {
        return startPoint + " → " + endPoint;
    }

    public String getFormattedCost() {
        return baseCost != null ? String.format("%.2f ₽", baseCost) : "N/A";
    }

    public String getDifficultyRussian() {
        switch (difficulty != null ? difficulty : "") {
            case "low": return "Легкий";
            case "medium": return "Средний";
            case "high": return "Сложный";
            default: return difficulty;
        }
    }

    @Override
    public String toString() {
        return getRouteDescription() + " (" + distanceKm + " км, " + getFormattedCost() + ")";
    }
}