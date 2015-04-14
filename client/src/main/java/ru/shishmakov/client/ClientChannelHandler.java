package ru.shishmakov.client;

import io.netty.channel.socket.SocketChannel;

/**
 * Class of endpoint processing HTTP Request was which sent to server.
 *
 * @author Dmitriy Shishmakov
 * @see Client
 */
public class ClientChannelHandler extends ChannelInitializer<SocketChannel> {


    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline p = ch.pipeline();

        p.addLast("log", new LoggingHandler(LogLevel.INFO));
        // Enable HTTPS if necessary.
        /*
        if (ssl) {
            SSLEngine engine =
                SecureChatSslContextFactory.getClientContext().createSSLEngine();
            engine.setUseClientMode(true);

            p.addLast("ssl", new SslHandler(engine));
        }
*/
        p.addLast("codec", new HttpClientCodec());

        // Remove the following line if you don't want automatic content decompression.
        // p.addLast("inflater", new HttpContentDecompressor());

        // Uncomment the following line if you don't want to handle HttpChunks.
        p.addLast("aggregator", new HttpObjectAggregator(1048576));
        p.addLast("handler", new ClientHandler());
    }
}
