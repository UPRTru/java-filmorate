package ru.yandex.practicum.filmorate.storage.user.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ValidationException;
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
        user.setId(generateId());
        log.info("newUser " + user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getName() == null) {
            user.setName(getUser(user.getId()).getName());
        }
        if (user.getLogin() == null) {
            user.setLogin(getUser(user.getId()).getLogin());
        }
        if (user.getEmail() == null) {
            user.setEmail(getUser(user.getId()).getEmail());
        }
        if (user.getBirthday() == null) {
            user.setBirthday(getUser(user.getId()).getBirthday());
        }
        user.setFriends(getUser(user.getId()).getFriends());
        log.info("updateUser " + user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void checkUserLogin(User userLogin, boolean update) {
        if (userLogin.getLogin().contains(" ")) {
            log.error("Логин не может содержать пробелы.");
            throw new ValidationException("Логин не может содержать пробелы.");
        }
        for (User user : users.values()) {
            if (update) {
                if (user.getLogin().equals(userLogin.getLogin()) && !user.getId().equals(userLogin.getId())) {
                    log.error("Пользователь с таким логином: " + userLogin.getLogin() + " уже существует");
                    throw new ObjectAlreadyExistsException("Пользователь с таким логином: " + userLogin.getLogin() + " уже существует");
                }
            } else {
                if (user.getLogin().equals(userLogin.getLogin())) {
                    log.error("Пользователь с таким логином: " + userLogin.getLogin() + " уже существует");
                    throw new ObjectAlreadyExistsException("Пользователь с таким логином: " + userLogin.getLogin() + " уже существует");
                }
            }
        }
    }

    @Override
    public void checkUserId(Long id) {
        if (!users.containsKey(id)) {
            log.error("Пользователя с id: " + id + " не существует.");
            throw new NotFoundException("Пользователя с id: " + id + " не существует.");
        }
    }

    @Override
    public List<User> getFriends(Long id) {
        List<User> result = new ArrayList<>();
        for (Long idFriend : getUser(id).getFriends()) {
            result.add(getUser(idFriend));
        }
        return result;
    }

    @Override
    public List<User> commonFriends(Long idOne, Long idTwo) {
        List<User> result = new ArrayList<>();
        for (Long id : getUser(idOne).getFriends()) {
            if (getUser(idTwo).getFriends().contains(id)) {
                result.add(getUser(id));
            }
        }
        return result;
    }

    @Override
    public User getUser(Long id) {
        log.info("getUser " + id);
        return users.getOrDefault(id, null);
    }

    @Override
    public void clearAll() {
        users.clear();
        id = 1L;
        log.info("Clear all users");
    }

    @Override
    public void addFriend(Long idUserOne, Long idUserTwo) {
        getUser(idUserOne).addFriend(idUserTwo);
        getUser(idUserTwo).addFriend(idUserOne);
        log.info(getUser(idUserOne).getName() + " friend " + getUser(idUserTwo).getName());
    }

    @Override
    public void removeFriend(Long idUserOne, Long idUserTwo) {
        getUser(idUserOne).removeFriend(idUserTwo);
        getUser(idUserTwo).removeFriend(idUserOne);
        log.info(getUser(idUserOne).getName() + " remove friend " + getUser(idUserTwo).getName());
    }
}
