package com.io.netty.socket;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/28 5:41 下午
 */
@Slf4j
public class SocketServerMulti {

    /**
     * accept和新建立的socket
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // 创建bossGroup和workGroup
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(2);
        NioEventLoopGroup workGroup = new NioEventLoopGroup(3);

        // 创建server,并绑定server到bossGroup
        NioServerSocketChannel server1 = new NioServerSocketChannel();
        bossGroup.register(server1);
        server1.pipeline().addLast(new MyAcceptHandler(workGroup));
        // 绑定端口,启动监听
        ChannelFuture bind1 = server1.bind(new InetSocketAddress("localhost", 8080));

        // 创建server,并绑定server到bossGroup
        NioServerSocketChannel server2 = new NioServerSocketChannel();
        bossGroup.register(server2);
        server2.pipeline().addLast(new MyAcceptHandler(workGroup));
        // 绑定端口,启动监听
        ChannelFuture bind2 = server2.bind(new InetSocketAddress("localhost", 9090));

        // 阻塞,直到server停止
        bind1.sync().channel().closeFuture().sync();
        // 阻塞,直到server停止
        bind2.sync().channel().closeFuture().sync();

        log.info("server stop...");
    }

    static class MyAcceptHandler extends ChannelInboundHandlerAdapter {

        private NioEventLoopGroup workGroup;

        public MyAcceptHandler(NioEventLoopGroup workGroup) {
            this.workGroup = workGroup;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            log.info("server registed success...");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            log.info("receive new socket...");
            SocketChannel client = (SocketChannel)msg;
            // 给client添加handler
            client.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                    log.info("client registed success...");
                }

                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    ByteBuf buf = (ByteBuf)msg;
                    byte[] data = new byte[buf.readableBytes()];
                    buf.readBytes(data);
                    log.info("client receive data:{}", new String(data));
                }
            });
            // 注册client到workGroup
            workGroup.register(client);
        }
    }

}
