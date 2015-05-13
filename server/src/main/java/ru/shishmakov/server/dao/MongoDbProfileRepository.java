package ru.shishmakov.server.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import ru.shishmakov.server.entity.Profile;

import java.util.UUID;


/**
 * Captures the domain type {@link Profile} to manage by Mongo DB.
 *
 * @author Dmitriy Shishmakov
 */
@Repository("mongoRepository")
public class MongoDbProfileRepository implements DbRepository<Profile, UUID> {

    @Autowired
    @Qualifier("serverMongoTemplate")
    private MongoOperations mongoOperations;

    @Override
    public Profile findOne(final UUID uuid) {
        final Query findQuery = new Query(Criteria.where("profileid").is(uuid));

        final Update updateQuery = new Update();
        updateQuery.inc("quantity", 1);

        final FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        options.remove(false);
        options.upsert(true);
        return mongoOperations.findAndModify(findQuery, updateQuery, options, Profile.class);
    }
}
