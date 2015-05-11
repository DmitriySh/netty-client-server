package ru.shishmakov.server.dao;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;

/**
 * Captures the domain type to manage. Exists solely to provide a connection to the DB.
 */
@NoRepositoryBean
public interface DbRepository<T, ID extends Serializable> extends Repository<T, ID> {

    /**
     * Retrieves an entity by its id.
     *
     * @param id must not be {@literal null}.
     * @return the entity with the given {@literal id}.
     */
    T findOne(final ID id);
}
