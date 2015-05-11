package ru.shishmakov.server.service;


import java.io.Serializable;

public interface DbService<T, ID extends Serializable> {

    T getById(final ID id);
}
