package ru.shishmakov.client.core.example;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.shishmakov.config.protocol.WorldClockProtocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

public class WorldClockClientHandler extends
    SimpleChannelInboundHandler<WorldClockProtocol.LocalTimes> {

  private static final Pattern DELIM = Pattern.compile("/");

  private final BlockingQueue<WorldClockProtocol.LocalTimes> answer = new LinkedBlockingQueue<>();

  public WorldClockClientHandler() {
    super(false);
  }

  public List<String> getLocalTimes(Collection<String> cities) {

    WorldClockProtocol.LocalTimes localTimes;
    boolean interrupted = false;
    for (; ; ) {
      try {
        localTimes = answer.take();
        break;
      } catch (InterruptedException ignore) {
        interrupted = true;
      }
    }

    if (interrupted) {
      Thread.currentThread().interrupt();
    }

    List<String> result = new ArrayList<String>();
    for (WorldClockProtocol.LocalTime lt : localTimes.getLocalTimeList()) {
      result.add(
          new Formatter().format(
              "%4d-%02d-%02d %02d:%02d:%02d %s",
              lt.getYear(),
              lt.getMonth(),
              lt.getDayOfMonth(),
              lt.getHour(),
              lt.getMinute(),
              lt.getSecond(),
              lt.getDayOfWeek().name()).toString());
    }

    return result;
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, WorldClockProtocol.LocalTimes times)
      throws Exception {
    answer.add(times);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
