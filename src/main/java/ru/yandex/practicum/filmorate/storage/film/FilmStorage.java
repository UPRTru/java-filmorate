package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {
    List<Film> getAll();

    Film add(Film film);

    Film update(Film film);

    Film getFilm(Long id);

    void clearAll();

    Film addLike(Long filmId, Long userId);

    Film removeLike(Long filmId, Long userId);

    List<Film> listPopularFilms(int limit);

    void checkNotFoundFilm(Film film);

    void checkIdFilm(Long id);
}
