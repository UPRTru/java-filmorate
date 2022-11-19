package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Service
public class FilmService implements Service<Film> {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film add(Film film) {
        return filmStorage.add(film);
    }

    @Override
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film getById(Long id) {
        return filmStorage.getFilm(id);
    }

    @Override
    public void clearAll() {
        filmStorage.clearAll();
    }

    public Film addLike(Long filmId, Long userId) {
        checkExistUserAndFilm(filmId, userId);
        filmStorage.getFilm(filmId).addLike(userId);
        log.info(filmStorage.getFilm(filmId).getName() + " like film. User - " + userStorage.getUser(userId).getName());
        return filmStorage.getFilm(filmId);
    }

    public Film removeLike(Long filmId, Long userId) {
        checkExistUserAndFilm(filmId, userId);
        filmStorage.getFilm(filmId).removeLike(userId);
        log.info(filmStorage.getFilm(filmId).getName() + " remove like film. User - " + userStorage.getUser(userId).getName());
        return filmStorage.getFilm(filmId);
    }

    private void checkExistUserAndFilm(Long filmId, Long userId) {
        if (userStorage.getUser(userId) == null) {
            log.error("Пользователь id" + userId + " не найден.");
            throw new NotFoundException("Пользователь id" + userId + " не найден.");
        }
        if (filmStorage.getFilm(filmId) == null) {
            log.error("Фильм id" + filmId + " не найден.");
            throw new NotFoundException("Фильм id" + filmId + " не найден.");
        }
    }

    public List<Film> listPopularFilms(int limit) {
        List<Film> sortedFilms = filmStorage.getAll();
        sortedFilms.sort((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()));
        log.info("get popular films");
        return sortedFilms.stream().limit(limit).collect(Collectors.toList());
    }
}
