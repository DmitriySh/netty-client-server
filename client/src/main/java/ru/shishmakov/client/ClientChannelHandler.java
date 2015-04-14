package ru.shishmakov.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 *
 *
 * @author Dmitriy Shishmakov
 * @see Client
 */
public class ClientChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast("codec", new HttpClientCodec());
        p.addLast("aggregator", new HttpObjectAggregator(1048576));
        p.addLast("handler", new HttpClientProcessorHandler());
    }
}
