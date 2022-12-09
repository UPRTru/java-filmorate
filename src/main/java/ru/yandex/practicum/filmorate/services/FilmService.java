package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("InDatabaseFilmStorage") FilmStorage filmStorage,
                       @Qualifier("InDatabaseUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film add(Film film) {
        filmStorage.checkNotFoundFilm(film);
        checkDate(film.getReleaseDate());
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        filmStorage.checkIdFilm(film.getId());
        checkDate(film.getReleaseDate());
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Long id) {
        filmStorage.checkIdFilm(id);
        return filmStorage.getFilm(id);
    }

    public void clearAll() {
        filmStorage.clearAll();
    }

    public Film addLike(Long filmId, Long userId) {
        filmStorage.checkIdFilm(filmId);
        userStorage.checkUserId(userId);
        return filmStorage.addLike(filmId, userId);
    }

    public Film removeLike(Long filmId, Long userId) {
        filmStorage.checkIdFilm(filmId);
        userStorage.checkUserId(userId);
        return filmStorage.removeLike(filmId, userId);
    }

    public List<Film> listPopularFilms(int limit) {
        return filmStorage.listPopularFilms(limit);
    }

    private void checkDate(LocalDate date) {
        if (date.isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
    }
}
