package ru.shishmakov.config;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.shishmakov.server.Server;

/**
 * Class manages event flows of pipeline in Netty
 *
 * @author Dmitriy Shishmakov
 * @see Server
 */
@Component
@Qualifier("channelPipelineInitializer")
public class ChannelPipelineInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private HttpRequestDecoder httpRequestDecoder;
    @Autowired
    private HttpObjectAggregator httpObjectAggregator;
    @Autowired
    private HttpResponseEncoder httpResponseEncoder;
    @Autowired
    private RequestProcessor requestProcessor;
    @Autowired
    private DatabaseHandler databaseHandler;
    @Autowired
    private ResponseSender responseSender;

    public void setHttpRequestDecoder(final HttpRequestDecoder httpRequestDecoder) {
        this.httpRequestDecoder = httpRequestDecoder;
    }

    public void setHttpObjectAggregator(final HttpObjectAggregator httpObjectAggregator) {
        this.httpObjectAggregator = httpObjectAggregator;
    }

    public void setHttpResponseEncoder(final HttpResponseEncoder httpResponseEncoder) {
        this.httpResponseEncoder = httpResponseEncoder;
    }

    public void setRequestProcessor(final RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    public void setDatabaseHandler(final DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public void setResponseSender(final ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        final int countThreads = Runtime.getRuntime().availableProcessors() * 2;
        final EventExecutorGroup workers = new DefaultEventExecutorGroup(countThreads);
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline
                .addLast("decoder", httpRequestDecoder)
                .addLast("aggregator", httpObjectAggregator)
                .addLast("encoder", httpResponseEncoder)
                .addLast("processor", requestProcessor)
                .addAfter(workers, "processor", "database", databaseHandler)
                .addAfter("database", "sender", responseSender);
    }
}
