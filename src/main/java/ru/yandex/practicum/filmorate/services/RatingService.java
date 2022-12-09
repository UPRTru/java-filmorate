package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.RatingStorage;

import java.util.List;

@Slf4j
@org.springframework.stereotype.Service
public class RatingService {
    private final RatingStorage ratingStorage;

    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public List<Rating> getAll() {
        return ratingStorage.getAll();
    }

    public Rating getById(Long ratingId) {
        if (ratingId <= 0) {
            throw new NotFoundException("id рейтинга должен быть больше 0");
        }
        ratingStorage.checkRating(ratingId);
        return ratingStorage.getById(ratingId);
    }
}
