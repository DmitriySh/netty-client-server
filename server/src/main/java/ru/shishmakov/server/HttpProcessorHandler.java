package ru.shishmakov.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
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
public class HttpProcessorHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

    /**
     * @param autoRelease {@code true} if handled message should be released and not transfer to the next pipe;
     *                    {@code false} otherwise
     */
    public HttpProcessorHandler(boolean autoRelease) {
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

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final HttpObject msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            this.handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else {
            logger.warn("Illegal protocol: {}. Expect instance is {}", msg, FullHttpRequest.class.getSimpleName());
            //todo: response
        }
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    private void handleHttpRequest(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        final StringBuilder buffer = new StringBuilder(16);
        // Handle a bad request.
        if (!request.getDecoderResult().isSuccess()) {
            final HttpResponseStatus status = HttpResponseStatus.BAD_REQUEST;
            buffer.append("Failure: ").append(status).append("\r\n");
            fillHttpResponse(ctx, buffer, status);
            return;
        }
        // Handle method of request.
        if (request.getMethod() == HttpMethod.POST) {
            final HttpResponseStatus status = HttpResponseStatus.OK;
            fillHttpResponse(ctx, buffer, status);
            return;
        }else {
            final HttpResponseStatus status = HttpResponseStatus.METHOD_NOT_ALLOWED;
            buffer.append("Failure: ").append(status).append("\r\n");
            fillHttpResponse(ctx, buffer, status);
        }


        fillHttpResponse(ctx, buffer, HttpResponseStatus.OK);
    }

    private void fillHttpResponse(final ChannelHandlerContext ctx, final CharSequence buffer, final HttpResponseStatus status) {
        final FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(buffer.toString(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.write(response);
    }

}
