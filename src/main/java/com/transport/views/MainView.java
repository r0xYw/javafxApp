package com.transport.views;

import com.transport.controllers.DatabaseController;
import com.transport.models.Driver;
import com.transport.models.Route;
import com.transport.models.Trip;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainView implements Initializable {
    // FXML элементы - три таблицы
    @FXML private TableView<Driver> driverTable;
    @FXML private TableView<Route> routeTable;
    @FXML private TableView<Trip> tripTable;
    @FXML private Label userInfoLabel;

    // Кнопки для всех таблиц
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button viewButton;
    @FXML private Button refreshButton;

    // Кнопки для маршрутов
    @FXML private Button addRouteButton;
    @FXML private Button editRouteButton;
    @FXML private Button deleteRouteButton;
    @FXML private Button refreshRouteButton;

    // Кнопки для поездок
    @FXML private Button addTripButton;
    @FXML private Button editTripButton;
    @FXML private Button deleteTripButton;
    @FXML private Button viewTripButton;
    @FXML private Button refreshTripButton;

    // Поля класса
    private Stage stage;
    private String userRole;
    private DatabaseController dbController;
    private ObservableList<Driver> driverData;
    private ObservableList<Route> routeData;
    private ObservableList<Trip> tripData;

    public MainView(Stage primaryStage, String role) {
        this.stage = primaryStage;
        this.userRole = role;
        this.dbController = DatabaseController.getInstance();
        this.driverData = FXCollections.observableArrayList();
        this.routeData = FXCollections.observableArrayList();
        this.tripData = FXCollections.observableArrayList();
        loadFXML();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("=== ИНИЦИАЛИЗАЦИЯ MainView ===");

        // Настройка интерфейса
        setupButtonAccess();
        setupUserInfo();

        // Загрузка данных во все три таблицы
        loadDriverData();
        loadRouteData();
        loadTripData();

        System.out.println("=== ИНИЦИАЛИЗАЦИЯ ЗАВЕРШЕНА ===");
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            stage.setScene(new Scene(root, 900, 700));
            stage.setTitle("Управление перевозками - " + userRole);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить интерфейс: " + e.getMessage());
        }
    }

    private void setupButtonAccess() {
        // Ограничиваем права для водителей
        if ("driver".equals(userRole)) {
            addButton.setDisable(true);
            editButton.setDisable(true);
            deleteButton.setDisable(true);

            if (addRouteButton != null) addRouteButton.setDisable(true);
            if (editRouteButton != null) editRouteButton.setDisable(true);
            if (deleteRouteButton != null) deleteRouteButton.setDisable(true);

            if (addTripButton != null) addTripButton.setDisable(true);
            if (editTripButton != null) editTripButton.setDisable(true);
            if (deleteTripButton != null) deleteTripButton.setDisable(true);
        }
        // Для admin и dispatcher все кнопки доступны
    }

    private void setupUserInfo() {
        if (userInfoLabel != null) {
            userInfoLabel.setText("Пользователь: " + userRole);
        }
    }

    // ===== ЗАГРУЗКА ДАННЫХ В ТРИ ТАБЛИЦЫ =====

    private void loadDriverData() {
        try {
            List<Driver> drivers = dbController.getAllDrivers();
            driverData.clear();
            driverData.addAll(drivers);

            if (driverTable != null) {
                driverTable.setItems(driverData);
            }

            System.out.println("Загружено водителей: " + drivers.size());
        } catch (Exception e) {
            System.err.println("Ошибка загрузки водителей: " + e.getMessage());
            showAlert("Ошибка", "Не удалось загрузить водителей: " + e.getMessage());
        }
    }

    private void loadRouteData() {
        System.out.println("=== ЗАГРУЗКА МАРШРУТОВ ===");
        try {
            List<Route> routes = dbController.getAllRoutes();
            System.out.println("Получено маршрутов из БД: " + routes.size());

            routeData.clear();
            routeData.addAll(routes);

            if (routeTable != null) {
                routeTable.setItems(routeData);
            }

            System.out.println("Данные маршрутов загружены");

        } catch (Exception e) {
            System.err.println("Ошибка загрузки маршрутов: " + e.getMessage());
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить данные маршрутов: " + e.getMessage());
        }
    }

    private void loadTripData() {
        try {
            List<Trip> trips = dbController.getAllTrips();
            tripData.clear();
            tripData.addAll(trips);

            if (tripTable != null) {
                tripTable.setItems(tripData);
            }

            System.out.println("Загружено поездок: " + trips.size());
        } catch (Exception e) {
            System.err.println("Ошибка загрузки поездок: " + e.getMessage());
            showAlert("Ошибка", "Не удалось загрузить поездки: " + e.getMessage());
        }
    }

    // ===== ОБРАБОТЧИКИ КНОПОК ДЛЯ МАРШРУТОВ =====

    @FXML
    private void handleAddRoute() {
        System.out.println("Добавить маршрут");

        Dialog<Route> dialog = createRouteDialog("Добавить маршрут", null);
        Optional<Route> result = dialog.showAndWait();

        result.ifPresent(route -> {
            try {
                boolean success = dbController.addRoute(
                        route.getStartPoint(),
                        route.getEndPoint(),
                        route.getDistanceKm(),
                        route.getEstimatedTimeHours(),
                        route.getDifficulty(),
                        route.getBaseCost()
                );

                if (success) {
                    showAlert("Успех", "Маршрут успешно добавлен!");
                    loadRouteData();
                } else {
                    showAlert("Ошибка", "Не удалось добавить маршрут");
                }
            } catch (Exception e) {
                showAlert("Ошибка", "Ошибка при добавлении: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleEditRoute() {
        Route selectedRoute = routeTable.getSelectionModel().getSelectedItem();
        if (selectedRoute != null) {
            System.out.println("Редактировать маршрут: " + selectedRoute.getStartPoint() + " → " + selectedRoute.getEndPoint());

            Dialog<Route> dialog = createRouteDialog("Редактировать маршрут", selectedRoute);
            Optional<Route> result = dialog.showAndWait();

            result.ifPresent(route -> {
                try {
                    boolean success = dbController.updateRoute(
                            selectedRoute.getId(),
                            route.getStartPoint(),
                            route.getEndPoint(),
                            route.getDistanceKm(),
                            route.getEstimatedTimeHours(),
                            route.getDifficulty(),
                            route.getBaseCost()
                    );

                    if (success) {
                        showAlert("Успех", "Маршрут обновлен!");
                        loadRouteData();
                    } else {
                        showAlert("Ошибка", "Не удалось обновить маршрут");
                    }
                } catch (Exception e) {
                    showAlert("Ошибка", "Ошибка при обновлении: " + e.getMessage());
                }
            });
        } else {
            showAlert("Предупреждение", "Выберите маршрут для редактирования");
        }
    }

    @FXML
    private void handleDeleteRoute() {
        Route selectedRoute = routeTable.getSelectionModel().getSelectedItem();
        if (selectedRoute != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Подтверждение");
            confirmAlert.setHeaderText("Удаление маршрута");
            confirmAlert.setContentText("Удалить маршрут " + selectedRoute.getStartPoint() + " → " + selectedRoute.getEndPoint() + "?\n\nВНИМАНИЕ: Все связанные поездки также будут удалены!");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean success = dbController.deleteRoute(selectedRoute.getId());

                    if (success) {
                        showAlert("Успех", "Маршрут удален!");
                        loadRouteData();
                        loadTripData(); // Обновляем поездки
                    } else {
                        showAlert("Ошибка", "Не удалось удалить маршрут");
                    }
                } catch (Exception e) {
                    showAlert("Ошибка", "Ошибка при удалении: " + e.getMessage());
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите маршрут для удаления");
        }
    }

    // ===== ОБРАБОТЧИКИ КНОПОК ДЛЯ ПОЕЗДОК =====

    @FXML
    private void handleAddTrip() {
        System.out.println("=== КНОПКА ДОБАВИТЬ ПОЕЗДКУ НАЖАТА ===");

        Dialog<Trip> dialog = createTripDialog("Добавить поездку", null);
        Optional<Trip> result = dialog.showAndWait();

        if (result.isPresent()) {
            Trip trip = result.get();
            System.out.println("=== ДАННЫЕ ИЗ ДИАЛОГА ===");
            System.out.println("Route ID: " + trip.getRouteId());
            System.out.println("Driver ID: " + trip.getDriverId());
            System.out.println("Start Time: " + trip.getStartTime());

            try {
                boolean success = dbController.addTrip(
                        trip.getRouteId(),
                        trip.getDriverId(),
                        trip.getStartTime(),
                        trip.getStatus(),
                        trip.getCargoDescription(),
                        trip.getFinalCost(),
                        trip.getCargoWeight()
                );

                System.out.println("=== РЕЗУЛЬТАТ ДОБАВЛЕНИЯ: " + success + " ===");

                if (success) {
                    showAlert("Успех", "Поездка успешно добавлена!");
                    loadTripData();
                } else {
                    showAlert("Ошибка", "Не удалось добавить поездку");
                }
            } catch (Exception e) {
                System.err.println("=== ИСКЛЮЧЕНИЕ ===");
                e.printStackTrace();
                showAlert("Ошибка", "Ошибка: " + e.getMessage());
            }
        } else {
            System.out.println("=== ДИАЛОГ ОТМЕНЕН ===");
        }
    }

    @FXML
    private void handleEditTrip() {
        Trip selectedTrip = tripTable.getSelectionModel().getSelectedItem();
        if (selectedTrip != null) {
            System.out.println("Редактировать поездку #" + selectedTrip.getId());

            Dialog<Trip> dialog = createTripDialog("Редактировать поездку", selectedTrip);
            Optional<Trip> result = dialog.showAndWait();

            result.ifPresent(trip -> {
                try {
                    boolean success = dbController.updateTrip(
                            selectedTrip.getId(),
                            trip.getRouteId(),
                            trip.getDriverId(),
                            trip.getStartTime(),
                            trip.getEndTime(),
                            trip.getStatus(),
                            trip.getCargoDescription(),
                            trip.getFinalCost(),
                            trip.getCargoWeight()
                    );

                    if (success) {
                        showAlert("Успех", "Поездка обновлена!");
                        loadTripData();
                    } else {
                        showAlert("Ошибка", "Не удалось обновить поездку");
                    }
                } catch (Exception e) {
                    showAlert("Ошибка", "Ошибка при обновлении: " + e.getMessage());
                }
            });
        } else {
            showAlert("Предупреждение", "Выберите поездку для редактирования");
        }
    }

    @FXML
    private void handleDeleteTrip() {
        Trip selectedTrip = tripTable.getSelectionModel().getSelectedItem();
        if (selectedTrip != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Подтверждение");
            confirmAlert.setHeaderText("Удаление поездки");
            confirmAlert.setContentText("Удалить поездку #" + selectedTrip.getId() + "?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean success = dbController.deleteTrip(selectedTrip.getId());

                    if (success) {
                        showAlert("Успех", "Поездка удалена!");
                        loadTripData();
                    } else {
                        showAlert("Ошибка", "Не удалось удалить поездку");
                    }
                } catch (Exception e) {
                    showAlert("Ошибка", "Ошибка при удалении: " + e.getMessage());
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите поездку для удаления");
        }
    }

    @FXML
    private void handleViewTrip() {
        Trip selectedTrip = tripTable.getSelectionModel().getSelectedItem();
        if (selectedTrip != null) {
            String info = String.format(
                    "ID поездки: %d\nID маршрута: %d\nID водителя: %d\nДата начала: %s\nДата окончания: %s\nСтоимость: %s\nСтатус: %s\nОписание груза: %s",
                    selectedTrip.getId(),
                    selectedTrip.getRouteId(),
                    selectedTrip.getDriverId(),
                    selectedTrip.getFormattedStartTime(),
                    selectedTrip.getFormattedEndTime(),
                    selectedTrip.getFormattedCost(),
                    selectedTrip.getStatusRussian(),
                    selectedTrip.getCargoDescription() != null ? selectedTrip.getCargoDescription() : "Не указано"
            );

            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Информация о поездке");
            infoAlert.setHeaderText("Детали поездки");
            infoAlert.setContentText(info);
            infoAlert.showAndWait();
        } else {
            showAlert("Предупреждение", "Выберите поездку для просмотра");
        }
    }

    @FXML
    private void handleAddDriver() {
        System.out.println("Добавить водителя");

        Dialog<Driver> dialog = createDriverDialog("Добавить водителя", null);
        Optional<Driver> result = dialog.showAndWait();

        result.ifPresent(driver -> {
            try {
                boolean success = dbController.addDriver(
                        driver.getLastName(),
                        driver.getFirstName(),
                        driver.getMiddleName(),
                        driver.getExperience()
                );

                if (success) {
                    showAlert("Успех", "Водитель успешно добавлен!");
                    loadDriverData(); // Обновляем только таблицу водителей
                } else {
                    showAlert("Ошибка", "Не удалось добавить водителя");
                }
            } catch (Exception e) {
                showAlert("Ошибка", "Ошибка при добавлении: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleEditDriver() {
        Driver selectedDriver = driverTable.getSelectionModel().getSelectedItem();
        if (selectedDriver != null) {
            System.out.println("Редактировать водителя: " + selectedDriver.getLastName());

            Dialog<Driver> dialog = createDriverDialog("Редактировать водителя", selectedDriver);
            Optional<Driver> result = dialog.showAndWait();

            result.ifPresent(driver -> {
                try {
                    boolean success = dbController.updateDriver(
                            selectedDriver.getId(),
                            driver.getLastName(),
                            driver.getFirstName(),
                            driver.getMiddleName(),
                            driver.getExperience()
                    );

                    if (success) {
                        showAlert("Успех", "Данные водителя обновлены!");
                        loadDriverData();
                    } else {
                        showAlert("Ошибка", "Не удалось обновить данные водителя");
                    }
                } catch (Exception e) {
                    showAlert("Ошибка", "Ошибка при обновлении: " + e.getMessage());
                }
            });
        } else {
            showAlert("Предупреждение", "Выберите водителя для редактирования");
        }
    }

    @FXML
    private void handleDeleteDriver() {
        Driver selectedDriver = driverTable.getSelectionModel().getSelectedItem();
        if (selectedDriver != null) {
            System.out.println("Удалить водителя: " + selectedDriver.getLastName());

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Подтверждение");
            confirmAlert.setHeaderText("Удаление водителя");
            confirmAlert.setContentText("Вы действительно хотите удалить водителя " +
                    selectedDriver.getLastName() + " " + selectedDriver.getFirstName() + "?\n\n" +
                    "ВНИМАНИЕ: Все связанные поездки также будут удалены!");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean success = dbController.deleteDriver(selectedDriver.getId());

                    if (success) {
                        showAlert("Успех", "Водитель удален!");
                        loadDriverData();
                        loadTripData(); // Обновляем поездки (некоторые могли удалиться)
                    } else {
                        showAlert("Ошибка", "Не удалось удалить водителя");
                    }
                } catch (Exception e) {
                    showAlert("Ошибка", "Ошибка при удалении: " + e.getMessage());
                }
            }
        } else {
            showAlert("Предупреждение", "Выберите водителя для удаления");
        }
    }

    @FXML
    private void handleViewDriver() {
        Driver selectedDriver = driverTable.getSelectionModel().getSelectedItem();
        if (selectedDriver != null) {
            System.out.println("Просмотр водителя: " + selectedDriver.getLastName());

            String info = String.format(
                    "ID: %d\nФамилия: %s\nИмя: %s\nОтчество: %s\nСтаж: %d лет",
                    selectedDriver.getId(),
                    selectedDriver.getLastName(),
                    selectedDriver.getFirstName(),
                    selectedDriver.getMiddleName() != null ? selectedDriver.getMiddleName() : "Не указано",
                    selectedDriver.getExperience()
            );

            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Информация о водителе");
            infoAlert.setHeaderText("Детали водителя");
            infoAlert.setContentText(info);
            infoAlert.showAndWait();
        } else {
            showAlert("Предупреждение", "Выберите водителя для просмотра");
        }
    }

    @FXML
    private void handleRefresh() {
        System.out.println("Обновить все данные");
        loadDriverData();
        loadRouteData();
        loadTripData();
        showAlert("Информация", "Все данные обновлены");
    }

    // ===== СОЗДАНИЕ ДИАЛОГОВ ДЛЯ ВСЕХ ТАБЛИЦ =====

    private Dialog<Route> createRouteDialog(String title, Route existingRoute) {
        Dialog<Route> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Введите данные маршрута");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField startPointField = new TextField();
        TextField endPointField = new TextField();
        TextField distanceField = new TextField();
        TextField timeField = new TextField();
        ComboBox<String> difficultyCombo = new ComboBox<>();
        difficultyCombo.getItems().addAll("low", "medium", "high");
        difficultyCombo.setValue("medium");
        TextField costField = new TextField();

        // Если редактируем существующий маршрут
        if (existingRoute != null) {
            startPointField.setText(existingRoute.getStartPoint());
            endPointField.setText(existingRoute.getEndPoint());
            distanceField.setText(existingRoute.getDistanceKm().toString());
            timeField.setText(existingRoute.getEstimatedTimeHours().toString());
            difficultyCombo.setValue(existingRoute.getDifficulty());
            costField.setText(existingRoute.getBaseCost().toString());
        }

        grid.add(new Label("Пункт отправления:"), 0, 0);
        grid.add(startPointField, 1, 0);
        grid.add(new Label("Пункт назначения:"), 0, 1);
        grid.add(endPointField, 1, 1);
        grid.add(new Label("Расстояние (км):"), 0, 2);
        grid.add(distanceField, 1, 2);
        grid.add(new Label("Время в пути (ч):"), 0, 3);
        grid.add(timeField, 1, 3);
        grid.add(new Label("Сложность:"), 0, 4);
        grid.add(difficultyCombo, 1, 4);
        grid.add(new Label("Базовая стоимость (₽):"), 0, 5);
        grid.add(costField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return new Route(
                            startPointField.getText().trim(),
                            endPointField.getText().trim(),
                            new java.math.BigDecimal(distanceField.getText().trim()),
                            new java.math.BigDecimal(timeField.getText().trim()),
                            difficultyCombo.getValue(),
                            new java.math.BigDecimal(costField.getText().trim())
                    );
                } catch (Exception e) {
                    showAlert("Ошибка", "Проверьте правильность введенных данных!");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private Dialog<Trip> createTripDialog(String title, Trip existingTrip) {
        Dialog<Trip> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Введите данные поездки");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField routeIdField = new TextField();
        TextField driverIdField = new TextField();
        TextField startTimeField = new TextField();
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("planned", "in_progress", "completed", "cancelled");
        statusCombo.setValue("planned");
        TextField costField = new TextField();
        TextField weightField = new TextField();
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(2);

        // Если редактируем существующую поездку
        if (existingTrip != null) {
            routeIdField.setText(String.valueOf(existingTrip.getRouteId()));
            driverIdField.setText(String.valueOf(existingTrip.getDriverId()));
            if (existingTrip.getStartTime() != null) {
                startTimeField.setText(existingTrip.getStartTime().toString());
            }
            statusCombo.setValue(existingTrip.getStatus());
            if (existingTrip.getFinalCost() != null) {
                costField.setText(existingTrip.getFinalCost().toString());
            }
            if (existingTrip.getCargoWeight() != null) {
                weightField.setText(existingTrip.getCargoWeight().toString());
            }
            if (existingTrip.getCargoDescription() != null) {
                descriptionArea.setText(existingTrip.getCargoDescription());
            }
        } else {
            // Значения по умолчанию для новой поездки
            startTimeField.setText(LocalDateTime.now().toString());
            costField.setText("10000");
            weightField.setText("5.0");
        }

        grid.add(new Label("ID маршрута:"), 0, 0);
        grid.add(routeIdField, 1, 0);
        grid.add(new Label("ID водителя:"), 0, 1);
        grid.add(driverIdField, 1, 1);
        grid.add(new Label("Время начала (YYYY-MM-DDTHH:MM):"), 0, 2);
        grid.add(startTimeField, 1, 2);
        grid.add(new Label("Статус:"), 0, 3);
        grid.add(statusCombo, 1, 3);
        grid.add(new Label("Стоимость (₽):"), 0, 4);
        grid.add(costField, 1, 4);
        grid.add(new Label("Вес груза (т):"), 0, 5);
        grid.add(weightField, 1, 5);
        grid.add(new Label("Описание груза:"), 0, 6);
        grid.add(descriptionArea, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Trip trip = new Trip();
                    trip.setRouteId(Integer.parseInt(routeIdField.getText().trim()));
                    trip.setDriverId(Integer.parseInt(driverIdField.getText().trim()));
                    trip.setStartTime(LocalDateTime.parse(startTimeField.getText().trim()));
                    trip.setStatus(statusCombo.getValue());
                    trip.setCargoDescription(descriptionArea.getText().trim());

                    if (!costField.getText().trim().isEmpty()) {
                        trip.setFinalCost(new BigDecimal(costField.getText().trim()));
                    }

                    if (!weightField.getText().trim().isEmpty()) {
                        trip.setCargoWeight(new BigDecimal(weightField.getText().trim()));
                    }

                    return trip;
                } catch (Exception e) {
                    showAlert("Ошибка", "Проверьте правильность введенных данных!\nФормат времени: YYYY-MM-DDTHH:MM:SS");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }
    private Dialog<Driver> createDriverDialog(String title, Driver existingDriver) {
        Dialog<Driver> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Введите данные водителя");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField lastNameField = new TextField();
        TextField firstNameField = new TextField();
        TextField middleNameField = new TextField();
        TextField experienceField = new TextField();

        // Если редактируем существующего водителя
        if (existingDriver != null) {
            lastNameField.setText(existingDriver.getLastName());
            firstNameField.setText(existingDriver.getFirstName());
            middleNameField.setText(existingDriver.getMiddleName() != null ? existingDriver.getMiddleName() : "");
            experienceField.setText(String.valueOf(existingDriver.getExperience()));
        }

        grid.add(new Label("Фамилия:"), 0, 0);
        grid.add(lastNameField, 1, 0);
        grid.add(new Label("Имя:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Отчество:"), 0, 2);
        grid.add(middleNameField, 1, 2);
        grid.add(new Label("Стаж (лет):"), 0, 3);
        grid.add(experienceField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Конвертируем результат в объект Driver
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String lastName = lastNameField.getText().trim();
                    String firstName = firstNameField.getText().trim();
                    String middleName = middleNameField.getText().trim();
                    int experience = Integer.parseInt(experienceField.getText().trim());

                    if (lastName.isEmpty() || firstName.isEmpty()) {
                        showAlert("Ошибка", "Фамилия и имя обязательны для заполнения!");
                        return null;
                    }

                    if (experience < 0) {
                        showAlert("Ошибка", "Стаж не может быть отрицательным!");
                        return null;
                    }

                    return new Driver(
                            0, // ID будет назначен БД
                            lastName,
                            firstName,
                            middleName.isEmpty() ? null : middleName,
                            experience
                    );
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Стаж должен быть числом!");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    // ===== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ =====

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        stage.show();
    }
}