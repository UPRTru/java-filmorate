package ru.yandex.practicum.filmorate.services;

import java.util.List;

public interface Service<T> {

    T add(T t);

    T update(T t);

    List<T> getAll();

    T getById(Long id);

    void clearAll();
}
