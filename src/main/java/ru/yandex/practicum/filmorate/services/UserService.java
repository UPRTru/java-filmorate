package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserService extends Service<User> {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    private int generateId() {
        return id++;
    }

    @Override
    public User add(User user) {
        if (users.containsKey(user.getId())) {
            log.error("Пользователь с таким id уже существует");
            throw new ValidationException("Пользователь с таким id уже существует");
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        checkUserLogin(user.getLogin());
        user.setId(generateId());
        log.info("newUser " + user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователя с id: " + user.getId() + " не существует.");
            throw new ValidationException("Пользователя с id: " + user.getId() + " не существует.");
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        checkUserLogin(user.getLogin());
        log.info("updateUser " + user);
        users.put(user.getId(), user);
        return user;
    }

    private void checkUserLogin(String login) {
        if (login.contains(" ")) {
            log.error("Логин не может содержать пробелы.");
            throw new ValidationException("Логин не может содержать пробелы.");
        }
        boolean checkFoundLogin = false;
        for (User user : users.values()) {
            if (user.getLogin().equals(login)) {
                checkFoundLogin = true;
                break;
            }
        }
        if (checkFoundLogin) {
            log.error("Пользователь с таким логином: " + login + " уже существует");
            throw new ValidationException("Пользователь с таким логином: " + login + " уже существует");
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void clearAll() {
        users.clear();
        id = 1;
    }
}
