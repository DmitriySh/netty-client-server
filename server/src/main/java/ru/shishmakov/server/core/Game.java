package ru.shishmakov.server.core;

import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.shishmakov.config.AppConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.invoke.MethodHandles;

/**
 * Manage life cycle of game <i>"Ping Pong"</i> by Netty server.
 *
 * @author Dmitriy Shishmakov
 */
@Component
public class Game {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

    @Autowired
    private AppConfig config;

    @Autowired
    @Qualifier("server")
    private ServerBootstrap server;

    @Autowired
    @Qualifier("mongo")
    private Mongo mongo;

    private Channel serverChannel;
    private String host;
    private int port;

    @PostConstruct
    public void init() {
        this.host = config.getBindHost();
        this.port = config.getBindPort();
    }

    public void start() throws InterruptedException {
        logger.debug("Initialise server ...");
        serverChannel = server.bind(host, port).sync().channel();
        logger.info("Start the server: {}. Listen on: {}", this.getClass().getSimpleName(),
                serverChannel.localAddress());
        serverChannel.closeFuture().sync();
    }

    public void checkDbConnection() {
        logger.debug("Check connection to MongoDB ... ");
        mongo.isLocked();
        final ServerAddress address = mongo.getAddress();
        logger.debug("Connected to MongoDB on {}:{}", address.getHost(), address.getPort());
    }

    public void stop() throws InterruptedException {
        logger.debug("Finalization server ...");
        serverChannel.close();
        logger.info("Shutdown the server: {}", serverChannel);
    }
}
