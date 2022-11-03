package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.Service;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Service<Film> service = new FilmService();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return service.add(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return service.update(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        return service.getAll();
    }
}
