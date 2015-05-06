package ru.shishmakov.server.helper;


/**
 * Connection to MongoDB.
 *
 * @author Dmitriy Shishmakov
 */
public class Database {
  //    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
  //            .lookup().lookupClass());
  //
  //    private static final Object lock = new Object();
  //
  //    private static volatile MongoClient instance;
  //    private static volatile DBCollection collection;
  //
  //    private Database() {
  //    }
  //
  //    public static MongoClient getInstance(final Config config) throws Exception {
  //        if (config == null) {
  //            throw new IllegalArgumentException("The database didn't start: configuration was uninitialized");
  //        }
  //        MongoClient result = instance;
  //        if (result == null) {
  //            synchronized (lock) {
  //                result = instance;
  //                if (result == null) {
  //                    logger.warn("Initialise connection to MongoDB ... ");
  //                    final String host = config.getString(ConfigKey.DATABASE_HOST);
  //                    final int port = config.getInt(ConfigKey.DATABASE_PORT);
  //                    final String user = config.getString(ConfigKey.DATABASE_USER);
  //                    final String password = config.getString(ConfigKey.DATABASE_PASSWORD);
  //                    final String databaseName = config.getString(ConfigKey.DATABASE_NAME);
  //
  //                    final MongoCredential credential =
  //                            MongoCredential.createMongoCRCredential(user, databaseName, password.toCharArray());
  //                    final ServerAddress serverAddress = new ServerAddress(host, port);
  //                    result = new MongoClient(serverAddress, Collections.singletonList(credential));
  //                    // testing connection
  //                    result.isLocked();
  //                    final DB db = result.getDB(databaseName);
  //                    final DBCollection tempCollect = db.getCollection(databaseName);
  //
  //                    logger.warn("Connected to MongoDB on {}:{}", host, port);
  //                    instance = result;
  //                    collection = tempCollect;
  //                }
  //            }
  //        }
  //        return result;
  //    }
  //
  //    public static DBCollection getDBCollection() {
  //        return collection;
  //    }

}

