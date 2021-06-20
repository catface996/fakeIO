package com.io.socket._2十万连接;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * @author by catface
 * @date 2021/6/20 11:05 上午
 */
public class SocketClient {

    public static void main(String[] args) {
        LinkedList<SocketChannel> clients = new LinkedList<>();
        InetSocketAddress serverAddress = new InetSocketAddress("192.168.162.4", 9090);
        // 端口号的范围为65535,一个short,两字节,16位,2^16=65535
        for (int i = 10000; i < 65000; i++) {

            try {
                SocketChannel client1 = SocketChannel.open();
                SocketChannel client2 = SocketChannel.open();

                // 首先要保证执行当前代码的机器的网卡上绑定了两个ip,分别为 192.168.162.5 , 192.168.162.6
                // 客户端1和客户端2,端口冲10000到65000
                // 客户端1连接到服务端
                client1.bind(new InetSocketAddress("192.168.162.5", i));
                client1.connect(serverAddress);
                clients.add(client1);

                // 客户端2连接到服务端
                client2.bind(new InetSocketAddress("192.168.162.6", i));
                client2.connect(serverAddress);
                clients.add(client1);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        System.out.println("clients " + clients.size());
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
