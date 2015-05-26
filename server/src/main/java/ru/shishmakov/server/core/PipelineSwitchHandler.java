package ru.shishmakov.server.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * Handler builds the current pipeline dynamically. It is depends on current protocol message.
 */
public abstract class PipelineSwitchHandler extends ByteToMessageDecoder {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles
      .lookup().lookupClass());

  @Override
  protected void decode(final ChannelHandlerContext ctx, final ByteBuf message,
      final List<Object> ignore) throws Exception {
    // Will use the first four bytes to detect a protocol.
    if (message.readableBytes() < 4) {
      return;
    }

    final short firstByte = message.getUnsignedByte(message.readerIndex());
    final short secondByte = message.getUnsignedByte(message.readerIndex() + 1);
    final short thirdByte = message.getUnsignedByte(message.readerIndex() + 2);
    final short fourthByte = message.getUnsignedByte(message.readerIndex() + 3);

    if (isHttp(firstByte, secondByte, thirdByte, fourthByte)) {
      logger.debug("HTTP protocol pipeline is enabled");
      enableHttpPipeline(ctx.pipeline());
      return;
    }
    if (firstByte == 'P' && secondByte == 'R' && thirdByte == 'B' & fourthByte == 'F') {
      logger.debug("Protocol Buffer pipeline is enabled");
      enableProtocolBufferPipeline(ctx.pipeline());
      return;
    }
    logger.debug("Unknown protocol: discard everything and close the connection");
    message.clear();
    ctx.close();
  }

  private static boolean isHttp(final short firstByte, final short secondByte, final short thirdByte, final short fourthByte) {
    return
        firstByte == 'G' && secondByte == 'E' && thirdByte == 'T' || // GET
            firstByte == 'P' && secondByte == 'O' && thirdByte == 'S' && fourthByte == 'T' || // POST
            firstByte == 'P' && secondByte == 'U' && thirdByte == 'T' || // PUT
            firstByte == 'H' && secondByte == 'E' && thirdByte == 'A' && fourthByte == 'D' || // HEAD
            firstByte == 'O' && secondByte == 'P' && thirdByte == 'T' && fourthByte == 'I' || // OPTIONS
            firstByte == 'P' && secondByte == 'A' && thirdByte == 'T' && fourthByte == 'C' || // PATCH
            firstByte == 'D' && secondByte == 'E' && thirdByte == 'L' && fourthByte == 'E' || // DELETE
            firstByte == 'T' && secondByte == 'R' && thirdByte == 'A' && fourthByte == 'C' || // TRACE
            firstByte == 'C' && secondByte == 'O' && thirdByte == 'N' && fourthByte == 'N';   // CONNECT
  }

  protected void enableProtocolBufferPipeline(final ChannelPipeline pipeline){
    // todo: should be done
    System.out.println("!!! Not yet ready !!!");
  }

  public void enableHttpPipeline(final ChannelPipeline pipeline){
    pipeline
        .addLast("decoder", getHttpRequestDecoder())
        .addLast("aggregator", getHttpObjectAggregator())
        .addLast("encoder", getHttpResponseEncoder())
        .addLast("processor", getRequestProcessor())
        .addAfter(getEventExecutorGroup(), "processor", "database", getDatabaseHandler())
        .addAfter("database", "sender", getResponseSender());
    pipeline.remove(this);
  }

  public abstract HttpRequestDecoder getHttpRequestDecoder();

  public abstract HttpObjectAggregator getHttpObjectAggregator();

  public abstract HttpResponseEncoder getHttpResponseEncoder();

  public abstract RequestProcessor getRequestProcessor();

  public abstract EventExecutorGroup getEventExecutorGroup();

  public abstract DatabaseHandler getDatabaseHandler();

  public abstract ResponseSender getResponseSender();

}
