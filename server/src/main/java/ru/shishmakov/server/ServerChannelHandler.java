package ru.shishmakov.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * Class manages event flows of pipeline in Netty
 *
 * @author Dmitriy Shishmakov
 * @see Server
 */
class ServerChannelHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        final int countThreads = Runtime.getRuntime().availableProcessors() * 2;
        final EventExecutorGroup workers = new DefaultEventExecutorGroup(1);
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline
                .addLast("decoder", new HttpRequestDecoder())
                .addLast("aggregator", new HttpObjectAggregator(1048576))
                .addLast("encoder", new HttpResponseEncoder())
                .addLast("processor", new HttpServerProcessorHandler())
                // can't receive HTTP Response to client
//                .addLast(workers, "database", new DatabaseServerHandler());
                .addLast("database", new DatabaseServerHandler());

    }
}
