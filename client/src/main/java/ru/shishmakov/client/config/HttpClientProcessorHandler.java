package ru.shishmakov.client.config;


import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;

/**
 * @author Dmitriy Shishmakov
 */
@Component
@Qualifier("httpClientProcessorHandler")
@Sharable
public class HttpClientProcessorHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        logger.error("Fail at handler: " + cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final HttpObject msg)
            throws Exception {
        if (!(msg instanceof FullHttpResponse)) {
            return;
        }
        final FullHttpResponse response = (FullHttpResponse) msg;
        final String data = response.content().toString(StandardCharsets.UTF_8);
        logger.info("Receive HTTP response: {} {}; content: {}", response.getProtocolVersion(),
                response.getStatus(), data);
    }
}
