package ru.shishmakov.server.core;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.*;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.shishmakov.config.AppConfig;
import ru.shishmakov.config.CommonConfig;
import ru.shishmakov.config.example.WorldClockProtocol;
import ru.shishmakov.server.core.example.WorldClockServerHandler;
import ru.shishmakov.server.dao.PackageMarkerRepository;
import ru.shishmakov.server.entity.PackageMarkerDocument;
import ru.shishmakov.server.service.PackageMarkerService;

import java.net.UnknownHostException;

/**
 * Extension of configuration for Server
 *
 * @author Dmitriy Shishmakov
 */
@Configuration
@Import(CommonConfig.class)
@ComponentScan(basePackageClasses =
        {PackageMarkerService.class, PackageMarkerRepository.class, PackageMarkerCore.class})
public class ServerConfig extends AbstractMongoConfiguration {

    @Autowired
    private AppConfig config;

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ResponseSender responseSender() {
        return new ResponseSender();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public RequestProcessor requestProcessor() {
        return new RequestProcessor();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public DatabaseHandler databaseHandler() {
        return new DatabaseHandler();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public HttpRequestDecoder httpRequestDecoder() {
        return new HttpRequestDecoder();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public HttpObjectAggregator httpObjectAggregator() {
        return new HttpObjectAggregator(1048576);
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public HttpResponseEncoder httpResponseEncoder() {
        return new HttpResponseEncoder();
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
    public WorldClockServerHandler worldClockServerHandler() {
        return new WorldClockServerHandler();
    }

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public PipelineSwitchHandler pipelineSwitchHandler() {
        return new PipelineSwitchHandler() {

            @Override
            public WorldClockServerHandler getWorldClockServerHandler() {
                return worldClockServerHandler();
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
            public HttpRequestDecoder getHttpRequestDecoder() {
                return httpRequestDecoder();
            }

            @Override
            public HttpObjectAggregator getHttpObjectAggregator() {
                return httpObjectAggregator();
            }

            @Override
            public HttpResponseEncoder getHttpResponseEncoder() {
                return httpResponseEncoder();
            }

            @Override
            public EventExecutorGroup getEventExecutorGroup() {
                return eventExecutorGroup();
            }

            @Override
            public RequestProcessor getRequestProcessor() {
                return requestProcessor();
            }

            @Override
            public DatabaseHandler getDatabaseHandler() {
                return databaseHandler();
            }

            @Override
            public ResponseSender getResponseSender() {
                return responseSender();
            }
        };
    }

    @Bean(name = "bootGroup", destroyMethod = "close")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public NioEventLoopGroup bootGroup() {
        return new NioEventLoopGroup(1) {
            public void close() {
                super.shutdownGracefully().awaitUninterruptibly();
            }
        };
    }

    @Bean(name = "processGroup", destroyMethod = "close")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public NioEventLoopGroup processGroup() {
        return new NioEventLoopGroup() {
            public void close() {
                super.shutdownGracefully().awaitUninterruptibly();
            }
        };
    }

    @Bean(name = "eventExecutorGroup", destroyMethod = "close")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public EventExecutorGroup eventExecutorGroup() {
        final int countThreads = Runtime.getRuntime().availableProcessors() * 2;
        return new DefaultEventExecutorGroup(countThreads) {
            public void close() {
                super.shutdownGracefully().awaitUninterruptibly();
            }
        };
    }

    @Bean(name = "serverChannelPipelineInitializer")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public ServerChannelPipelineInitializer channelPipelineInitializer() {
        return new ServerChannelPipelineInitializer() {
            @Override
            protected PipelineSwitchHandler getPipelineSwitchHandler() {
                return pipelineSwitchHandler();
            }
        };
    }

    @Bean(name = "server")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public ServerBootstrap serverBootstrap() {
        final ServerBootstrap server = new ServerBootstrap();
        server.group(bootGroup(), processGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(channelPipelineInitializer());
        return server;
    }

    /**
     * Return the name of the database to connect to.
     */
    @Override
    protected String getDatabaseName() {
        return config.getDatabaseName();
    }

    /**
     * Return the base package to scan classes for mapped {@link Document} annotations.
     */
    @Override
    protected String getMappingBasePackage() {
        final Package mappingPackage = PackageMarkerDocument.class.getPackage();
        return mappingPackage == null ? null : mappingPackage.getName();
    }

    /**
     * The MongoClient class is designed to be <u>thread-safe</u> and shared among threads.
     */
    @Bean(name = "mongo", destroyMethod = "close")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public MongoClient mongo() throws UnknownHostException {
        final String host = config.getDatabaseHost();
        final Integer port = config.getDatabasePort();
        final MongoClient mongoClient = new MongoClient(new ServerAddress(host, port));
        mongoClient.setWriteConcern(WriteConcern.SAFE);
        return mongoClient;
    }

    /**
     * Creates a {@link MongoDbFactory} to be used by the {@link MongoTemplate}.
     */
    @Bean
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Override
    @SuppressWarnings("deprecation")
    public MongoDbFactory mongoDbFactory() throws Exception {
        final String user = config.getDatabaseUser();
        final String password = config.getDatabasePassword();
        final UserCredentials userCredentials = new UserCredentials(user, password);
        return new SimpleMongoDbFactory(mongo(), getDatabaseName(), userCredentials);
    }

    /**
     * The template offers convenience operations to create, update, delete and query
     * for MongoDB documents and provides a mapping between your domain objects and MongoDB documents.
     * MongoTemplate is <u>thread-safe</u> and can be reused across multiple instances.
     */
    @Bean(name = "serverMongoTemplate")
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Override
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
    }
}
