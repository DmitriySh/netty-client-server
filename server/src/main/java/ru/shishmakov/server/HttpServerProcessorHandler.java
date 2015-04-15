package ru.shishmakov.server;

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

import java.lang.invoke.MethodHandles;

/**
 * Class of endpoint processing HTTP Request was which sent to server.
 *
 * @author Dmitriy Shishmakov
 * @see ServerChannelHandler
 */
public class HttpServerProcessorHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

    final StringBuilder buffer = new StringBuilder(128);

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        logger.error("Fail at handler: " + cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
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
            buffer.setLength(0);
            this.handleHttpRequest(ctx, (FullHttpRequest) msg, buffer);
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handleHttpRequest(final ChannelHandlerContext ctx, final FullHttpRequest request,
                                   final StringBuilder buffer) {
        // Handle method of request.
        if (HttpMethod.POST.equals(request.getMethod())) {
            processPost(ctx, request, buffer);
        } else {
            final HttpResponseStatus status = HttpResponseStatus.METHOD_NOT_ALLOWED;
            buffer.append("Failure: ").append(status).append("\r\n");
            fillHttpResponse(ctx, buffer.toString(), status);
        }
    }

    private void processPost(final ChannelHandlerContext ctx, final FullHttpRequest request, final StringBuilder buffer) {
        final String uri = request.getUri();
        final String[] chunks = uri.split("/");

        logger.info("client localAddress: {}", ctx.channel().localAddress());
        logger.info("client remoteAddress: {}", ctx.channel().remoteAddress());
        ByteBuf content = request.content();
        if (content.isReadable()) {
            logger.info("client data: {}", content.toString(CharsetUtil.UTF_8));
        }
        buffer.append("{\"action\":\"pong\"").append(",").append(" \"content\":\"pong N\"}");
        final HttpResponseStatus status = HttpResponseStatus.OK;
        fillHttpResponse(ctx, buffer.toString(), status);
    }

    private void fillHttpResponse(final ChannelHandlerContext ctx, final String data, final HttpResponseStatus status) {
        final ByteBuf content = Unpooled.copiedBuffer(data, CharsetUtil.UTF_8);
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        final HttpHeaders headers = response.headers();
        headers.set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        headers.set(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
        ctx.write(response);
    }

}
