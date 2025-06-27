package com.transport.controllers;

import com.transport.models.Driver;
import com.transport.utils.DialogUtils;
import com.transport.views.MainView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class DriverController {
    private MainView mainView;
    private DatabaseController dbController;

    public DriverController(MainView mainView) {
        this.mainView = mainView;
        this.dbController = DatabaseController.getInstance();
        loadDrivers();
    }

    private void loadDrivers() {
        List<Driver> drivers = dbController.getAllDrivers();
        ObservableList<Driver> driverData = FXCollections.observableArrayList(drivers);
    }
}