package ru.shishmakov.server.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.shishmakov.server.dao.DbRepository;
import ru.shishmakov.server.entity.Profile;

import java.util.UUID;

/**
 * Access to domain type {@link Profile} by instance of DAO object.
 *
 * @author Dmitriy Shishmakov
 */
@Service("mongoService")
public class MongoDbProfileService implements DbService<Profile, UUID> {

    @Autowired
    @Qualifier("mongoRepository")
    private DbRepository<Profile, UUID> repository;

    @Override
    public Profile getById(final UUID uuid) {
        return repository.findOne(uuid);
    }
}
