package com.transport.controllers;

import com.transport.models.User;
import com.transport.utils.DialogUtils;

public class AuthController {
    private DatabaseController dbController;

    public AuthController() {
        dbController = DatabaseController.getInstance();
    }

    public User login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            DialogUtils.showError("Ошибка входа", "Заполните все поля");
            return null;
        }

        User user = dbController.authenticate(username, password);
        if (user == null) {
            DialogUtils.showError("Ошибка входа", "Неверные учетные данные");
        }
        return user;
    }
}