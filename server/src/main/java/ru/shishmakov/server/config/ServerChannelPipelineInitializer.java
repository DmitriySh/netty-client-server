package ru.shishmakov.server.config;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import ru.shishmakov.server.Server;

/**
 * Class manages event flows of pipeline in Netty
 *
 * @author Dmitriy Shishmakov
 * @see Server
 */
public abstract class ServerChannelPipelineInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline
                .addLast("decoder", getHttpRequestDecoder())
                .addLast("aggregator", getHttpObjectAggregator())
                .addLast("encoder", getHttpResponseEncoder())
                .addLast("processor", getRequestProcessor())
                .addAfter(getEventExecutorGroup(), "processor", "database", getDatabaseHandler())
                .addAfter("database", "sender", getResponseSender());
    }

    public abstract ChannelHandler getHttpRequestDecoder();

    public abstract ChannelHandler getHttpObjectAggregator();

    public abstract ChannelHandler getHttpResponseEncoder();

    public abstract RequestProcessor getRequestProcessor() ;

    public abstract EventExecutorGroup getEventExecutorGroup();

    public abstract DatabaseHandler getDatabaseHandler();

    public abstract ResponseSender getResponseSender();
}
