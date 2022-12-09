package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Film {
    private Long id;
    private Set<Long> likes = new HashSet<>();
    private List<Genre> genres = new ArrayList<>();
    private int rate;
    private Rating mpa = new Rating();
    @NotNull
    @NotBlank
    private String name;
    @Length(max = 200)
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private int duration;

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

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("rate", rate);
        values.put("rating_id", mpa.getId());
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        return values;
    }
}
