package ru.shishmakov.server.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shishmakov.server.core.example.WorldClockServerHandler;

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

//        if (isHttp(firstByte, secondByte, thirdByte, fourthByte)) {
        if (false) {
            logger.debug("HTTP protocol pipeline is enabled");
            enableHttpPipeline(ctx.pipeline());
            return;
        }
        if (true) {
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

    protected void enableProtocolBufferPipeline(final ChannelPipeline pipeline) {
        pipeline.
                addLast("frame-decoder", getProtobufVarint32FrameDecoder())
                .addLast("decoder", getProtobufDecoder())
                .addLast("field-prepended", getProtobufVarint32LengthFieldPrepender())
                .addLast("encoder", getProtobufEncoder())
                .addLast("processor", getWorldClockServerHandler());
        pipeline.remove(this);
    }

    public void enableHttpPipeline(final ChannelPipeline pipeline) {
        pipeline
                .addLast("decoder", getHttpRequestDecoder())
                .addLast("aggregator", getHttpObjectAggregator())
                .addLast("encoder", getHttpResponseEncoder())
                .addLast("processor", getRequestProcessor())
                .addAfter(getEventExecutorGroup(), "processor", "database", getDatabaseHandler())
                .addAfter("database", "sender", getResponseSender());
        pipeline.remove(this);
    }

    public abstract WorldClockServerHandler getWorldClockServerHandler();

    public abstract ProtobufEncoder getProtobufEncoder();

    public abstract ProtobufVarint32LengthFieldPrepender getProtobufVarint32LengthFieldPrepender();

    public abstract ProtobufDecoder getProtobufDecoder();

    public abstract ProtobufVarint32FrameDecoder getProtobufVarint32FrameDecoder();

    public abstract HttpRequestDecoder getHttpRequestDecoder();

    public abstract HttpObjectAggregator getHttpObjectAggregator();

    public abstract HttpResponseEncoder getHttpResponseEncoder();

    public abstract RequestProcessor getRequestProcessor();

    public abstract EventExecutorGroup getEventExecutorGroup();

    public abstract DatabaseHandler getDatabaseHandler();

    public abstract ResponseSender getResponseSender();

}
