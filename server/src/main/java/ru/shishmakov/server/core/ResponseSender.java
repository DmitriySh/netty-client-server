package ru.shishmakov.server.core;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shishmakov.server.helper.ResponseWorker;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;

/**
 * Class sends the HTTP Response which was prepared the previous channels.
 * The last inbound channel of {@link ChannelPipeline}.
 *
 * @author Dmitriy Shishmakov
 * @see HttpPipeline
 */
public class ResponseSender extends ChannelRead<ResponseWorker> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

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
     * Write the total HttpResponse from previous InboundHandlers to OutboundHandlers.
     *
     * @param ctx    instance to interact with {@link ChannelPipeline} and other handlers
     * @param worker the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    public void decode(final ChannelHandlerContext ctx, final ResponseWorker worker) throws Exception {
        final FullHttpResponse response = worker.getWorker();
        logger.debug("Sent the data:{}", response.content().toString(StandardCharsets.UTF_8));
        ctx.write(response);
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
