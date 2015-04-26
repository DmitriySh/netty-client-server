package ru.shishmakov.server;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shishmakov.entity.CookieHash;
import ru.shishmakov.entity.Protocol;
import ru.shishmakov.helper.CookieUtil;
import ru.shishmakov.helper.Database;
import ru.shishmakov.helper.ResponseUtil;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Set;

/**
 * Class process the HTTP Request which was sent to the server.
 * He is seeking the number of received messages in database. Protocol  "ping".
 *
 * @author Dmitriy Shishmakov
 * @see ServerChannelHandler
 */
public class HttpServerDatabaseHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());
    /**
     * Protocol to receive the messages
     */
    private static final String PING = "ping";
    /**
     * Protocol to send the messages
     */
    private static final String PONG = "pong";
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
     * Method to processes a {@code "ping"} message. Part of pipeline works with database.
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
        final FullHttpRequest request = (FullHttpRequest) msg;
        final Protocol protocol = buildFromJson(request);
        if (!PING.equalsIgnoreCase(protocol.getAction())) {
            ResponseUtil.writeResponseHttp400(gson, ctx, "protocol");
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }

        final long quantity = findPongQuantity(request);
        ResponseUtil.writeResponseHttp200(gson, ctx, PONG, PONG + " " + quantity);
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * The main method of the server <i>"Ping Pong"</i>.
     * HTTP cookie is a main opportunity for server to know all clients: new and old.
     * It produces hash code over all cookies (key:value) and this integer value is a key for making a decision.
     * <p>
     * <b>Example of JSON document: </b><br/>
     * {@code {"coockie_hash" : 77737217 , "quantity" : 2}}
     * <p>
     * <b>Example of FindAndModify query: </b> <br/>
     * {@code
     * {query: {"coockie_hash" : 77737217} ,
     * sort: {"coockie_hash" : 1},
     * update: {$inc: {"quantity" : 1}},
     * new: true, upset: true}}
     *
     * @param request instance of {@link FullHttpRequest}
     * @return quantity of requests from current client
     */
    private long findPongQuantity(final FullHttpRequest request) {
        final Set<Cookie> cookies = getCookie(request);
        if (cookies.isEmpty()) {
            return 1;
        }
        int hash = CookieUtil.buildHash(cookies);

        final DBCollection collection = Database.getDBCollection();
        if (collection == null) {
            throw new IllegalArgumentException("The database don't have a link with collection for making the query.");
        }
        // create a finding query
        final BasicDBObject query = new BasicDBObject("coockie_hash", hash);
        // create an ascending query
        final DBObject sortQuery = new BasicDBObject("coockie_hash", 1);
        // create an increment query
        final DBObject updateQuery = new BasicDBObject("$inc", new BasicDBObject("quantity", 1));
        // remove the document specified in the query
        final boolean remove = false;
        // return the modified document rather than the original
        final boolean returnNew = true;
        // create a new document if no document matches the query
        final boolean upsert = true;
        // subset of fields to return
        final DBObject fields = null;
        final DBObject dbObject = collection.findAndModify(query, fields, sortQuery, remove, updateQuery, returnNew, upsert);

        final String json = JSON.serialize(dbObject);
        final CookieHash client = gson.fromJson(json, CookieHash.class);
        return client.getQuantity();
    }

    private Protocol buildFromJson(final FullHttpRequest request) {
        try {
            final String data = request.content().toString(CharsetUtil.UTF_8);
            final Protocol protocol = gson.fromJson(data, Protocol.class);
            return protocol == null ? new Protocol("") : protocol;
        } catch (Exception e) {
            //can't parse: temp solution
            return new Protocol("");
        }
    }

    private static Set<Cookie> getCookie(final FullHttpRequest request) {
        final String cookieHeader = request.headers().get(HttpHeaders.Names.COOKIE);
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return Collections.emptySet();
        }
        return CookieDecoder.decode(cookieHeader);
    }
}
