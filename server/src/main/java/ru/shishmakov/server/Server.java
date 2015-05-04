package ru.shishmakov.server;


import com.mongodb.Mongo;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import ru.shishmakov.config.AppConfig;
import ru.shishmakov.config.ServerConfig;

import java.lang.invoke.MethodHandles;

/**
 * Netty server of game <i>"Ping Pong"</i>
 *
 * @author Dmitriy Shishmakov
 */
public class Server {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

    private final String host;
    private final int port;

    public Server(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public void run(NioEventLoopGroup bootGroup, NioEventLoopGroup processGroup) throws InterruptedException {
        logger.warn("Initialise server ...");
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(
                ServerConfig.class);
        try {
            final ServerBootstrap server = context.getBean("server", ServerBootstrap.class);
            final Channel serverChannel = server.bind(host, port).sync().channel();
            logger.warn("Start the server: {}. Listen on: {}", this.getClass().getSimpleName(), serverChannel.localAddress());
            serverChannel.closeFuture().sync();
            logger.warn("Shutdown the server: {}", serverChannel);
        } finally {
            // shutdown all events
            bootGroup.shutdownGracefully();
            processGroup.shutdownGracefully();
            // waiting termination of all threads
            bootGroup.terminationFuture().sync();
            processGroup.terminationFuture().sync();
        }
    }

    public static void main(final String[] args) {
        final AbstractApplicationContext context = new AnnotationConfigApplicationContext(
                ServerConfig.class);
        final Mongo mongoClient = context.getBean(Mongo.class);
        final AppConfig config = context.getBean(AppConfig.class);
        final NioEventLoopGroup bootGroup = context.getBean("bootGroup", NioEventLoopGroup.class);
        final NioEventLoopGroup processGroup = context.getBean("processGroup", NioEventLoopGroup.class);
        try {
            logger.warn("Check connection to MongoDB ... ");
            mongoClient.isLocked();
            final String host = config.getBindHost();
            final int port = config.getBindPort();
            logger.warn("Connected to MongoDB on {}:{}", host, port);
            new Server(host, port).run(bootGroup, processGroup);
        } catch (Exception e) {
            logger.error("The server failure: " + e.getMessage(), e);
        }
    }

}
