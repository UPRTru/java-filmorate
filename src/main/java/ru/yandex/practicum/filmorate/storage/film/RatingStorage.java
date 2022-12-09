package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingStorage {
    List<Rating> getAll();

    Rating getById (Long ratingId);

    void checkRating(Long id);
}
