package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Data
@RequiredArgsConstructor
public class User {
    private Set<Long> friends = new HashSet<>();
    private Long id;
    @Email
    @NotNull
    @NotBlank
    private String email;
    @NotNull
    @NotBlank
    private String login;
    private String name;
    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    public void addFriend(Long id) {
        friends.add(id);
    }

    public void removeFriend(Long id) {
        if (!friends.contains(id)) {
            log.error("Пользователь id" + id + " не найден в списке друзей пользоваьеля id" + this.id);
            throw new NotFoundException("Пользователь id" + id + " не найден в списке друзей пользоваьеля id" + this.id);
        }
        friends.remove(id);
    }
}
