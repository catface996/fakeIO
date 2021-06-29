package com.io.netty.rpc.server;

import java.net.InetSocketAddress;

import com.io.netty.rpc.server.socket.ByteToRequestHandler;
import com.io.netty.rpc.server.socket.RequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/29 10:21 上午
 */
@Slf4j
public class RpcServer {

    public static void main(String[] args) throws Exception {

        // 启动监听
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup worker = new NioEventLoopGroup(3);

        ServerBootstrap server = new ServerBootstrap();
        ChannelFuture bind = server
            .group(boss, worker)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    nioSocketChannel.pipeline()
                        .addLast(new ByteToRequestHandler())
                        .addLast(new RequestHandler());
                }
            })
            .bind(new InetSocketAddress("localhost", 9090));
        log.info("server start success...");
        bind.sync().channel().closeFuture().sync();
    }
}
