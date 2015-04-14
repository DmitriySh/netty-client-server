package ru.shishmakov.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * @author Dmitriy Shishmakov
 * @see Client
 */
public class ClientChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(final SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline
                // inbound/outbound
                .addLast("codec", new HttpClientCodec())
                // inbound
                .addLast("aggregator", new HttpObjectAggregator(1048576))
                .addLast("processor", new HttpClientProcessorHandler());
    }
}
