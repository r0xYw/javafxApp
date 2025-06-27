package com.transport;

import com.transport.controllers.AuthController;
import com.transport.controllers.DriverController;
import com.transport.models.User;
import com.transport.views.LoginView;
import com.transport.views.MainView;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Stage primaryStage;
    private AuthController authController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.authController = new AuthController();
        showLoginView();
    }

    private void showLoginView() {
        LoginView loginView = new LoginView(primaryStage);
        loginView.setLoginHandler(this::handleLogin);
        loginView.show();
    }

    private void handleLogin(String username, String password) {
        User user = authController.login(username, password);
        if (user != null) {
            showMainView(user);
        }
    }

    private void showMainView(User user) {
        MainView mainView = new MainView(primaryStage, user.getRole());
        new DriverController(mainView);
        mainView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}