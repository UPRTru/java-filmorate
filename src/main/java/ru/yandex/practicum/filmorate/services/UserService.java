package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.YourselfException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@org.springframework.stereotype.Service
public class UserService implements Service<User> {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User add(User user) {
        return userStorage.add(user);
    }

    @Override
    public User update(User user) {
        return userStorage.update(user);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User getById(Long id) {
        return userStorage.getUser(id);
    }

    @Override
    public void clearAll() {
        userStorage.clearAll();
    }

    public void addFriend(Long idUserOne, Long idUserTwo) {
        checkYourself(idUserOne, idUserTwo);
        checkUserId(idUserOne);
        checkUserId(idUserTwo);
        userStorage.getUser(idUserOne).addFriend(idUserTwo);
        userStorage.getUser(idUserTwo).addFriend(idUserOne);
        log.info(userStorage.getUser(idUserOne).getName() + " friend " + userStorage.getUser(idUserTwo).getName());
    }

    public void removeFriend(Long idUserOne, Long idUserTwo) {
        checkYourself(idUserOne, idUserTwo);
        checkUserId(idUserOne);
        checkUserId(idUserTwo);
        userStorage.getUser(idUserOne).removeFriend(idUserTwo);
        userStorage.getUser(idUserTwo).removeFriend(idUserOne);
        log.info(userStorage.getUser(idUserOne).getName() + " remove friend " + userStorage.getUser(idUserTwo).getName());
    }

    private void checkUserId(Long id) {
        if (userStorage.getUser(id) == null) {
            log.error("id" + id + " пользователь не найден.");
            throw new NotFoundException("id" + id + " пользователь не найден.");
        }
    }

    private void checkYourself(Long idOne, Long idTwo) {
        if (idOne.equals(idTwo)) {
            log.error("Пользователь не может удалить или добавить в друзья себя.");
            throw new YourselfException("Пользователь не может удалить или добавить в друзья себя.");
        }
    }

    public List<User> getFriends(Long id) {
        List<User> result = new ArrayList<>();
        checkUserId(id);
        for (Long idFriend : userStorage.getUser(id).getFriends()) {
            result.add(userStorage.getUser(idFriend));
        }
        return result;
    }

    public List<User> commonFriends(Long idOne, Long idTwo) {
        List<User> result = new ArrayList<>();
        checkUserId(idOne);
        checkUserId(idTwo);
        for (Long id : userStorage.getUser(idOne).getFriends()) {
            if (userStorage.getUser(idTwo).getFriends().contains(id)) {
                result.add(userStorage.getUser(id));
            }
        }
        return result;
    }
}
