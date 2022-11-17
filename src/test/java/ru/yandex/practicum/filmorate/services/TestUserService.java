package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestUserService extends TestService {
    UserService userService = new UserService(new InMemoryUserStorage());
    @Override
    protected UserService getService() {
        return userService;
    }

    @Override
    protected void custom() {
        User user = new User();
        Exception thrown;
        assertThrows(NullPointerException.class, () -> userService.add(user));
        user.setEmail("email@test.ru");
        user.setLogin("Log in");
        user.setName("Name");
        Calendar calendar = Calendar.getInstance();
        user.setBirthday(calendar.getTime());
        thrown = assertThrows(ValidationException.class, () -> userService.add(user));
        assertEquals("Логин не может содержать пробелы.", thrown.getMessage());
        user.setLogin("Login");
        userService.add(user);
        assertEquals(userService.getAll().get(0), user, "Пользователи должна совпадать.");
        user.setId(0L);
        thrown = assertThrows(ObjectAlreadyExistsException.class, () -> userService.add(user));
        assertEquals("Пользователь с таким логином: Login уже существует", thrown.getMessage());
        thrown = assertThrows(NotFoundException.class, () -> userService.update(user));
        assertEquals("Пользователя с id: 0 не существует.", thrown.getMessage());
    }
}
