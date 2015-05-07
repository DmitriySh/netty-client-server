package ru.shishmakov.client;


import com.google.gson.JsonObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.shishmakov.client.config.ClientConfig;
import ru.shishmakov.config.AppConfig;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;


/**
 * Netty client for server game <i>"Ping Pong"</i>
 *
 * @author Dmitriy Shishmakov
 */
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

    private final String host;
    private final int port;
    private final String uri;
    private final String sessionId;
    private final Bootstrap client;

    public Client(final AnnotationConfigApplicationContext context) {
        final AppConfig config = context.getBean(AppConfig.class);
        this.client = context.getBean("client", Bootstrap.class);
        this.host = config.getConnectionHost();
        this.port = config.getConnectionPort();
        this.uri = config.getConnectionUri();
        this.sessionId = config.getSessionId();
    }

    private void run() throws InterruptedException {
        logger.warn("Initialise client ...");
        final Channel clientChannel = client.connect(host, port).sync().channel();
        logger.warn("Start the client: {}. Listen on local address: {}; remote address: {}",
                this.getClass().getSimpleName(), clientChannel.localAddress(),
                clientChannel.remoteAddress());
        final String json = buildJson();
        final FullHttpRequest request = buildFullHttpRequest(json);
        clientChannel.writeAndFlush(request);
        logger.info("Send HTTP request: {} {} {}; content: {}", request.getMethod(), request.getUri(),
                request.getProtocolVersion(), json);
        clientChannel.closeFuture().sync();
        logger.warn("Client to close the connection: {}", Client.class.getSimpleName());
    }

    private FullHttpRequest buildFullHttpRequest(final String json) {
        final ByteBuf content = Unpooled.copiedBuffer(json, StandardCharsets.UTF_8);
        final FullHttpRequest request =
                new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, content);
        final HttpHeaders headers = request.headers();
        headers.set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        headers.set(HttpHeaders.Names.ACCEPT, "application/json");
        headers.set(HttpHeaders.Names.USER_AGENT, "Netty 4.0");
        headers.set(HttpHeaders.Names.HOST, host);
        headers.set(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(content.readableBytes()));
        return request;
    }

    private String buildJson() {
        final JsonObject json = new JsonObject();
        json.addProperty("action", "ping");
        json.addProperty("sessionid", sessionId);
        return json.toString();
    }

    public static void main(final String[] args) throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                ClientConfig.class)) {
            context.registerShutdownHook();
            new Client(context).run();
        } catch (Exception e) {
            logger.error("The client failure: " + e.getMessage(), e);
        }
    }
}
