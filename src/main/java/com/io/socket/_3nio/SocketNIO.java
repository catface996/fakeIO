package com.io.socket._3nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * @author by catface
 * @date 2021/6/21 1:11 下午
 */
public class SocketNIO {

    public static void main(String[] args) {
        LinkedList<SocketChannel> sockets = new LinkedList<>();
        try {
            System.out.println("请输入任意字符,进入下一步,开始监听,准备接收请求...");
            System.in.read();
            //服务端开启监听：接受客户端
            ServerSocketChannel ss = ServerSocketChannel.open();
            ss.bind(new InetSocketAddress(9090));
            // 重点  OS  NONBLOCKING!!!
            // 只让接受客户端  不阻塞
            ss.configureBlocking(false);
            for (; ; ) {
                SocketChannel client = ss.accept();
                if (client != null) {
                    // 重点,建立连接后,设置连接的交互模式是否是阻塞的
                    client.configureBlocking(false);
                    sockets.add(client);
                }
                ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                for (SocketChannel socket : sockets) {
                    int num = socket.read(buffer);
                    if (num > 0) {
                        buffer.flip();
                        byte[] aaa = new byte[buffer.limit()];
                        buffer.get(aaa);
                        String b = new String(aaa);
                        System.out.println(socket.socket().getPort() + " : " + b);
                        buffer.clear();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
