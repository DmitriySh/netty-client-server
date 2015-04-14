package ru.shishmakov.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 *
 *
 * @author Dmitriy Shishmakov
 * @see Server
 */
class ServerChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline
                // inbound
                .addLast("decoder", new HttpRequestDecoder())
                .addLast("aggregator", new HttpObjectAggregator(1048576))
                .addLast("processor", new HttpServerProcessorHandler()/*todo: true or false?*/)
                // outbound
                .addLast("encoder", new HttpResponseEncoder());

    }
}
