package ru.shishmakov.server.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.shishmakov.config.AppConfig;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * Sending HTTP Requests from Netty Client to Server.
 *
 * @author Dmitriy Shishmakov
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TestHttpRequest extends TestBase {

    @Autowired
    private AppConfig config;

    @Autowired
    @Qualifier("serverChannelPipelineInitializer")
    private ChannelInitializer channelPipelineInitializer;

    private String host;
    private int port;
    private String uri;

    private static final StringBuilder buffer = new StringBuilder();

    /**
     * Configuration is necessary to all test cases.
     */
    @PostConstruct
    public void init() {
        try {
            this.host = config.getConnectionHost();
            this.port = config.getConnectionPort();
            this.uri = config.getConnectionUri();
        } catch (final Exception e) {
            throw new IllegalStateException("Test failure: " + e.getMessage(), e);
        }
    }

    /**
     * Buffer needs to be empty before each test case.
     */
    @Override
    public void setUp() {
        super.setUp();
        buffer.setLength(0);
    }

    /**
     * Test with illegal type of method into HTTP Request.
     */
    @Test
    public void testHttp405NotAllowedMethod() {
        final NioEventLoopGroup bootGroup = new NioEventLoopGroup();
        final NioEventLoopGroup processGroup = new NioEventLoopGroup();
        final String json = "{\"action\":\"ping\"}";
        final ByteBuf content = Unpooled.copiedBuffer(json, StandardCharsets.UTF_8);

        final ChannelFuture serverChannel = runServer(bootGroup, processGroup);
        runClient(HttpMethod.PUT, content, uri);

        serverChannel.awaitUninterruptibly();
        bootGroup.shutdownGracefully();
        processGroup.shutdownGracefully();

        final String expected = "{\"action\":\"error\",\"content\":\"Ping Pong server failure\",\"status\":\"405 Method Not Allowed\"}";
        final String actual = buffer.toString();
        logger.info("Expected result: {}", expected);
        logger.info("Actual result: {}", actual);
        Assert.assertEquals("Hadn't processed the request: 405 Method Not Allowed", expected, actual);
    }

    /**
     * Test with empty body into HTTP Request.
     */
    @Test
    public void testHttp400EmptyProtocolBody() {
        final NioEventLoopGroup bootGroup = new NioEventLoopGroup();
        final NioEventLoopGroup processGroup = new NioEventLoopGroup();
        final ByteBuf content = Unpooled.buffer(0);

        final ChannelFuture serverChannel = runServer(bootGroup, processGroup);
        runClient(HttpMethod.POST, content, uri);

        serverChannel.awaitUninterruptibly();
        bootGroup.shutdownGracefully();
        processGroup.shutdownGracefully();

        final String expected = "{\"action\":\"error\",\"content\":\"Ping Pong server can not parse protocol of the request\",\"status\":\"400 Bad Request\"}";
        final String actual = buffer.toString();
        logger.info("Expected result: {}", expected);
        logger.info("Actual result: {}", actual);
        Assert.assertEquals("Hadn't processed the request: 400 Bad Request", expected, actual);
    }

    /**
     * Test with defects into body protocol of HTTP Request.
     */
    @Test
    public void testHttp400BadProtocolBody() {
        final NioEventLoopGroup bootGroup = new NioEventLoopGroup();
        final NioEventLoopGroup processGroup = new NioEventLoopGroup();
        final String json = "{\"altron\":\"ping\"}";
        final ByteBuf content = Unpooled.copiedBuffer(json, StandardCharsets.UTF_8);

        final ChannelFuture serverChannel = runServer(bootGroup, processGroup);
        runClient(HttpMethod.POST, content, uri);

        serverChannel.awaitUninterruptibly();
        bootGroup.shutdownGracefully();
        processGroup.shutdownGracefully();

        final String expected = "{\"action\":\"error\",\"content\":\"Ping Pong server can not parse protocol of the request\",\"status\":\"400 Bad Request\"}";
        final String actual = buffer.toString();
        logger.info("Expected result: {}", expected);
        logger.info("Actual result: {}", actual);
        Assert.assertEquals("Hadn't processed the request: 400 Bad Request", expected, actual);
    }

    /**
     * Test with URI into HTTP Request for receive information about author of project.
     */
    @Test
    public void testHttp200AuthorRequest() {
        final NioEventLoopGroup bootGroup = new NioEventLoopGroup(1);
        final NioEventLoopGroup processGroup = new NioEventLoopGroup();
        final String json = "{\"action\":\"ping\"}";
        final ByteBuf content = Unpooled.copiedBuffer(json, StandardCharsets.UTF_8);

        final ChannelFuture serverChannel = runServer(bootGroup, processGroup);
        runClient(HttpMethod.POST, content, "/author");

        serverChannel.awaitUninterruptibly();
        bootGroup.shutdownGracefully();
        processGroup.shutdownGracefully();

        final String expected = "{\"action\":\"author\",\"content\":\"Dmitriy Shishmakov, https://github.com/DmitriySh\",\"status\":\"200 OK\"}";
        final String actual = buffer.toString();
        logger.info("Expected result: {}", expected);
        logger.info("Actual result: {}", actual);
        Assert.assertEquals("Haven't received info about author", expected, actual);
    }

    /**
     * Run test Netty Server with real class ServerChannelHandler
     */
    private ChannelFuture runServer(final NioEventLoopGroup bootGroup,
                                    final NioEventLoopGroup processGroup) {
        final ServerBootstrap server = new ServerBootstrap();
        server.group(bootGroup, processGroup)
                .option(ChannelOption.SO_REUSEADDR, true)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(channelPipelineInitializer);
        return server.bind(host, port);
    }

    /**
     * Run test Netty Client
     */
    private void runClient(final HttpMethod httpMethod, final ByteBuf content, final String uri) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            final Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientHandler());

            final FullHttpRequest request =
                    new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, httpMethod, uri, content);
            final HttpHeaders headers = request.headers();
            headers.set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
            headers.set(HttpHeaders.Names.HOST, host);
            headers.set(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(content.readableBytes()));

            final Channel clientChannel = client.connect(host, port).sync().channel();
            clientChannel.writeAndFlush(request);
            clientChannel.closeFuture().awaitUninterruptibly();
            group.shutdownGracefully();
        } catch (InterruptedException e) {
            Assert.fail("The client failure: " + e.getMessage());
        }
    }

    private static class ClientHandler extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(final SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline
                    .addLast("codec", new HttpClientCodec())
                    .addLast("aggregator", new HttpObjectAggregator(1048576))
                    .addLast("processor", new ClientHttpProcessor());
        }
    }

    private static class ClientHttpProcessor extends SimpleChannelInboundHandler<HttpObject> {
        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, final HttpObject msg)
                throws Exception {
            if (msg instanceof FullHttpResponse) {
                final FullHttpResponse response = (FullHttpResponse) msg;
                final String data = response.content().toString(StandardCharsets.UTF_8);
                buffer.append(data);
            }
        }
    }
}
