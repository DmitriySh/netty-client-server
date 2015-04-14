package ru.shishmakov.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.InetSocketAddress;

/**
 *
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

    public void run() throws InterruptedException {
        // 1 thread: one by one
        final NioEventLoopGroup bootGroup = new NioEventLoopGroup(1);
        // N threads: depends by cores or value of  system property
        final NioEventLoopGroup processGroup = new NioEventLoopGroup();

        try {
            final ServerBootstrap server = new ServerBootstrap();
            server.group(bootGroup, processGroup)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerChannelHandler());

            final Channel serverChannel = server.bind(host, port).sync().channel();
            logger.info("Start the server: {}. Listen on: {}", this.getClass().getSimpleName(), serverChannel.localAddress());
            serverChannel.closeFuture().sync();
            logger.info("Shutdown the server: {}", serverChannel);
        } catch (Exception e) {
            // shutdown all events
            bootGroup.shutdownGracefully();
            processGroup.shutdownGracefully();
            // waiting termination of all threads
            bootGroup.terminationFuture().sync();
            processGroup.terminationFuture().sync();
        }
    }

    public static void main(final String[] args) {
        if (args.length != 2) {
            logger.error("Failed to start; pay attention to use {}: <host> <port>", Server.class.getSimpleName());
            return;
        }
        try {
            final String host = String.valueOf(args[0]);
            final int port = Integer.parseInt(args[1]);
            new Server(host, port).run();
        } catch (Exception e) {
            logger.error("The server failure: " + e.getMessage(), e);
        }
    }

}
