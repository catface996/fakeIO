package com.io.socket._3bio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author by catface
 * @date 2021/6/21 11:06 上午
 */
public class SocketBIO {

    public static void main(String[] args) {

        try {
            System.out.println("请输入任意字符,进入下一步,开始监听,准备接收请求...");
            System.in.read();
            //服务端开启监听：接受客户端
            ServerSocketChannel ss = ServerSocketChannel.open();
            ss.bind(new InetSocketAddress(9090));
            // 重点  OS  NONBLOCKING!!!
            // 只让接受客户端  不阻塞
            ss.configureBlocking(true);
            while (true) {
                SocketChannel client = ss.accept();
                if (client == null) {
                    System.out.println("accept未获取请求,未建立新的连接");
                } else {
                    // 重点,建立连接后,设置连接的交互模式是否是阻塞的
                    client.configureBlocking(true);
                    new Thread(() -> {
                        try {
                            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);  //可以在堆里   堆外
                            while (true) {
                                int num = client.read(buffer);
                                if (num > 0) {
                                    buffer.flip();
                                    byte[] aaa = new byte[buffer.limit()];
                                    buffer.get(aaa);
                                    String b = new String(aaa);
                                    System.out.println(client.socket().getPort() + " : " + b);
                                    buffer.clear();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
