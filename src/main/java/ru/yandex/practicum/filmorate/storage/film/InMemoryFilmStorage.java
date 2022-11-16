package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long id = 1L;

    private Long generateId() {
        return id++;
    }

    @Override
    public List<Film> getAll() {
        log.info("getAll Films");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film add(Film film) {
        if (films.containsKey(film.getId())) {
            log.error("Фильм с id: " + film.getId() + " уже существует");
            throw new ObjectAlreadyExistsException("Фильм с id: " + film.getId() + " уже существует");
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
            throw new NotFoundException("Фильм с id: " + film.getId() + " не найден");
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
    public Film getFilm(Long id) {
        checkFilm(id);
        log.info("getFilm " + id);
        return films.getOrDefault(id, null);
    }

    private void checkFilm(Long id) {
        if (!films.containsKey(id)) {
            log.error("Фильм " + id + " не найден.");
            throw new NotFoundException("Фильм " + id + " не найден.");
        }
    }

    @Override
    public void clearAll() {
        films.clear();
        id = 1L;
        log.info("Clear all films");
    }
}
