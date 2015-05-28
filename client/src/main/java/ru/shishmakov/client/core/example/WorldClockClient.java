package ru.shishmakov.client.core.example;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Arrays;
import java.util.List;

/**
 * Sends a list of continent/city pairs to a {@code WorldClockServer} to
 * get the local times of the specified cities.
 */

public final class WorldClockClient {

  static final boolean SSL = System.getProperty("ssl") != null;
  static final String HOST = System.getProperty("host", "127.0.0.1");
  static final int PORT = Integer.parseInt(System.getProperty("port", "8463"));
  static final List<String> CITIES = Arrays.asList(System.getProperty(
      "cities", "Asia/Seoul,Europe/Berlin,America/Los_Angeles").split(","));

  public static void main(String[] args) throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap b = new Bootstrap();
      b.group(group)
          .channel(NioSocketChannel.class)
          .handler(new WorldClockClientInitializer());

      // Make a new connection.
      Channel ch = b.connect(HOST, PORT).sync().channel();

      // Get the handler instance to initiate the request.
      WorldClockClientHandler handler = ch.pipeline().get(WorldClockClientHandler.class);

      // Request and get the response.
      List<String> response = handler.getLocalTimes(CITIES);

      // Close the connection.
      ch.close();

      // Print the response at last but not least.
      for (int i = 0; i < CITIES.size(); i++) {
        System.out.format("%28s: %s%n", CITIES.get(i), response.get(i));
      }
    } finally {
      group.shutdownGracefully();
    }
  }
}
