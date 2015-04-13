package ru.shishmakov.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


public class ServerPingPong {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

    public static void main(String[] args) {
        try {
            final int port = Integer.parseInt(args[0]);
            new ServerPingPong().run(port);
        } catch (InterruptedException e) {
            logger.error("The server failure: " + e.getMessage(), e);
        }
    }

    public void run(final int port) throws InterruptedException {
        final NioEventLoopGroup bootGroup = new NioEventLoopGroup(1);
        final NioEventLoopGroup processGroup = new NioEventLoopGroup(1);

        try {
            final ServerBootstrap server = new ServerBootstrap();
            server.group(bootGroup, processGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServeChannelHandler());// todo: haven't done

            final Channel serverChannel = server.bind(port).sync().channel();
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

}
