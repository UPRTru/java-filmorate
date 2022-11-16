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
}
