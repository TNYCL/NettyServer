package com.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netty.initializer.ServerChannelInitializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NettyServer {

    public static final String APPLICATION_PROPERTIES = "application.properties";
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private static int port;
    private static int delay;
    private static int maxFrameLength;
    private static int allHeartbeatTimeout;

    public static void main(String[] args) {
        loadProperties(APPLICATION_PROPERTIES);
        new NettyServer().run();
    }

    protected static void loadProperties(String file) {
        InputStream applicationConfigStream = NettyServer.class.getClassLoader().getResourceAsStream(file);
        Properties properties = new Properties();
        try {
            properties.load(applicationConfigStream);
            port = Integer.parseInt(properties.getProperty("port"));
            delay = Integer.parseInt(properties.getProperty("delay"));
            maxFrameLength = Integer.parseInt(properties.getProperty("maxFrameLength"));
            allHeartbeatTimeout = Integer.parseInt(properties.getProperty("allHeartbeatTimeout"));
        } catch (IOException e) {
            logger.error("Error in loading properties", e);
        }
        logger.info("Properties loaded successfully");
    }

    public void run() {
        logger.info("Starting server on port: " + port);
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.localAddress(port)
                    .group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, false)
                    .childHandler(new ServerChannelInitializer(delay, maxFrameLength, allHeartbeatTimeout))
                    .bind()
                    .sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (Exception e) {
            logger.error("Error in server bootstraping", e);
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
