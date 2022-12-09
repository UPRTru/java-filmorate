package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {

    List<User> getAll();

    User add(User user);

    User update(User user);

    User getUser(Long id);

    void clearAll();

    void addFriend(Long idUserOne, Long idUserTwo);

    void removeFriend(Long idUserOne, Long idUserTwo);

    void checkUserLogin(User userLogin, boolean update);

    void checkUserId(Long id);

    List<User> getFriends(Long id);

    List<User> commonFriends(Long idOne, Long idTwo);
}
