package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
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

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id: " + film.getId() +" не найден");
            throw new ValidationException("Фильм с id: " + film.getId() +" не найден");
        }
        checkDate(film);
        if (!films.containsKey(film.getId())) {
            film.setId(generateId());
        }
        log.info("updateFilm " + film);
        films.put(film.getId(), film);
        return film;
    }

    private int generateId() {
        return id++;
    }

    @GetMapping
    public List<Film> getFilms() {
        if (films.isEmpty()) {
            log.error("Список фильмов пуст.");
            throw new ValidationException("Список фильмов пуст.");
        }
        return new ArrayList<>(films.values());
    }

    private void checkDate (Film film) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1895, Calendar.DECEMBER, 28);
        if (film.getReleaseDate().before(calendar.getTime())) {
            log.error("дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
    }
}
