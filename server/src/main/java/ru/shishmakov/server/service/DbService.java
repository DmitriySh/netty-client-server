package ru.shishmakov.server.service;


import java.io.Serializable;

public interface DbService<T, ID extends Serializable> {

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given {@literal id}.
     */
    T getById(final ID id);
}
