package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exception.YourselfException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("InDatabaseUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) {
        userStorage.checkUserLogin(user, false);
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        return userStorage.add(user);
    }

    public User update(User user) {
        userStorage.checkUserId(user.getId());
        userStorage.checkUserLogin(user, true);
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(Long id) {
        userStorage.checkUserId(id);
        return userStorage.getUser(id);
    }

    public void clearAll() {
        userStorage.clearAll();
    }

    public void addFriend(Long idUserOne, Long idUserTwo) {
        checkYourself(idUserOne, idUserTwo);
        userStorage.checkUserId(idUserOne);
        userStorage.checkUserId(idUserTwo);
        userStorage.addFriend(idUserOne, idUserTwo);
    }

    public void removeFriend(Long idUserOne, Long idUserTwo) {
        checkYourself(idUserOne, idUserTwo);
        userStorage.checkUserId(idUserOne);
        userStorage.checkUserId(idUserTwo);
        userStorage.removeFriend(idUserOne, idUserTwo);
    }

    private void checkYourself(Long idOne, Long idTwo) {
        if (idOne.equals(idTwo)) {
            log.error("Пользователь не может удалить или добавить в друзья себя.");
            throw new YourselfException("Пользователь не может удалить или добавить в друзья себя.");
        }
    }

    public List<User> getFriends(Long id) {
        userStorage.checkUserId(id);
        return userStorage.getFriends(id);
    }

    public List<User> commonFriends(Long idOne, Long idTwo) {
        userStorage.checkUserId(idOne);
        userStorage.checkUserId(idTwo);
        return userStorage.commonFriends(idOne, idTwo);
    }
}
