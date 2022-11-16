package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    private Long generateId() {
        return id++;
    }

    @Override
    public List<User> getAll() {
        log.info("getAll Users");
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
        if (users.containsKey(user.getId())) {
            log.error("Пользователь с таким id уже существует");
            throw new ObjectAlreadyExistsException("Пользователь с таким id уже существует");
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
        checkUserId(user.getId());
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
            throw new ObjectAlreadyExistsException("Пользователь с таким логином: " + login + " уже существует");
        }
    }

    private void checkUserId(Long id) {
        if (!users.containsKey(id)) {
            log.error("Пользователя с id: " + id + " не существует.");
            throw new NotFoundException("Пользователя с id: " + id + " не существует.");
        }
    }

    @Override
    public User getUser(Long id) {
        checkUserId(id);
        log.info("getUser " + id);
        return users.getOrDefault(id, null);
    }

    @Override
    public void clearAll() {
        users.clear();
        id = 1L;
        log.info("Clear all users");
    }
}