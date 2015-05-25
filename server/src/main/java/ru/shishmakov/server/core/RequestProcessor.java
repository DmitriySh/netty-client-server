package ru.shishmakov.server.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shishmakov.server.helper.DatabaseWorker;
import ru.shishmakov.server.helper.ResponseWorker;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;

/**
 * Class parses the HTTP Request which was sent to the server.
 *
 * @author Dmitriy Shishmakov
 * @see ServerChannelPipelineInitializer
 */
public class RequestProcessor extends ChannelRead<FullHttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());
    private static final String HANDLER_URI = "/handler";
    private static final String AUTHOR_URI = "/author";

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
     * @param ctx         instance to interact with {@link ChannelPipeline} and other handlers
     * @param httpRequest the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    public void decode(final ChannelHandlerContext ctx, final FullHttpRequest httpRequest) throws Exception {
        writeLogClientInfo(ctx, httpRequest);
        if (!HttpMethod.POST.equals((httpRequest).getMethod())) {
            final FullHttpResponse response = this.buildResponseHttp405();
            fireResponseChannel(ctx, response);
            return;
        }

        switch (httpRequest.getUri()) {
            case HANDLER_URI: {
                // pushed to the next channel
                ctx.fireChannelRead(new DatabaseWorker(httpRequest));
                break;
            }
            case AUTHOR_URI: {
                final FullHttpResponse response = this.buildAuthorResponseHttp200();
                fireResponseChannel(ctx, response);
                break;
            }
            default: {
                final FullHttpResponse response = this.buildResponseHttp400("uri");
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
    private void fireResponseChannel(final ChannelHandlerContext ctx,
                                     final FullHttpResponse response) {
        final ChannelHandlerContext context = ctx.pipeline().context(DatabaseHandler.class);
        context.fireChannelRead(new ResponseWorker(response));
    }

    private void writeLogClientInfo(final ChannelHandlerContext ctx,
                                    final FullHttpRequest httpRequest) {
        logger.info("// ---------------- start client ");
        logger.debug("Client localAddress: {}", ctx.channel().localAddress());
        logger.debug("Client remoteAddress: {}", ctx.channel().remoteAddress());
        final ByteBuf content = httpRequest.content();
        final String uri = httpRequest.getUri();
        if (content.isReadable()) {
            final String data = String.valueOf(content.toString(StandardCharsets.UTF_8));
            logger.info("Client uri: {} data: {}", uri, data);
        }
    }

}
