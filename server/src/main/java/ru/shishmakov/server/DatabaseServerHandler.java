package ru.shishmakov.server;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shishmakov.entity.Protocol;
import ru.shishmakov.helper.ResponseUtil;

import java.lang.invoke.MethodHandles;

/**
 * Class process the HTTP Request which was sent to the server.
 * Finds the number of received messages "ping".
 *
 * @author Dmitriy Shishmakov
 * @see ServerChannelHandler
 */
public class DatabaseServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles
            .lookup().lookupClass());
    private static final String PING = "ping";
    private static final String PONG = "pong";

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

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            final Protocol protocol = buildFromJson((FullHttpRequest) msg);
            if (PING.equalsIgnoreCase(protocol.getAction())) {
//            final long quantity = findPongQuantity(request);
                final long quantity = 0;
                ResponseUtil.buildResponseHttp200(gson, ctx, PONG, "pong " + quantity);
            } else {
                ResponseUtil.buildResponseHttp400(gson, ctx, "protocol");
            }
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    /*private long findPongQuantity(final FullHttpRequest request) {
        final Set<Cookie> cookies = ResponseUtil.getCookie(request);
        if (cookies.isEmpty()) {
            return 1;
        }
        final StringBuilder builder = new StringBuilder(cookies.size() * 7);
        for (Cookie cookie : cookies) {
            builder.append(cookie.getName()
        }
        final DBCollection collection = Database.getDBCollection();
        new BasicDBObject("coockie_hash": );
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
        collection.findAndModify(, , sortQuery, remove, updateQuery, returnNew, upsert);

        return 0;
    }*/

    private Protocol buildFromJson(final FullHttpRequest request) {
        try {
            final String data = request.content().toString(CharsetUtil.UTF_8);
            return gson.fromJson(data, Protocol.class);
        } catch (Exception e) {
            //can't parse: temp solution
            return new Protocol(null);
        }
    }


}
