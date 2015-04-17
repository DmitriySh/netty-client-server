package ru.shishmakov.server;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shishmakov.helper.ResponseUtil;

import java.lang.invoke.MethodHandles;
import java.util.Set;

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
     * Main method processes each incoming message
     *
     * @param ctx instance to interact with {@link ChannelPipeline} and other handlers
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            final Set<Cookie> cookies = ResponseUtil.getCookie((FullHttpRequest) msg);
            logger.info("client localAddress: {}", ctx.channel().localAddress());
            logger.info("client remoteAddress: {}", ctx.channel().remoteAddress());
            for (Cookie cookie : cookies) {
                logger.info("client cookie: {}", cookie);
            }
            handleHttpRequest(ctx, (FullHttpRequest) msg);
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            logger.info("// ---------------- end client ");
        }
    }

    /**
     * Handle the HttpRequest from client. Build a new HttpResponse and send to client.
     *
     * @param ctx     instance to interact with {@link ChannelPipeline} and other handlers
     * @param request instance of {@link FullHttpRequest}
     */
    private void handleHttpRequest(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        if (HttpMethod.POST.equals(request.getMethod())) {
            processPost(ctx, request);
        } else {
            ResponseUtil.buildResponseHttp405(gson, ctx);
        }
    }

    private void processPost(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        ByteBuf content = request.content();
        final String uri = request.getUri();
        if (content.isReadable()) {
            logger.info("client uri: {} data: {}", uri, content.toString(CharsetUtil.UTF_8));
        }
        switch (uri) {
            case HANDLER_URI: {
                // pushed to the next channel
                ctx.fireChannelRead(request);
                break;
            }
            case AUTHOR_URI: {
                ResponseUtil.buildAuthorResponseHttp200(gson, ctx);
                break;
            }
            default: {
                ResponseUtil.buildResponseHttp400(gson, ctx, "URI");
                break;
            }
        }
    }


}
