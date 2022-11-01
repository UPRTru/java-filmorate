package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    @PostMapping
    public User newUser(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            log.error("Пользователь с таким id уже существует");
            throw new ValidationException("Пользователь с таким id уже существует");
        }

        if (checkUserLogin(user.getLogin())) {
            log.error("Пользователь с таким логином: " + user.getLogin() + " уже существует");
            throw new ValidationException("Пользователь с таким логином: " + user.getLogin() + " уже существует");
        }

        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }

        user.setId(generateId());

        log.info("newUser " + user);
        users.put(user.getId(), user);
        return user;
    }

    private int generateId() {
        return id++;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователя с id: " + user.getId() + " не существует.");
            throw new ValidationException("Пользователя с id: " + user.getId() + " не существует.");
        }

        if (checkUserLogin(user.getLogin())) {
            log.error("Пользователь с логином: " + user.getLogin() + " уже существует");
            throw new ValidationException("Пользователь с логином: " + user.getLogin() + " уже существует");
        }

        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }

        log.info("updateUser " + user);
        users.put(user.getId(), user);
        return user;
    }

    private boolean checkUserLogin(String login) {
        if (login.contains(" ")) {
            log.error("Логин не может содержать пробелы.");
            throw new ValidationException("Логин не может содержать пробелы.");
        }
        boolean result = false;
        for (User user: users.values()) {
            if (user.getLogin().equals(login)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}

