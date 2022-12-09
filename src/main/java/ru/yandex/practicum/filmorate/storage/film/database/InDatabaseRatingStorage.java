package ru.yandex.practicum.filmorate.storage.film.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InDatabaseRatingStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Rating> getAll() {
        String FIND_ALL_MPA = "SELECT * " +
                "FROM RATINGS";
        return jdbcTemplate.query(FIND_ALL_MPA, this::mapRowToRating);
    }

    @Override
    public Rating getById(Long ratingId) {
        String FIND_MPA_BY_ID = "SELECT * " +
                "FROM RATINGS " +
                "WHERE RATING_ID = ?";
        return jdbcTemplate.queryForObject(FIND_MPA_BY_ID, this::mapRowToRating, ratingId);
    }

    @Override
    public void checkRating(Long id) {
        try {
            String findRatingById = "SELECT * " +
                    "FROM RATINGS " +
                    "WHERE RATING_ID = ?";
            jdbcTemplate.queryForObject(findRatingById, this::mapRowToRating, id);
        } catch (Exception e) {
            log.error("Рейтинг с id: " + id + " не найден");
            throw new NotFoundException("Рейтинг с id: " + id + " не найден");
        }
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getLong("rating_id"))
                .name(resultSet.getString("rating_name"))
                .build();
    }
}
