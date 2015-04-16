package ru.shishmakov.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shishmakov.config.Config;

import java.lang.invoke.MethodHandles;


/**
 * @author Dmitriy Shishmakov
 */
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

    private final String host;
    private final int port;
    private final String uri;

    public Client(final String host, final int port, final String uri) {
        this.host = host;
        this.port = port;
        this.uri = uri;
    }

    private void run() throws InterruptedException {
        // N threads: depends by cores or value of  system property
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            final Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelHandler());

            final String json = "{\"action\":\"ping\"}";
            final FullHttpRequest request = buildFullHttpRequest(json);

            final Channel clientChannel = client.connect(host, port).sync().channel();
            logger.info("Start the client: {}. Listen on local address: {}; remote address: {}",
                    this.getClass().getSimpleName(), clientChannel.localAddress(), clientChannel.remoteAddress());
            clientChannel.writeAndFlush(request);
            logger.info("Send HTTP request: {} {} {}; content: {}", request.getMethod(), request.getUri(),
                    request.getProtocolVersion(), json);
            clientChannel.closeFuture().sync();
            logger.info("Server to close the connection: {}", Client.class.getSimpleName());
        } finally {
            // shutdown all events
            group.shutdownGracefully();
            // waiting termination of all threads
            group.terminationFuture().sync();
        }
    }

    private FullHttpRequest buildFullHttpRequest(String jsonMessage) {
        final ByteBuf content = Unpooled.copiedBuffer(jsonMessage, CharsetUtil.UTF_8);
        final FullHttpRequest request =
                new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, content);
        final HttpHeaders headers = request.headers();
        headers.set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        headers.set(HttpHeaders.Names.ACCEPT, "application/json");
        headers.set(HttpHeaders.Names.USER_AGENT, "Netty 4.0");
        headers.set(HttpHeaders.Names.COOKIE, "id=client12");
        headers.set(HttpHeaders.Names.HOST, host);
        headers.set(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(content.readableBytes()));
        return request;
    }

    public static void main(final String[] args) throws Exception {
        try {
            final Config config = Config.getInstance();
            final String host = config.getString("connect.host");
            final int port = config.getInt("connect.port");
            final String uri = config.getString("connect.uri");
            new Client(host, port, uri).run();
        } catch (Exception e) {
            logger.error("The server failure: " + e.getMessage(), e);
        }
    }
}
