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
public class ChannelPipelineInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        final int countThreads = Runtime.getRuntime().availableProcessors() * 2;
        final EventExecutorGroup workers = new DefaultEventExecutorGroup(countThreads);
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline
                .addLast("decoder", new HttpRequestDecoder())
                .addLast("aggregator", new HttpObjectAggregator(1048576))
                .addLast("encoder", new HttpResponseEncoder())
                .addLast("processor", new RequestProcessor())
                .addAfter(workers, "processor", "database", new DatabaseHandler())
                .addAfter("database", "sender", new ResponseSender());
    }
}
