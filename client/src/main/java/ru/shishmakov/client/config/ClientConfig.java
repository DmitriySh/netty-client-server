package ru.shishmakov.client.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import ru.shishmakov.config.CommonConfig;

/**
 * Extension of configuration for Client
 */
@Configuration
@ComponentScan(basePackageClasses = PackageMarker.class)
@Import(CommonConfig.class)
public class ClientConfig {

    @Autowired
    @Qualifier("httpClientProcessorHandler")
    private HttpClientProcessorHandler httpClientProcessorHandler;

    @Bean(name = "processGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup processGroup() {
        return new NioEventLoopGroup();
    }

    @Bean
    @Scope("prototype")
    public HttpClientCodec httpClientCodec() {
        return new HttpClientCodec();
    }

    @Bean
    @Scope("prototype")
    public HttpObjectAggregator httpObjectAggregator() {
        return new HttpObjectAggregator(1048576);
    }

    @Bean(name = "clientChannelPipelineInitializer")
    public ClientChannelPipelineInitializer channelPipelineInitializer() {
        return new ClientChannelPipelineInitializer() {
            @Override
            public HttpClientCodec getHttpClientCodec() {
                return httpClientCodec();
            }

            @Override
            public HttpObjectAggregator getHttpObjectAggregator() {
                return httpObjectAggregator();
            }

            @Override
            public HttpClientProcessorHandler getHttpClientProcessorHandler() {
                return httpClientProcessorHandler;
            }
        };
    }

    @Bean(name = "client")
    public Bootstrap bootstrap() {
        final Bootstrap client = new Bootstrap();
        client.group(processGroup())
                .channel(NioSocketChannel.class)
                .handler(channelPipelineInitializer());
        return client;
    }
}
