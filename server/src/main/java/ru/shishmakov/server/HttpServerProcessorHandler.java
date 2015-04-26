package ru.shishmakov.server;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shishmakov.helper.ResponseUtil;

import java.lang.invoke.MethodHandles;

/**
 * Class parses the HTTP Request which was sent to the server.
 *
 * @author Dmitriy Shishmakov
 * @see ServerChannelHandler
 */
public class HttpServerProcessorHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());
    private static final String HANDLER_URI = "/handler";
    private static final String AUTHOR_URI = "/author";
    /**
     * Converter Java Object -> JSON, JSON -> Java Object
     */
    private final Gson gson = new Gson();

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
     * Handle the HttpRequest from client.
     * Push the request to the next channel or might to build a new HttpResponse and send to client.
     *
     * @param ctx instance to interact with {@link ChannelPipeline} and other handlers
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            return;
        }
        final FullHttpRequest httpRequest = (FullHttpRequest) msg;
        if (!HttpMethod.POST.equals((httpRequest).getMethod())) {
            ResponseUtil.writeResponseHttp405(gson, ctx);
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            return;
        }

        writeLogClientInfo(ctx, httpRequest);
        switch (httpRequest.getUri()) {
            case HANDLER_URI:
                // pushed to the next channel
                ctx.fireChannelRead(httpRequest);
                break;
            case AUTHOR_URI:
                ResponseUtil.writeAuthorResponseHttp200(gson, ctx);
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                break;
            default:
                ResponseUtil.writeResponseHttp400(gson, ctx, "uri");
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                break;
        }
    }

    private static void writeLogClientInfo(final ChannelHandlerContext ctx, final FullHttpRequest httpRequest) {
        logger.info("// ---------------- start client ");
        logger.debug("client localAddress: {}", ctx.channel().localAddress());
        logger.debug("client remoteAddress: {}", ctx.channel().remoteAddress());
        final ByteBuf content = httpRequest.content();
        final String uri = httpRequest.getUri();
        if (content.isReadable()) {
            final String data = String.valueOf(content.toString(CharsetUtil.UTF_8));
            logger.info("client uri: {} data: {}", uri, data);
        }
    }


}
