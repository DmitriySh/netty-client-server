package ru.shishmakov.server.helper;

import com.google.gson.internal.LinkedTreeMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import ru.shishmakov.server.entity.Client;
import ru.shishmakov.server.entity.Protocol;

import java.nio.charset.StandardCharsets;

/**
 * Utility class for preparing HTTP Response
 *
 * @author Dmitriy Shishmakov
 */
public final class ResponseUtil {

  private ResponseUtil() {
  }

  public static FullHttpResponse buildResponseHttp400(final String content) {
    final HttpResponseStatus status = HttpResponseStatus.BAD_REQUEST;
    final Protocol protocol = new Protocol("error");
    protocol.setContent("Ping Pong server can not parse " + content + " of the request");
    protocol.setStatus(String.valueOf(status));
    final String json = protocol.toString();
    return buildHttpResponse(json, status);
  }

  public static FullHttpResponse buildAuthorResponseHttp200() {
    final HttpResponseStatus status = HttpResponseStatus.OK;
    final Protocol protocol = new Protocol("author");
    protocol.setContent("Dmitriy Shishmakov, https://github.com/DmitriySh");
    protocol.setStatus(String.valueOf(status));
    final String json = protocol.toString();
    return buildHttpResponse(json, status);
  }

  public static FullHttpResponse buildResponseHttp200(final String pong, final Client client) {
    final String uuid = (String) ((LinkedTreeMap) client.getSessionid()).get("$uuid");
    final Protocol protocol = new Protocol(pong);
    protocol.setContent(pong + " " + client.getQuantity());
    protocol.setSessionid(uuid);
    protocol.setStatus(HttpResponseStatus.OK.toString());
    return buildHttpResponse(protocol.toString(), HttpResponseStatus.OK);
  }

  public static FullHttpResponse buildResponseHttp405() {
    final HttpResponseStatus status = HttpResponseStatus.METHOD_NOT_ALLOWED;
    final Protocol protocol = new Protocol("error");
    protocol.setContent("Ping Pong server failure");
    protocol.setStatus(String.valueOf(status));
    final String json = protocol.toString();
    return buildHttpResponse(json, status);
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
