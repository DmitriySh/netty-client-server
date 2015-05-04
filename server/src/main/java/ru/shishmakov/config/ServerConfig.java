package ru.shishmakov.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.net.UnknownHostException;

@Configuration
@ComponentScan(basePackageClasses = PackageMarker.class)
@Import(CommonConfig.class)
public class ServerConfig {

    @Autowired
    @Qualifier("channelPipelineInitializer")
    private ChannelInitializer channelPipelineInitializer;

    @Autowired
    private AppConfig config;

    @Bean
    public HttpRequestDecoder httpRequestDecoder() {
        return new HttpRequestDecoder();
    }

    @Bean
    public HttpObjectAggregator httpObjectAggregator() {
        return new HttpObjectAggregator(1048576);
    }

    @Bean
    public HttpResponseEncoder httpResponseEncoder() {
        return new HttpResponseEncoder();
    }

    @Bean
    public RequestProcessor requestProcessor() {
        return new RequestProcessor();
    }

    @Bean
    public DatabaseHandler databaseHandler() {
        return new DatabaseHandler();
    }

    @Bean
    public ResponseSender responseSender() {
        return new ResponseSender();
    }

    @Bean(name = "bootGroup")
    public NioEventLoopGroup bootGroup() {
        return new NioEventLoopGroup(1);
    }

    @Bean(name = "processGroup")
    public NioEventLoopGroup processGroup() {
        return new NioEventLoopGroup();
    }

    @Bean(name = "server")
    public ServerBootstrap serverBootstrap() {
        final ServerBootstrap server = new ServerBootstrap();
        server.group(bootGroup(), processGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(channelPipelineInitializer);
        return server;
    }

    @Bean(name = "mongo", destroyMethod = "close")
    public Mongo mongo() throws UnknownHostException {
        final String host = config.getDatabaseHost();
        final Integer port = config.getDatabasePort();
        return new MongoClient(new ServerAddress(host, port));
    }

    @Bean
    public MongoTemplate mongoDbFactory() throws Exception {
        final String user = config.getDatabaseUser();
        final String password = config.getDatabasePassword();
        final String databaseName = config.getDatabaseName();
        final UserCredentials userCredentials = new UserCredentials(user, password);
        return new MongoTemplate(mongo(), databaseName, userCredentials);
    }
}
