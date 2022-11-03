package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
public class FilmService extends Service<Film> {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    private int generateId() {
        return id++;
    }

    @Override
    public Film add(Film film) {
        if (films.containsKey(film.getId())) {
            log.error("Фильм с id: " + film.getId() + " уже существует");
            throw new ValidationException("Фильм с id: " + film.getId() + " уже существует");
        }
        checkDate(film);
        film.setId(generateId());
        log.info("addFilm " + film);
        films.put(film.getId(), film);
        return film;
    }

    private void checkDate(Film film) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1895, Calendar.DECEMBER, 27,23,59);
        if (film.getReleaseDate().before(calendar.getTime())) {
            log.error("дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id: " + film.getId() + " не найден");
            throw new ValidationException("Фильм с id: " + film.getId() + " не найден");
        }
        checkDate(film);
        if (!films.containsKey(film.getId())) {
            film.setId(generateId());
        }
        log.info("updateFilm " + film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void clearAll() {
        films.clear();
        id = 1;
    }
}
