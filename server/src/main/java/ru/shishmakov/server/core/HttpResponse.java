package ru.shishmakov.server.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import ru.shishmakov.server.entity.Profile;
import ru.shishmakov.server.helper.Protocol;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Abstract base class for {@link ChannelInboundHandler} implementations which provide utility methods
 * for preparing HTTP Response.
 *
 * @author Dmitriy Shishmakov
 */
public abstract class HttpResponse extends ChannelInboundHandlerAdapter {

    public FullHttpResponse buildResponseHttp400(final String content) {
        final HttpResponseStatus status = HttpResponseStatus.BAD_REQUEST;
        final Protocol protocol = new Protocol("error");
        protocol.setContent("Ping Pong server can not parse " + content + " of the request");
        protocol.setStatus(String.valueOf(status));
        return buildHttpResponse(protocol.toString(), status);
    }

    public FullHttpResponse buildAuthorResponseHttp200() {
        final HttpResponseStatus status = HttpResponseStatus.OK;
        final Protocol protocol = new Protocol("author");
        protocol.setContent("Dmitriy Shishmakov, https://github.com/DmitriySh");
        protocol.setStatus(String.valueOf(status));
        return buildHttpResponse(protocol.toString(), status);
    }

    public FullHttpResponse buildResponseHttp200(final String pong, final Profile profile) {
        final HttpResponseStatus status = HttpResponseStatus.OK;
        final UUID uuid = profile.getProfileId();
        final Protocol protocol = new Protocol(pong);
        protocol.setContent(pong + " " + profile.getQuantity());
        protocol.setProfileId(uuid.toString());
        protocol.setStatus(String.valueOf(status));
        return buildHttpResponse(protocol.toString(), status);
    }

    public FullHttpResponse buildResponseHttp405() {
        final HttpResponseStatus status = HttpResponseStatus.METHOD_NOT_ALLOWED;
        final Protocol protocol = new Protocol("error");
        protocol.setContent("Ping Pong server failure");
        protocol.setStatus(String.valueOf(status));
        return buildHttpResponse(protocol.toString(), status);
    }

    private static FullHttpResponse buildHttpResponse(final String data,
                                                      final HttpResponseStatus status) {
        final ByteBuf content = Unpooled.copiedBuffer(data, StandardCharsets.UTF_8);
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                content);
        final HttpHeaders headers = response.headers();
        headers.set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        headers.set(HttpHeaders.Names.USER_AGENT, "Netty 4.0");
        headers.set(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
        return response;
    }

}
