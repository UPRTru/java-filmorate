package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestFilmService extends TestService {
    FilmService filmService = new FilmService();

    @Override
    protected FilmService getService() {
        return filmService;
    }

    @Override
    protected void custom() {
        Film film = new Film();
        ValidationException thrown;
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
        film.setId(1);
        thrown = assertThrows(ValidationException.class, () -> filmService.add(film));
        assertEquals("Фильм с id: 1 уже существует", thrown.getMessage());
        film.setId(2);
        thrown = assertThrows(ValidationException.class, () -> filmService.update(film));
        assertEquals("Фильм с id: 2 не найден", thrown.getMessage());
    }
}
