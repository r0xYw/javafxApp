package com.transport.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.BiConsumer;

public class LoginView {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private Stage stage;
    private BiConsumer<String, String> loginHandler;

    public LoginView(Stage primaryStage) {
        this.stage = primaryStage;
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            stage.setScene(new Scene(root, 300, 250));
            stage.setTitle("Авторизация");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        if (loginHandler != null) {
            loginHandler.accept(usernameField.getText(), passwordField.getText());
        }
    }

    public void showError(String message) {
        errorLabel.setText(message);
    }

    public void setLoginHandler(BiConsumer<String, String> loginHandler) {
        this.loginHandler = loginHandler;
    }

    public void show() {
        stage.show();
    }
}