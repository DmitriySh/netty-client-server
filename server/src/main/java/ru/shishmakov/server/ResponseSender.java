package ru.shishmakov.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shishmakov.helper.ResponseWorker;

import java.lang.invoke.MethodHandles;

/**
 * Class sends the HTTP Response which was prepared the previous channels.
 * The last inbound channel of {@link ChannelPipeline}.
 *
 * @author Dmitriy Shishmakov
 * @see ChannelPipelineInitializer
 */
public class ResponseSender extends ChannelInboundHandlerAdapter {

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

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (!(msg instanceof ResponseWorker)) {
            return;
        }
        @SuppressWarnings("unchecked")
        final ResponseWorker<FullHttpResponse> worker = (ResponseWorker<FullHttpResponse>) msg;
        final FullHttpResponse response = worker.getWorker();
        ctx.write(response);
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        logger.debug("Sent the data:{}", response.content().toString(CharsetUtil.UTF_8));
    }
}
