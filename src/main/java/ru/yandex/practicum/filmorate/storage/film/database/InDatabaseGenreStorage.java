package ru.yandex.practicum.filmorate.storage.film.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InDatabaseGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        final String findAllGenre = "SELECT * " +
                "FROM GENRE " +
                "ORDER BY genre_id";
        log.info("getAll Genre");
        return jdbcTemplate.query(findAllGenre, this::mapRowToGenre);
    }

    @Override
    public Genre getById(Long genreId) {
        final String findGenreById = "SELECT * " +
                "FROM GENRE " +
                "WHERE genre_id = ?";
        log.info("getGenre " + genreId);
        return jdbcTemplate.queryForObject(findGenreById, this::mapRowToGenre, genreId);
    }

    @Override
    public void checkById(Long id) {
        try{
            final String findGenreById = "SELECT * " +
                    "FROM GENRE " +
                    "WHERE genre_id = ?";
            jdbcTemplate.queryForObject(findGenreById, this::mapRowToGenre, id);
        } catch (Exception e) {
            log.error("Жанра с id: " + id + " не найден");
            throw new NotFoundException("Такого жанра нет.");
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}

