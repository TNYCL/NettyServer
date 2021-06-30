package com.netty.util;

import io.netty.buffer.ByteBuf;

public class UtilByte {

    public static final byte CARRIAGE_RETURN_BYTE = 0X0D;

    public static byte[] getByteArray(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        int readerIndex = byteBuf.readerIndex();
        byteBuf.getBytes(readerIndex, bytes);
        return bytes;
    }
}
