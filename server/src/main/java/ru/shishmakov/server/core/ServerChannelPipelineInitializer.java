package ru.shishmakov.server.core;


import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

@Sharable
public abstract class ServerChannelPipelineInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(final SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("switcher", getPipelineSwitchHandler());
    }

    protected abstract PipelineSwitchHandler getPipelineSwitchHandler();

}
