package ru.shishmakov.server;

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

    /**
     * @param autoRelease {@code true} if handled message should be released and not transfer to the next pipe;
     *                    {@code false} otherwise
     */
    public HttpServerProcessorHandler(boolean autoRelease) {
        super(autoRelease);
        //todo: if false -> ReferenceCountUtil.retain(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Fail at handler: " + cause.getMessage(), cause);
        ctx.close();
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
        final StringBuilder buffer = new StringBuilder(16);
        if (msg instanceof FullHttpRequest) {
            this.handleHttpRequest(ctx, (FullHttpRequest) msg, buffer);
        } else {
            logger.warn("Illegal protocol: {}. Expect instance is {}", msg, FullHttpRequest.class.getSimpleName());
            final HttpResponseStatus status = HttpResponseStatus.BAD_REQUEST;
            buffer.append("Failure: ").append(status).append("\r\n");
            fillHttpResponse(ctx, buffer, status);
        }
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    private void handleHttpRequest(final ChannelHandlerContext ctx, final FullHttpRequest request,
                                   final StringBuilder buffer) {
        // Handle a bad request.
        if (!request.getDecoderResult().isSuccess()) {
            final HttpResponseStatus status = HttpResponseStatus.BAD_REQUEST;
            buffer.append("Failure: ").append(status).append("\r\n");
            fillHttpResponse(ctx, buffer, status);
            return;
        }
        // Handle method of request.
        if (request.getMethod() == HttpMethod.POST) {
            processPost(ctx, request, buffer);
        } else {
            final HttpResponseStatus status = HttpResponseStatus.METHOD_NOT_ALLOWED;
            buffer.append("Failure: ").append(status).append("\r\n");
            fillHttpResponse(ctx, buffer, status);
        }
    }

    private void processPost(final ChannelHandlerContext ctx, final FullHttpRequest request, final StringBuilder buffer) {
        final String uri = request.getUri();
        final String[] chunks = uri.split("/");

        final HttpResponseStatus status = HttpResponseStatus.OK;
        fillHttpResponse(ctx, buffer, status);
    }

    private void fillHttpResponse(final ChannelHandlerContext ctx, final CharSequence buffer, final HttpResponseStatus status) {
        final FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(buffer.toString(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.write(response);
    }

}
