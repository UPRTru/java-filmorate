package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.Service;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Service<User> service = new UserService();

    @PostMapping
    public User newUser(@Valid @RequestBody User user) {
        return service.add(user);
    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return service.update(user);
    }

    @GetMapping
    public List<User> getUsers() {
        return service.getAll();
    }
}

