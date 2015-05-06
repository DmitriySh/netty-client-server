package ru.shishmakov.server;


import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import ru.shishmakov.config.AppConfig;
import ru.shishmakov.server.config.ServerConfig;

import com.mongodb.Mongo;
import com.mongodb.ServerAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;

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
  private final ServerBootstrap server;

  public Server(final AbstractApplicationContext context) {
    final AppConfig config = context.getBean(AppConfig.class);
    this.server = context.getBean("server", ServerBootstrap.class);
    this.host = config.getBindHost();
    this.port = config.getBindPort();
  }

  public void run() throws InterruptedException {
    logger.warn("Initialise server ...");
    final Channel serverChannel = server.bind(host, port).sync().channel();
    logger.warn("Start the server: {}. Listen on: {}", this.getClass()
        .getSimpleName(), serverChannel.localAddress());
    serverChannel.closeFuture().sync();
    logger.warn("Shutdown the server: {}", serverChannel);
  }

  public static void main(final String[] args) {
    try (AbstractApplicationContext context = new AnnotationConfigApplicationContext(
        ServerConfig.class)) {
      context.registerShutdownHook();
      checkDbConnection(context);
      new Server(context).run();
    } catch (Exception e) {
      logger.error("The server failure: " + e.getMessage(), e);
    }
  }

  private static void checkDbConnection(final AbstractApplicationContext context) {
    logger.warn("Check connection to MongoDB ... ");
    final Mongo mongoClient = context.getBean(Mongo.class);
    final ServerAddress address = mongoClient.getAddress();
    logger.warn("Connected to MongoDB on {}:{}", address.getHost(),
        address.getPort());
  }

}
