package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestFilmService extends TestService {
    FilmService filmService = new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage());

    @Override
    protected FilmService getService() {
        return filmService;
    }

    @Override
    protected void custom() {
        Film film = new Film();
        Exception thrown;
        assertThrows(NullPointerException.class, () -> filmService.add(film));
        film.setName("Name");
        film.setDescription("Description");
        Calendar calendar = Calendar.getInstance();
        calendar.set(1895, Calendar.DECEMBER, 27,0,0);
        film.setReleaseDate(calendar.getTime());
        film.setDuration(0);
        thrown = assertThrows(ValidationException.class, () -> filmService.add(film));
        assertEquals("дата релиза — не раньше 28 декабря 1895 года", thrown.getMessage());
        calendar.set(1895, Calendar.DECEMBER, 28,0,0);
        film.setReleaseDate(calendar.getTime());
        filmService.add(film);
        assertEquals(filmService.getAll().get(0) , film, "Фильмы должны совпадать.");
        film.setId(1L);
        thrown = assertThrows(ObjectAlreadyExistsException.class, () -> filmService.add(film));
        assertEquals("Такой фильм уже существует " + film, thrown.getMessage());
        film.setId(2L);
        thrown = assertThrows(NotFoundException.class, () -> filmService.update(film));
        assertEquals("Фильм с id: 2 не найден", thrown.getMessage());
    }
}
