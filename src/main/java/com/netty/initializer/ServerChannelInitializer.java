package com.netty.initializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netty.handler.ServerChannelHandler;
import com.netty.handler.ServerHeartbeatHandler;

import static com.netty.util.UtilByte.CARRIAGE_RETURN_BYTE;

import java.util.concurrent.TimeUnit;

public class ServerChannelInitializer extends ChannelInitializer<Channel> {

    private static final Logger logger = LoggerFactory.getLogger(ServerChannelInitializer.class);
    private int delay;
    private int maxFrameLength = 700;
    private int readerHeartbeatTimeout = 0;
    private int writerHeartbeatTimeout = 0;
    private int allHeartbeatTimeout = 60;

    public ServerChannelInitializer(int delay, int readerHeartbeatTimeout, int writerHeartbeatTimeout, int allHeartbeatTimeout) {
        this.delay = delay;
        this.readerHeartbeatTimeout = readerHeartbeatTimeout;
        this.writerHeartbeatTimeout = writerHeartbeatTimeout;
        this.allHeartbeatTimeout = allHeartbeatTimeout;
    }

    public ServerChannelInitializer(int delay, int maxFrameLength, int allHeartbeatTimeout) {
        this.delay = delay;
        this.maxFrameLength = maxFrameLength;
        this.allHeartbeatTimeout = allHeartbeatTimeout;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        logger.info("Creating channel: " + channel.toString());

        byte[] carriage_return_byte_array = {CARRIAGE_RETURN_BYTE};
        ByteBuf carriage_return = Unpooled.copiedBuffer(carriage_return_byte_array);
        DelimiterBasedFrameDecoder decoder = new DelimiterBasedFrameDecoder(maxFrameLength, false, carriage_return);
        channel.pipeline().addFirst(decoder);

        ChannelHandler channelHandler = new ServerChannelHandler(delay);
        channel.pipeline().addLast(channelHandler);

        IdleStateHandler idleStateHandler = new IdleStateHandler(readerHeartbeatTimeout, writerHeartbeatTimeout, allHeartbeatTimeout, TimeUnit.SECONDS);
        channel.pipeline().addLast(idleStateHandler);
        channel.pipeline().addLast(new ServerHeartbeatHandler());
    }
}
