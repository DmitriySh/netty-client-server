package ru.shishmakov.helper;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import ru.shishmakov.entity.Protocol;

/**
 * Utility class for preparing HTTP Response
 *
 * @author Dmitriy Shishmakov
 */
public final class ResponseUtil {

    private ResponseUtil() {
    }

    public static FullHttpResponse buildResponseHttp400(final Gson gson, final ChannelHandlerContext ctx, final String content) {
        final HttpResponseStatus status = HttpResponseStatus.BAD_REQUEST;
        final Protocol protocol = new Protocol("error");
        protocol.setContent("Ping Pong server can not parse " + content + " of the request");
        protocol.setStatus(String.valueOf(status));
        final String json = gson.toJson(protocol);
        return buildHttpResponse(json, status);
    }

    public static FullHttpResponse buildAuthorResponseHttp200(final Gson gson, final ChannelHandlerContext ctx) {
        final HttpResponseStatus status = HttpResponseStatus.OK;
        final Protocol protocol = new Protocol("author");
        protocol.setContent("Dmitriy Shishmakov, https://github.com/DmitriySh");
        protocol.setStatus(String.valueOf(status));
        final String json = gson.toJson(protocol);
        return buildHttpResponse(json, status);
    }

    public static FullHttpResponse buildResponseHttp200(final Gson gson, final ChannelHandlerContext ctx,
                                                        final String pong, final String content) {
        final HttpResponseStatus status = HttpResponseStatus.OK;
        final Protocol protocol = new Protocol(pong);
        protocol.setContent(content);
        protocol.setStatus(String.valueOf(status));
        final String json = gson.toJson(protocol);
        return buildHttpResponse(json, status);
    }

    public static FullHttpResponse buildResponseHttp405(final Gson gson, final ChannelHandlerContext ctx) {
        final HttpResponseStatus status = HttpResponseStatus.METHOD_NOT_ALLOWED;
        final Protocol protocol = new Protocol("error");
        protocol.setContent("Ping Pong server failure");
        protocol.setStatus(String.valueOf(status));
        final String json = gson.toJson(protocol);
        return buildHttpResponse(json, status);
    }

    private static FullHttpResponse buildHttpResponse(final String data, final HttpResponseStatus status) {
        final ByteBuf content = Unpooled.copiedBuffer(data, CharsetUtil.UTF_8);
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        final HttpHeaders headers = response.headers();
        headers.set(HttpHeaders.Names.CONTENT_TYPE, "application/json; charset=UTF-8");
        headers.set(HttpHeaders.Names.USER_AGENT, "Netty 4.0");
        headers.set(HttpHeaders.Names.CONTENT_LENGTH, content.readableBytes());
        return response;
    }

}
