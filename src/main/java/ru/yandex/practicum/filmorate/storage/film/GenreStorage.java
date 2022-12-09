package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public interface GenreStorage {
    List<Genre> getAll();

    Genre getById(Long id);

    void checkById(Long id);
}
