package ru.shishmakov.config;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.shishmakov.client.Client;
import ru.shishmakov.client.HttpClientProcessorHandler;

/**
 * @author Dmitriy Shishmakov
 * @see Client
 */
@Component
@Qualifier("clientChannelPipelineInitializer")
public class ClientChannelPipelineInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private HttpClientCodec httpClientCodec;
    @Autowired
    private HttpObjectAggregator httpObjectAggregator;
    @Autowired
    private HttpClientProcessorHandler httpClientProcessorHandler;

    @Override
    public void initChannel(final SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline
                .addLast("codec", httpClientCodec)
                .addLast("aggregator", httpObjectAggregator)
                .addLast("processor", httpClientProcessorHandler);
    }
}
