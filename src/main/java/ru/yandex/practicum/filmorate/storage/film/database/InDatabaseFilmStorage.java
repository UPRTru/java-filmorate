package ru.yandex.practicum.filmorate.storage.film.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@Qualifier("InDatabaseFilmStorage")
@RequiredArgsConstructor
public class InDatabaseFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAll() {
        String findAllFilms = "SELECT FILMS.*, RATINGS.RATING_NAME " +
                "FROM FILMS, RATINGS " +
                "WHERE FILMS.RATING_ID = RATINGS.RATING_ID";
        log.info("getAll Films");
        return jdbcTemplate.query(findAllFilms, this::mapRowToFilm);
    }

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        log.info("newFilm " + film);
        return updateGenres(film);
    }

    private Film CheckFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();
    }

    @Override
    public void checkNotFoundFilm(Film film) {
        try {
            String findFilmById = "SELECT * " +
                    "FROM FILMS " +
                    "WHERE NAME = ? AND RELEASE_DATE = ?";
            jdbcTemplate.queryForObject(findFilmById, this::CheckFilm, film.getName(), film.getReleaseDate());
            log.error("Такой фильм уже существует " + film);
            throw new ObjectAlreadyExistsException("Такой фильм уже существует " + film);
        } catch (Exception e) {
        }
    }

    @Override
    public void checkIdFilm(Long id) {
        try {
            String findFilmById = "SELECT * " +
                    "FROM FILMS " +
                    "WHERE FILM_ID = ?";
            jdbcTemplate.queryForObject(findFilmById, this::CheckFilm, id);
        } catch (Exception e) {
            log.error("Фильм с id: " + id + " не найден");
            throw new NotFoundException("Фильм с id: " + id + " не найден");
        }
    }

    @Override
    public Film update(Film film) {
        String updateFilm = "UPDATE FILMS " +
                "SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? " +
                "WHERE FILM_ID = ?";
        jdbcTemplate.update(updateFilm
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());
        String DELETE_GENRES_BY_FILM_ID = "DELETE FROM film_genre " +
                "WHERE film_id = ?";
        jdbcTemplate.update(DELETE_GENRES_BY_FILM_ID, film.getId());
        log.info("updateFilm " + film);
        return updateGenres(film);
    }

    @Override
    public Film getFilm(Long id) {
        String findFilmById = "SELECT FILMS.*, RATINGS.RATING_NAME " +
                "FROM FILMS, RATINGS " +
                "WHERE FILMS.RATING_ID = RATINGS.RATING_ID AND FILM_ID = ?";
        log.info("getFilm" + id);
        return jdbcTemplate.queryForObject(findFilmById, this::mapRowToFilm, id);
    }

    @Override
    public List<Film> listPopularFilms(int count) {
        String findPopularFilmsWithLikes = "SELECT f.*, R.RATING_NAME " +
                "FROM FILMS f " +
                "LEFT JOIN FILM_LIKES fl ON fl.FILM_ID = f.FILM_ID " +
                "LEFT JOIN RATINGS R on f.RATING_ID = R.RATING_ID " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(fl.USER_ID) DESC " +
                "LIMIT ?";
        log.info("listPopularFilms");
        return jdbcTemplate.query(findPopularFilmsWithLikes, this::mapRowToFilm, count);
    }

    @Override
    public void clearAll() {
        String clearAll = "DELETE FROM FILMS ";
        log.info("Clear all films");
        jdbcTemplate.update(clearAll);
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        String insertFilmLike = "INSERT INTO FILM_LIKES (FILM_ID, USER_ID) " +
                "VALUES(?, ?)";
        jdbcTemplate.update(insertFilmLike, filmId, userId);
        log.info("Film id - " + filmId + " like user id - " + userId);
        return getFilm(filmId);
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        String deleteFilmLike = "DELETE FROM FILM_LIKES " +
                "WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(deleteFilmLike, filmId, userId);
        log.info("Film id - " + filmId + " remove like user id - " + userId);
        return getFilm(filmId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .mpa(new Rating(resultSet.getLong("rating_id"), resultSet.getString("rating_name")))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .genres(getSetGenres(resultSet))
                .build();
    }

    private Rating getSetRating(ResultSet rs) throws SQLException {
        String findMpaById = "SELECT * " +
                "FROM RATINGS " +
                "WHERE RATING_ID = ?";

        Rating rating = new Rating();
        rating.setId(rs.getLong("rating_id"));
        rating.setName(jdbcTemplate.queryForObject(findMpaById, this::mapRowToRating, rating.getId()).getName());
        log.info("getSetRating " + rs);
        return rating;
    }

    private Film updateGenres(Film film) {
        if (film.getGenres() != null) {
            if (film.getGenres().size() == 0) {
                return film;
            }
            String insertFilmGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) " +
                    "VALUES(?, ?)";
            film.getGenres().stream()
                    .map(Genre::getId)
                    .distinct()
                    .forEach(id -> jdbcTemplate.update(insertFilmGenre, film.getId(), id));
        }
        log.info("updateGenres " + film);
        return getFilm(film.getId());
    }

    private List<Genre> getSetGenres(ResultSet rs) throws SQLException {
        String findGenreByFilmId = "SELECT fg.GENRE_ID, " +
                "g.name " +
                "FROM FILM_GENRE fg " +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.FILM_ID = ?";
        Long filmId = rs.getLong("film_id");
        log.info("getSetGenres " + rs);
        return jdbcTemplate.query(findGenreByFilmId, this::mapRowToGenre, filmId);
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getLong("rating_id"))
                .name(resultSet.getString("rating_name"))
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
