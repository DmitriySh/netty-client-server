package ru.shishmakov.config;

import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.shishmakov.config.CommonConfig;
import ru.shishmakov.server.DatabaseHandler;
import ru.shishmakov.server.RequestProcessor;
import ru.shishmakov.server.ResponseSender;

@Configuration
@ComponentScan(basePackageClasses = PackageMarker.class)
@Import(CommonConfig.class)
public class ServerConfig {

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
}
