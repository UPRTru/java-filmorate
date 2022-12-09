package ru.yandex.practicum.filmorate.storage.film.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        film.setId(generateId());
        log.info("addFilm " + film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void checkNotFoundFilm(Film film) {
        for (Film f : films.values()) {
            if (f.getName().equals(film.getName()) && f.getReleaseDate().equals(film.getReleaseDate())) {
                log.error("Такой фильм уже существует " + film);
                throw new ObjectAlreadyExistsException("Такой фильм уже существует " + film);
            }
        }
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            film.setId(generateId());
        }
        log.info("updateFilm " + film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilm(Long id) {
        log.info("getFilm " + id);
        return films.getOrDefault(id, null);
    }

    @Override
    public void checkIdFilm(Long id) {
        if (!films.containsKey(id)) {
            log.error("Фильм " + id + " не найден.");
            throw new NotFoundException("Фильм " + id + " не найден.");
        }
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        films.get(filmId).addLike(userId);
        log.info(films.get(filmId).getName() + " like film. User id - " + userId);
        return films.get(filmId);
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        films.get(filmId).removeLike(userId);
        log.info(films.get(filmId).getName() + " remove like film. User id - " + userId);
        return films.get(filmId);
    }

    @Override
    public List<Film> listPopularFilms(int limit) {
        List<Film> sortedFilms = getAll();
        sortedFilms.sort((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()));
        log.info("get popular films");
        return sortedFilms.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public void clearAll() {
        films.clear();
        id = 1L;
        log.info("Clear all films");
    }
}
