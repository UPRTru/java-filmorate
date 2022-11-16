package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Data
@RequiredArgsConstructor
public class Film {
    private Set<Long> likes = new HashSet<>();
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @Length(max = 200)
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;
    @NotNull
    @Positive
    private double duration;

    public void addLike(Long id) {
        likes.add(id);
    }

    public void removeLike(Long id) {
        if (!likes.contains(id)) {
            log.error("Пользователь id" + id + " не ставил лайк фильму id" + this.id);
            throw new NotFoundException("Пользователь id" + id + " не ставил лайк фильму id" + this.id);
        }
        likes.remove(id);
    }
}
