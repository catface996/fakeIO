package com.io.netty.socket;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.io.netty.socket.SocketClient.MyInHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/28 4:07 下午
 */
@Slf4j
public class SocketClient2 {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture localhostChannel = bootstrap.group(group)
            // 设置channel类型
            .channel(NioSocketChannel.class)
            // 绑定handler
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new MyInHandler());
                }
            })
            // 建立与server的socket连接
            .connect(new InetSocketAddress("localhost", 9090));
        // 连接成功后,获取返回的socket
        Channel channel = localhostChannel.sync().channel();
        // 通过获取的socket发送数据
        ByteBuf buf = Unpooled.copiedBuffer("hello world!".getBytes(StandardCharsets.UTF_8));
        ChannelFuture sendFuture = channel.writeAndFlush(buf);
        // 阻塞当前线程,等待发送完成
        sendFuture.sync();

        // 等待socket连接断开
        channel.closeFuture().sync();

        log.info("socket closed,client over...");

    }

}
