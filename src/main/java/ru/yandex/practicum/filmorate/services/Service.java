package ru.yandex.practicum.filmorate.services;

import java.util.List;

public abstract class Service<T> {

    public abstract T add(T t);

    public abstract T update(T t);

    public abstract List<T> getAll();

    public abstract void clearAll();
}
