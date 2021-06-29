package com.io.netty.rpc.client.socket;

import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/29 10:45 上午
 */
@Slf4j
public class SocketPool {

    private static NioEventLoopGroup GROUP = new NioEventLoopGroup(4);

    private static LinkedBlockingQueue<NioSocketChannel> channels = new LinkedBlockingQueue<>();

    public static void initPool() {
        for (int i = 0; i < 3; i++) {
            // 线程组
            // socket连接
            NioSocketChannel socketChannel = new NioSocketChannel();
            // socket连接注册到线程组
            GROUP.register(socketChannel);

            ChannelPipeline pipeline = socketChannel.pipeline();
            // 注册handler到socketChannel上
            pipeline.addLast(new ByteToResponseHandler());
            pipeline.addLast(new ResponseHandler());

            // 连接到服务端
            ChannelFuture connect = socketChannel.connect(new InetSocketAddress("localhost", 9090));
            // 注意连接是异步调用,需要阻塞在当前位置,等待连接成功
            try {
                connect.sync();
            } catch (InterruptedException e) {
                log.error("连接到服务端异常..", e);
            }
            // 添加的队列
            channels.add(socketChannel);
        }
    }

    // TODO 目前简单实现,暂时不获取目标服务所在的服务器和端口列表,固定写死,后续可加入zk

    public static NioSocketChannel getChannel(String serviceName) {
        return channels.poll();
    }
}
