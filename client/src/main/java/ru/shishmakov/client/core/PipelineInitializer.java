package ru.shishmakov.client.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.springframework.beans.factory.annotation.Autowired;
import ru.shishmakov.client.Client;
import ru.shishmakov.client.core.example.WorldClockClientHandler;
import ru.shishmakov.config.AppConfig;
import ru.shishmakov.config.helper.ProtocolType;

/**
 * @author Dmitriy Shishmakov
 * @see Client
 */
public abstract class PipelineInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private AppConfig config;

    @Override
    public void initChannel(final SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        final ProtocolType protocol = config.getProtocolType();
        if (ProtocolType.HTTP == protocol) {
            enableHttpPipeline(pipeline);
        }
        if (ProtocolType.PROTOCOL_BUFFER == protocol) {
            enableProtocolBufferPipeline(pipeline);
        }
    }

    private void enableProtocolBufferPipeline(final ChannelPipeline pipeline) {
        pipeline
                .addLast("frame-decoder", getProtobufVarint32FrameDecoder())
                .addLast("decoder", getProtobufDecoder())
                .addLast("field-prepended", getProtobufVarint32LengthFieldPrepender())
                .addLast("encoder", getProtobufEncoder())
                .addLast("processor", getWorldClockClientHandler());
    }

    private void enableHttpPipeline(final ChannelPipeline pipeline) {
        pipeline
                .addLast("codec", getHttpClientCodec())
                .addLast("aggregator", getHttpObjectAggregator())
                .addLast("processor", getHttpClientProcessorHandler());
    }

    protected abstract WorldClockClientHandler getWorldClockClientHandler();

    public abstract ProtobufEncoder getProtobufEncoder();

    public abstract ProtobufVarint32LengthFieldPrepender getProtobufVarint32LengthFieldPrepender();

    public abstract ProtobufDecoder getProtobufDecoder();

    public abstract ProtobufVarint32FrameDecoder getProtobufVarint32FrameDecoder();

    public abstract HttpClientCodec getHttpClientCodec();

    public abstract HttpObjectAggregator getHttpObjectAggregator();

    public abstract HttpClientProcessorHandler getHttpClientProcessorHandler();
}
