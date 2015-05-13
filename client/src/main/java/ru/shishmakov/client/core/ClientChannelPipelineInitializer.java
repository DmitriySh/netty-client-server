package ru.shishmakov.client.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import ru.shishmakov.client.Client;

/**
 * @author Dmitriy Shishmakov
 * @see Client
 */
public abstract class ClientChannelPipelineInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(final SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline
                .addLast("codec", getHttpClientCodec())
                .addLast("aggregator", getHttpObjectAggregator())
                .addLast("processor", getHttpClientProcessorHandler());
    }

    public abstract HttpClientCodec getHttpClientCodec();

    public abstract HttpObjectAggregator getHttpObjectAggregator();

    public abstract HttpClientProcessorHandler getHttpClientProcessorHandler();
}
