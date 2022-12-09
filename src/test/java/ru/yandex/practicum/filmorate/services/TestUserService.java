package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.memory.InMemoryUserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TestUserService {
    UserService userService = new UserService(new InMemoryUserStorage());

    @Test
    protected void custom() {
        assertNotNull(userService.getAll(), "HashMap должна быть проинициализирована.");
        assertEquals(userService.getAll().size(), 0, "HashMap должна быть пустой.");

        User user = User.builder().build();
        Exception thrown;
        assertThrows(NullPointerException.class, () -> userService.add(user));
        user.setEmail("email@test.ru");
        user.setLogin("Log in");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2010, 12, 31));
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

        assertNotNull(userService.getAll(), "HashMap не должна быть пустой.");
        assertEquals(userService.getAll().size(), 1, "В HashMap должна быть одна запись.");
        userService.clearAll();
        assertEquals(userService.getAll().size(), 0, "HashMap должна быть пустой.");
    }
}
