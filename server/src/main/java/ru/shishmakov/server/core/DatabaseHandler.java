package ru.shishmakov.server.core;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.shishmakov.server.entity.Profile;
import ru.shishmakov.server.helper.DatabaseWorker;
import ru.shishmakov.server.helper.Protocol;
import ru.shishmakov.server.helper.ResponseWorker;
import ru.shishmakov.server.service.DbService;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Class processes the HTTP Request which was sent to the server.
 * It is seeking the number of received messages in database. Protocol  "ping".
 *
 * @author Dmitriy Shishmakov
 * @see PipelineSwitchHandler
 */
public class DatabaseHandler extends ChannelRead<DatabaseWorker> {

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

    @Autowired
    @Qualifier("mongoService")
    private DbService<Profile, UUID> service;

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
     * Method to processes a {@code "ping"} message. Part of pipeline works with database.
     *
     * @param ctx    instance to interact with {@link ChannelPipeline} and other handlers
     * @param worker the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    public void decode(final ChannelHandlerContext ctx, final DatabaseWorker worker) throws Exception {
        final FullHttpRequest request = worker.getWorker();
        final Protocol protocol = buildProtocol(request);
        // illegal action command
        if (!PING.equalsIgnoreCase(protocol.getAction())) {
            final FullHttpResponse response = this.buildResponseHttp400("protocol");
            // pushed to the next channel
            ctx.fireChannelRead(new ResponseWorker(response));
            return;
        }
        // known action command
        final Profile profile = findOrCreateProfile(protocol);
        final FullHttpResponse response = this.buildResponseHttp200(PONG, profile);
        // pushed to the next channel
        ctx.fireChannelRead(new ResponseWorker(response));
    }

    /**
     * The main method of the server <i>"Ping Pong"</i>.
     * Profile ID is a main possibility for server to know all clients: new and old.
     * The type is an {@link UUID} receives from client or generates on server side and sent back to client.
     * Java type {@link UUID} is a {@link Binary} type of BSON into Mongo DB.
     * <p>
     * <b>Server JSON document: </b>
     * <pre>
     * {
     *    "_id" : ObjectId("55510463b33e15e132620f4a"),
     *    "profileid" : BinData(3,"Yk2qH7ibk4vQ4XQ31kNCiw=="),
     *    "quantity" : 7
     * }
     * </pre>
     * <p>
     * <b>Example of FindAndModify query: </b>
     * <pre>
     * {
     *    query: {"profileid" : BinData(3,"Yk2qH7ibk4vQ4XQ31kNCiw==")} ,
     *    update: {$inc: {"quantity" : 1}},
     *    new: true,
     *    upset: true
     * }
     * </pre>
     * <p>
     * <b>Client request document:</b>
     * <pre>
     * {
     *    "action" : "ping",
     *    "profileid" : "8b939bb8-1faa-4d62-8b42-43d63774e1d0"
     * }
     * </pre>
     * <p>
     * <b>Server response document:</b>
     * <pre>
     * {
     *    "action" : "pong",
     *    "content" : "pong 7",
     *    "profileid" : "8b939bb8-1faa-4d62-8b42-43d63774e1d0",
     *    "status" : "200 OK"
     * }
     * </pre>
     *
     * @param protocol instance of {@link Protocol}
     * @return quantity of requests from current client
     */
    private Profile findOrCreateProfile(final Protocol protocol) {
        final String profileId = protocol.getProfileId();
        if (profileId == null) {
            final UUID newProfileId = UUID.randomUUID();
            return service.getById(newProfileId);
        }
        return service.getById(UUID.fromString(profileId));
    }

    private Protocol buildProtocol(final FullHttpRequest request) {
        try {
            final String data = request.content().toString(StandardCharsets.UTF_8);
            final Protocol protocol = new Gson().fromJson(data, Protocol.class);
            return protocol == null ? new Protocol(null) : protocol;
        } catch (Exception e) {
            //can't parse: temp solution
            return new Protocol(null);
        }
    }
}
