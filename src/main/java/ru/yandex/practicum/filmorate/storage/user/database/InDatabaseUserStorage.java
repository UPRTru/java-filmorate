package ru.yandex.practicum.filmorate.storage.user.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ValidationException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("InDatabaseUserStorage")
@RequiredArgsConstructor
public class InDatabaseUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAll() {
        String findAllUsers = "SELECT * " +
                "FROM users";
        log.info("getAll Users");
        return jdbcTemplate.query(findAllUsers, this::mapRowToUser);
    }

    @Override
    public User add(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
        log.info("newUser " + user);
        return user;
    }

    @Override
    public void checkUserLogin(User userLogin, boolean update) {
        if (userLogin.getLogin().contains(" ")) {
            log.error("Логин не может содержать пробелы.");
            throw new ValidationException("Логин не может содержать пробелы.");
        }
        if (update) {
            try {
                String findUserById = "SELECT * " +
                        "FROM users " +
                        "WHERE USER_ID != ? AND LOGIN = ?";
                jdbcTemplate.queryForObject(findUserById, this::mapRowToUser, userLogin.getId(), userLogin.getLogin());
                log.error("Пользователь с таким логином: " + userLogin.getLogin() + " уже существует");
                throw new ObjectAlreadyExistsException("Пользователь с таким логином: " + userLogin.getLogin() + " уже существует");
            } catch (Exception e) {
            }
        } else {
            try {
                String findUserById = "SELECT * " +
                        "FROM users " +
                        "WHERE LOGIN = ?";
                jdbcTemplate.queryForObject(findUserById, this::mapRowToUser, userLogin.getLogin());
                log.error("Пользователь с таким логином: " + userLogin.getLogin() + " уже существует");
                throw new ObjectAlreadyExistsException("Пользователь с таким логином: " + userLogin.getLogin() + " уже существует");
            } catch (Exception e) {
            }
        }
    }

    @Override
    public User update(User user) {
        if (user.getName() == null) {
            user.setName(getUser(user.getId()).getName());
        }
        if (user.getLogin() == null) {
            user.setLogin(getUser(user.getId()).getLogin());
        }
        if (user.getEmail() == null) {
            user.setEmail(getUser(user.getId()).getEmail());
        }
        if (user.getBirthday() == null) {
            user.setBirthday(getUser(user.getId()).getBirthday());
        }
        String updateUser = "UPDATE users SET " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE USER_ID = ?";
        jdbcTemplate.update(updateUser
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
        log.info("Update user" + user);
        return getUser(user.getId());
    }

    @Override
    public void checkUserId(Long id) {
        try {
            String findUserById = "SELECT * " +
                    "FROM users " +
                    "WHERE USER_ID = ?";
            jdbcTemplate.queryForObject(findUserById, this::mapRowToUser, id);
        } catch (Exception e) {
            log.error("Пользователя с id: " + id + " не существует.");
            throw new NotFoundException("Пользователя с id: " + id + " не существует.");
        }
    }

    @Override
    public User getUser(Long id) {
        checkUserId(id);
        String findUserById = "SELECT * " +
                "FROM users " +
                "WHERE USER_ID = ?";
        log.info("Get user " + id);
        return jdbcTemplate.queryForObject(findUserById, this::mapRowToUser, id);
    }

    @Override
    public void clearAll() {
        String clearAll = "DELETE FROM users ";
        jdbcTemplate.update(clearAll);
        log.info("Clear all users");
    }

    @Override
    public List<User> getFriends(Long id) {
        String findUserFriendsById = "SELECT u.* " +
                "FROM USER_FRIENDS us " +
                "LEFT JOIN users u ON us.FRIEND_ID = u.USER_ID " +
                "WHERE us.USER_ID = ? ";
        log.info("getFriends user id - " + id);
        return jdbcTemplate.query(findUserFriendsById, this::mapRowToUser, id);
    }

    @Override
    public List<User> commonFriends(Long idOne, Long idTwo) {
        String findFriendsIdByUserId = "SELECT FRIEND_ID " +
                "FROM USER_FRIENDS " +
                "WHERE USER_ID = ? ";
        Set<Long> userFriends = new HashSet<>(new HashSet<>
                (jdbcTemplate.query(findFriendsIdByUserId, (rs, friend_id) -> rs.getLong("friend_id"), idOne)));
        Set<Long> otherFriends = new HashSet<>(new HashSet<>
                (jdbcTemplate.query(findFriendsIdByUserId, (rs, friend_id) -> rs.getLong("friend_id"), idTwo)));
        userFriends.retainAll(otherFriends);
        log.info("commonFriends id userOne - " + idOne + " id userTwo - " + idTwo);
        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public void addFriend(Long idUserOne, Long idUserTwo) {
        String checkFriendship = "SELECT CONFIRMED_BY_FRIEND " +
                "FROM USER_FRIENDS " +
                "WHERE USER_ID = ? " +
                "AND USER_ID = ?";
        SqlRowSet statusRowsUser1 = jdbcTemplate.queryForRowSet(checkFriendship, idUserOne, idUserTwo);
        SqlRowSet statusRowsUser2 = jdbcTemplate.queryForRowSet(checkFriendship, idUserTwo, idUserOne);
        if (!statusRowsUser1.toString().equals("Confirmed")
                && !statusRowsUser1.toString().equals("Not Confirmed")
                && !statusRowsUser2.toString().equals("Confirmed")
                && statusRowsUser2.toString().equals("Not Confirmed")) {
            String updateFriendship = "UPDATE USER_FRIENDS SET " +
                    "CONFIRMED_BY_FRIEND = ? " +
                    "WHERE USER_ID = ?";
            jdbcTemplate.update(updateFriendship, "Confirmed", idUserOne);
        } else {
            String insertFriendship = "INSERT INTO USER_FRIENDS (USER_ID, FRIEND_ID, CONFIRMED_BY_FRIEND) " +
                    "VALUES (?, ?, ?)";
            jdbcTemplate.update(insertFriendship, idUserOne, idUserTwo, "Not Confirmed");
        }
        log.info("addFriend id userOne - " + idUserOne + " id userTwo - " + idUserTwo);
    }

    @Override
    public void removeFriend(Long idUserOne, Long idUserTwo) {
        String deleteFriend = "DELETE FROM USER_FRIENDS " +
                "WHERE USER_ID = ? " +
                "AND FRIEND_ID = ?";
        jdbcTemplate.update(deleteFriend, idUserOne, idUserTwo);
        log.info("removeFriend id userOne - " + idUserOne + " id userTwo - " + idUserTwo);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
