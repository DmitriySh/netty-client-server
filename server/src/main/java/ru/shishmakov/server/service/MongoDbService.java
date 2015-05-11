package ru.shishmakov.server.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.shishmakov.server.dao.DbRepository;
import ru.shishmakov.server.entity.Profile;

import java.util.UUID;

/**
 * @author Dmitriy Shishmakov
 */
@Service("mongoService")
public class MongoDbService implements DbService<Profile, UUID> {

    @Autowired
    @Qualifier("mongoRepository")
    private DbRepository<Profile, UUID> repository;

    @Override
    public Profile getById(final UUID uuid) {
        return repository.findOne(uuid);
    }
}
