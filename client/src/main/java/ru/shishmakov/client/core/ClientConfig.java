package ru.shishmakov.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.*;
import ru.shishmakov.client.core.example.WorldClockClientHandler;
import ru.shishmakov.config.CommonConfig;
import ru.shishmakov.config.example.WorldClockProtocol;

/**
 * Extension of configuration for Client
 *
 * @author Dmitriy Shishmakov
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

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ProtobufVarint32FrameDecoder protobufVarint32FrameDecoder() {
        return new ProtobufVarint32FrameDecoder();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ProtobufDecoder protobufDecoder() {
        return new ProtobufDecoder(WorldClockProtocol.Locations.getDefaultInstance());
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ProtobufVarint32LengthFieldPrepender protobufVarint32LengthFieldPrepender() {
        return new ProtobufVarint32LengthFieldPrepender();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ProtobufEncoder protobufEncoder() {
        return new ProtobufEncoder();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public WorldClockClientHandler worldClockClientHandler() {
        return new WorldClockClientHandler();
    }

    @Bean(name = "clientChannelPipelineInitializer")
    public PipelineInitializer channelPipelineInitializer() {
        return new PipelineInitializer() {
            @Override
            protected WorldClockClientHandler getWorldClockClientHandler() {
                return worldClockClientHandler();
            }

            @Override
            public ProtobufEncoder getProtobufEncoder() {
                return protobufEncoder();
            }

            @Override
            public ProtobufVarint32LengthFieldPrepender getProtobufVarint32LengthFieldPrepender() {
                return protobufVarint32LengthFieldPrepender();
            }

            @Override
            public ProtobufDecoder getProtobufDecoder() {
                return protobufDecoder();
            }

            @Override
            public ProtobufVarint32FrameDecoder getProtobufVarint32FrameDecoder() {
                return protobufVarint32FrameDecoder();
            }

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
