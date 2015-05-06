package ru.shishmakov.client.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.shishmakov.config.AppConfig;
import ru.shishmakov.config.CommonConfig;

/**
 * Extension of configuration for Client
 */
@Configuration
@ComponentScan(basePackageClasses = PackageMarker.class)
@Import(CommonConfig.class)
public class ClientConfig {

    @Autowired
    private AppConfig config;

    @Autowired
    @Qualifier("clientChannelPipelineInitializer")
    private ClientChannelPipelineInitializer channelPipelineInitializer;

    @Bean(name = "processGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup processGroup() {
        return new NioEventLoopGroup();
    }

    @Bean
    public HttpClientCodec httpClientCodec() {
        return new HttpClientCodec();
    }
    @Bean
    public HttpObjectAggregator httpObjectAggregator() {
        return new HttpObjectAggregator(1048576);
    }

    @Bean(name = "client")
    public Bootstrap bootstrap() {
        final Bootstrap client = new Bootstrap();
        client.group(processGroup())
                .channel(NioSocketChannel.class)
                .handler(channelPipelineInitializer);
        return client;
    }
}
