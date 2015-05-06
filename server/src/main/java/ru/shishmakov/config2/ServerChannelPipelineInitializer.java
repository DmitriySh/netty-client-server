package ru.shishmakov.config2;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
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
@Qualifier("serverChannelPipelineInitializer")
public class ServerChannelPipelineInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private HttpRequestDecoder httpRequestDecoder;
    @Autowired
    private HttpObjectAggregator httpObjectAggregator;
    @Autowired
    private HttpResponseEncoder httpResponseEncoder;
    @Autowired
    @Qualifier("requestProcessor")
    private ChannelInboundHandler requestProcessor;
    @Autowired
    @Qualifier("databaseHandler")
    private ChannelInboundHandler databaseHandler;
    @Autowired
    @Qualifier("responseSender")
    private ChannelInboundHandler responseSender;
    @Autowired
    public EventExecutorGroup eventExecutorGroup;

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

    public void setResponseSender(ChannelInboundHandler responseSender) {
        this.responseSender = responseSender;
    }

    public void setRequestProcessor(ChannelInboundHandler requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    public void setDatabaseHandler(ChannelInboundHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public void setEventExecutorGroup(EventExecutorGroup eventExecutorGroup) {
        this.eventExecutorGroup = eventExecutorGroup;
    }

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline
                .addLast("decoder", httpRequestDecoder)
                .addLast("aggregator", httpObjectAggregator)
                .addLast("encoder", httpResponseEncoder)
                .addLast("processor", requestProcessor)
                .addAfter(eventExecutorGroup, "processor", "database", databaseHandler)
                .addAfter("database", "sender", responseSender);
    }
}
