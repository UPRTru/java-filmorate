package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.memory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.memory.InMemoryUserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TestFilmService {
    FilmService filmService = new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage());

    @Test
    void TestFilm() {
        assertNotNull(filmService.getAll(), "HashMap должна быть проинициализирована.");
        assertEquals(filmService.getAll().size(), 0, "HashMap должна быть пустой.");

        Film film = Film.builder().build();
        Exception thrown;
        assertThrows(NullPointerException.class, () -> filmService.add(film));
        film.setName("Name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(0);
        thrown = assertThrows(ValidationException.class, () -> filmService.add(film));
        assertEquals("дата релиза — не раньше 28 декабря 1895 года", thrown.getMessage());
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        filmService.add(film);
        assertEquals(filmService.getAll().get(0), film, "Фильмы должны совпадать.");
        film.setId(1L);
        thrown = assertThrows(ObjectAlreadyExistsException.class, () -> filmService.add(film));
        assertEquals("Такой фильм уже существует " + film, thrown.getMessage());
        film.setId(2L);
        thrown = assertThrows(NotFoundException.class, () -> filmService.update(film));
        assertEquals("Фильм 2 не найден.", thrown.getMessage());

        assertNotNull(filmService.getAll(), "HashMap не должна быть пустой.");
        assertEquals(filmService.getAll().size(), 1, "В HashMap должна быть одна запись.");
        filmService.clearAll();
        assertEquals(filmService.getAll().size(), 0, "HashMap должна быть пустой.");
    }
}
