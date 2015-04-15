package ru.shishmakov.server;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shishmakov.entity.Protocol;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Set;

/**
 * Class parses the HTTP Request which was sent to server.
 *
 * @author Dmitriy Shishmakov
 * @see ServerChannelHandler
 */
public class HttpServerProcessorHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());
    private static final String HANDLER_URI = "/handler";
    private static final String AUTHOR_URI = "/author";

    private final StringBuilder buffer = new StringBuilder(128);
    private Set<Cookie> cookies;

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        logger.error("Fail at handler: " + cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        logger.info("// ---------------- end client ");
        ctx.flush();
    }

    /**
     * Main method processes each incoming message
     *
     * @param ctx instance to interact with {@link ChannelPipeline} and other handlers
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final HttpObject msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            this.buffer.setLength(0);
            this.cookies = encodeCookie((FullHttpRequest) msg);
            logger.info("client localAddress: {}", ctx.channel().localAddress());
            logger.info("client remoteAddress: {}", ctx.channel().remoteAddress());
            for (Cookie cookie : cookies) {
                logger.info("client cookie: {}", cookie);
            }
            handleHttpRequest(ctx, (FullHttpRequest) msg, buffer);
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * Handle the HttpRequest from client. Build a new HttpResponse and send to client.
     *
     * @param ctx     instance to interact with {@link ChannelPipeline} and other handlers
     * @param request instance of {@link FullHttpRequest}
     * @param buffer  container of string data for the request body
     */
    private void handleHttpRequest(final ChannelHandlerContext ctx, final FullHttpRequest request,
                                   final StringBuilder buffer) {
        if (HttpMethod.POST.equals(request.getMethod())) {
            processPost(ctx, request, buffer);
        } else {
            buildResponseHttp405(ctx);
        }
    }

    private void processPost(final ChannelHandlerContext ctx, final FullHttpRequest request, final StringBuilder buffer) {
        ByteBuf content = request.content();
        final String uri = request.getUri();
        if (content.isReadable()) {
            logger.info("client uri: {} data: {}", uri, content.toString(CharsetUtil.UTF_8));
        }
        switch (uri) {
            case HANDLER_URI: {
                buildResponseHttp200(ctx, request);
                break;
            }
            case AUTHOR_URI: {
                buildAuthorResponseHttp200(ctx);
                break;
            }
            default: {
                buildResponseHttp400(ctx, "URI");
                break;
            }
        }
    }

    private void buildResponseHttp200(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        final String data = request.content().toString(CharsetUtil.UTF_8);
        final Protocol protocol = new Gson().fromJson(data, Protocol.class);
        if ("ping".equalsIgnoreCase(protocol.getAction())) {
            final Protocol temp = new Protocol("pong");
            temp.setContent("pong N");
            final String json = new Gson().toJson(temp);
            buffer.append(json).append("\r\n");
            final HttpResponseStatus status = HttpResponseStatus.OK;
            fillHttpResponse(ctx, buffer.toString(), status);
        } else {
            buildResponseHttp400(ctx, "protocol");
        }
    }

    private void buildAuthorResponseHttp200(final ChannelHandlerContext ctx) {
        final HttpResponseStatus status = HttpResponseStatus.OK;
        final Protocol protocol = new Protocol("author");
        protocol.setContent("Dmitriy Shishmakov, https://github.com/DmitriySh");
        protocol.setStatus(String.valueOf(status));
        final String json = new Gson().toJson(protocol);
        buffer.append(json).append("\r\n");
        fillHttpResponse(ctx, buffer.toString(), status);
    }

    private void buildResponseHttp400(final ChannelHandlerContext ctx, final String content) {
        final HttpResponseStatus status = HttpResponseStatus.BAD_REQUEST;
        final Protocol protocol = new Protocol("error");
        protocol.setContent("Ping Pong server can not parse " + content + " of the request");
        protocol.setStatus(String.valueOf(status));
        final String json = new Gson().toJson(protocol);
        buffer.append(json).append("\r\n");
        fillHttpResponse(ctx, buffer.toString(), status);
    }

    private void buildResponseHttp405(final ChannelHandlerContext ctx) {
        final HttpResponseStatus status = HttpResponseStatus.METHOD_NOT_ALLOWED;
        final Protocol protocol = new Protocol("error");
        protocol.setContent("Ping Pong server failure");
        protocol.setStatus(String.valueOf(status));
        final String json = new Gson().toJson(protocol);
        buffer.append(json).append("\r\n");
        fillHttpResponse(ctx, buffer.toString(), status);
    }

    private void fillHttpResponse(final ChannelHandlerContext ctx, final String data, final HttpResponseStatus status) {
        final ByteBuf content = Unpooled.copiedBuffer(data, CharsetUtil.UTF_8);
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        final HttpHeaders headers = response.headers();
        headers.set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        headers.set(HttpHeaders.Names.USER_AGENT, "Netty 4.0");
        headers.set(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
        ctx.write(response);
    }

    private Set<Cookie> encodeCookie(final FullHttpRequest request) {
        String cookieHeader = request.headers().get(HttpHeaders.Names.COOKIE);
        if (cookieHeader == null) {
            return Collections.emptySet();
        }
        return CookieDecoder.decode(cookieHeader);
    }

}
