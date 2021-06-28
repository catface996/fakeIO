package com.io.netty.socket;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/28 5:19 下午
 */
@Slf4j
public class SocketServer {

    /**
     * accept和新建立的socket的read在同一个group中完成
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // 创建EventGroup并将server注册到group中
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        NioServerSocketChannel server = new NioServerSocketChannel();
        group.register(server);

        // 为server添加Handler
        ChannelPipeline pipeline = server.pipeline();
        pipeline.addLast(new MyAcceptHandler(group, new ChannelInit()));

        ChannelFuture bind = server.bind(new InetSocketAddress("localhost", 9090));
        bind.sync().channel().closeFuture().sync();

    }

    static class MyAcceptHandler extends ChannelInboundHandlerAdapter {
        NioEventLoopGroup group;
        ChannelHandler handler;

        public MyAcceptHandler(NioEventLoopGroup group, ChannelHandler handler) {
            this.group = group;
            this.handler = handler;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            log.info("server registed...");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 获得accept到的socket连接,并绑定后续处理的handler
            SocketChannel client = (SocketChannel)msg;
            ChannelPipeline pipeline = client.pipeline();
            pipeline.addLast(handler);
            // 注册新建的socket连接到group中
            group.register(client);
        }
    }

    @ChannelHandler.Sharable
    static class ChannelInit extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            log.info("socket 已注册...");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf)msg;
            byte[] data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            log.info("receive data:{}", new String(data));
        }
    }

}
