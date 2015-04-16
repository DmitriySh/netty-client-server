package ru.shishmakov.config;


import com.mongodb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collections;

public class Database {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

    private static final Object lock = new Object();

    private static volatile MongoClient instance;
    private static volatile DBCollection collection;

    private Database() {
    }

    public static MongoClient getInstance(final Config config) throws Exception {
        if (config == null) {
            throw new IllegalArgumentException("The database didn't start: configuration was uninitialized");
        }
        MongoClient result = instance;
        if (result == null) {
            synchronized (lock) {
                result = instance;
                if (result == null) {
                    final String host = config.getString("database.host");
                    final int port = config.getInt("database.port");
                    final String user = config.getString("database.user");
                    final String password = config.getString("database.password");
                    final String databaseName = config.getString("database.databaseName");

                    final MongoCredential credential =
                            MongoCredential.createMongoCRCredential(user, databaseName, password.toCharArray());
                    final ServerAddress serverAddress = new ServerAddress(host, port);
                    result = new MongoClient(serverAddress, Collections.singletonList(credential));
                    final DB db = result.getDB(databaseName);
                    collection = db.getCollection(databaseName);

                    logger.info("Connected to MongoDB on {}:{}", host, port);
                    instance = result;
                }
            }
        }
        return result;
    }

    public static DBCollection getDBCollection() {
        return collection;
    }

}

