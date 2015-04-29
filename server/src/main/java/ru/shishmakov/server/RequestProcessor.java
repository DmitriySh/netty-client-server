package ru.shishmakov.server;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shishmakov.helper.DatabaseWorker;
import ru.shishmakov.helper.ResponseUtil;
import ru.shishmakov.helper.ResponseWorker;

import java.lang.invoke.MethodHandles;

/**
 * Class parses the HTTP Request which was sent to the server.
 *
 * @author Dmitriy Shishmakov
 * @see ChannelPipelineInitializer
 */
public class RequestProcessor extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());
    private static final String HANDLER_URI = "/handler";
    private static final String AUTHOR_URI = "/author";
    /**
     * Converter Java Object -> JSON, JSON -> Java Object
     */
    private final Gson gson = new Gson();

    private static void writeLogClientInfo(final ChannelHandlerContext ctx, final FullHttpRequest httpRequest) {
        logger.info("// ---------------- start client ");
        logger.debug("Client localAddress: {}", ctx.channel().localAddress());
        logger.debug("Client remoteAddress: {}", ctx.channel().remoteAddress());
        final ByteBuf content = httpRequest.content();
        final String uri = httpRequest.getUri();
        if (content.isReadable()) {
            final String data = String.valueOf(content.toString(CharsetUtil.UTF_8));
            logger.info("Client uri: {} data: {}", uri, data);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        logger.error("Fail at handler: " + cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
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
            final FullHttpResponse response = ResponseUtil.buildResponseHttp405(gson, ctx);
            fireResponseChannel(ctx, response);
            return;
        }

        writeLogClientInfo(ctx, httpRequest);
        switch (httpRequest.getUri()) {
            case HANDLER_URI: {
                // pushed to the next channel
                ctx.fireChannelRead(new DatabaseWorker<>(httpRequest));
                break;
            }
            case AUTHOR_URI: {
                final FullHttpResponse response = ResponseUtil.buildAuthorResponseHttp200(gson, ctx);
                fireResponseChannel(ctx, response);
                break;
            }
            default: {
                final FullHttpResponse response = ResponseUtil.buildResponseHttp400(gson, ctx, "uri");
                fireResponseChannel(ctx, response);
                break;
            }
        }
    }

    /**
     * Skip the channel {@link DatabaseHandler} and a push message to the channel {@link ResponseSender}
     *
     * @param ctx      instance to interact with {@link ChannelPipeline} and other handlers
     * @param response instance of {@link FullHttpResponse}
     */
    private void fireResponseChannel(final ChannelHandlerContext ctx, final FullHttpResponse response) {
        final ChannelHandlerContext context = ctx.pipeline().context(DatabaseHandler.class);
        context.fireChannelRead(new ResponseWorker<>(response));
    }

}
