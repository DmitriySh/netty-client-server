package ru.shishmakov.server.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.EventExecutorGroup;
import ru.shishmakov.server.Server;

/**
 * Class manages event flows of pipeline in Netty
 *
 * @author Dmitriy Shishmakov
 * @see Server
 */
public abstract class HttpPipeline {

    protected void fillPipeline(final ChannelHandlerContext ctx) {
        final ChannelPipeline pipeline = ctx.pipeline();
        pipeline.remove(PipelineSwitchHandler.class);
        pipeline
                .addLast("decoder", getHttpRequestDecoder())
                .addLast("aggregator", getHttpObjectAggregator())
                .addLast("encoder", getHttpResponseEncoder())
                .addLast("processor", getRequestProcessor())
                .addAfter(getEventExecutorGroup(), "processor", "database", getDatabaseHandler())
                .addAfter("database", "sender", getResponseSender());
    }

    public abstract HttpRequestDecoder getHttpRequestDecoder();

    public abstract HttpObjectAggregator getHttpObjectAggregator();

    public abstract HttpResponseEncoder getHttpResponseEncoder();

    public abstract RequestProcessor getRequestProcessor();

    public abstract EventExecutorGroup getEventExecutorGroup();

    public abstract DatabaseHandler getDatabaseHandler();

    public abstract ResponseSender getResponseSender();
}
