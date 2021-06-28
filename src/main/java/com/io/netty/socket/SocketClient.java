package com.io.netty.socket;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/28 3:18 下午
 */
@Slf4j
public class SocketClient {

    public static void main(String[] args) throws Exception {
        // 线程组
        NioEventLoopGroup group = new NioEventLoopGroup(4);
        // socket连接
        NioSocketChannel socketChannel = new NioSocketChannel();
        // socket连接注册到线程组
        group.register(socketChannel);

        ChannelPipeline pipeline = socketChannel.pipeline();
        // 注册handler到socketChannel上
        pipeline.addLast(new MyInHandler());

        // 连接到服务端
        ChannelFuture connect = socketChannel.connect(new InetSocketAddress("localhost", 9090));
        // 注意连接是异步调用,需要阻塞在当前位置,等待连接成功
        ChannelFuture connectSync = connect.sync();
        log.info("成功连接到server...");
        ByteBuf buf = Unpooled.copiedBuffer("hello world!".getBytes(StandardCharsets.UTF_8));
        // 注意写入是异步调用,需要阻塞在当前位置,等待写入完成
        ChannelFuture sendSync = socketChannel.writeAndFlush(buf);
        sendSync.sync();

        // 等待连接关闭
        connectSync.channel().closeFuture().sync();
        System.out.println("socket close,client over...");

    }

    static class MyInHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf)msg;
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            log.info("read data from socket:{}", new String(data));
        }
    }

}
