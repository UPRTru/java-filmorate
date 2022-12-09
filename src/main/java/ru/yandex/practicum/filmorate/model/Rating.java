package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Rating {
    private Long id;
    private String name;

    public Rating() {}
    public Rating(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}