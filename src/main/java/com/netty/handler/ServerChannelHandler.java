package com.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.netty.util.UtilByte.CARRIAGE_RETURN_BYTE;
import static com.netty.util.UtilByte.getByteArray;

import java.util.Arrays;

public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServerChannelHandler.class);

    private int delay;
    String model;

    public ServerChannelHandler(int delay) {
        this.delay = delay;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf input = (ByteBuf) msg;
        byte[] inputArray = getByteArray(input);
        Channel channel = ctx.channel();
        Thread.sleep(delay);
        logger.info("Incoming message: " + Arrays.toString(inputArray));
        byte[] outputArray = getOutput(inputArray);
        logger.info("Outgoing message: " + Arrays.toString(outputArray));
        channel.writeAndFlush(Unpooled.copiedBuffer(outputArray));
    }

    private byte[] getOutput(byte[] inputArray) {
        String color = new String(inputArray);
        logger.info("Input Color: " + color);
        logger.info("Input Length: " + inputArray.length);
        if (color.contains("red")) {
            model = "1";
        } else if (color.contains("blue")) {
            model = "2";
        } else {
            model = "3";
        }
        return ArrayUtils.add(model.getBytes(), CARRIAGE_RETURN_BYTE);
    }
}
