package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    private Long id;
    private Set<Long> friends = new HashSet<>();
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
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthday;

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

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("name", name);
        values.put("birthday", birthday);
        return values;
    }
}
