package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TestService <T extends Service<T>>{
    protected T service;
    protected abstract T getService();

    @BeforeEach
    void createService() {
        service = getService();
    }

    @Test
    void allTest() {
        assertNotNull(service.getAll(), "HashMap должна быть проинициализирована.");
        assertEquals(service.getAll().size(), 0, "HashMap должна быть пустой.");
        custom();
        assertNotNull(service.getAll(), "HashMap не должна быть пустой.");
        assertEquals(service.getAll().size(), 1, "В HashMap должна быть одна запись.");
        service.clearAll();
        assertEquals(service.getAll().size(), 0, "HashMap должна быть пустой.");
    }

    protected abstract void custom();
}
