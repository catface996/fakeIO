package com.io.socket._1socket基本流程;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * @author by catface
 * @date 2021/6/21 2:45 下午
 */
public class SocketClient {

    public static void main(String[] args) {
        try {
            InetSocketAddress serverAddress = new InetSocketAddress("localhost", 9090);
            SocketChannel client1 = SocketChannel.open();
            client1.bind(new InetSocketAddress(9999));
            client1.connect(serverAddress);
            client1.configureBlocking(false);
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);

            new Thread(() -> {
                try {
                    for (; ; ) {
                        TimeUnit.SECONDS.sleep(10);
                        int num = client1.read(readBuffer);
                        if (num > 0) {
                            readBuffer.flip();
                            byte[] aaa = new byte[readBuffer.limit()];
                            readBuffer.get(aaa);
                            String b = new String(aaa);
                            System.out.println(client1.socket().getPort() + " : " + b);
                            readBuffer.clear();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();

            while (true) {
                ByteBuffer buffer = ByteBuffer.allocate(16);
                byte[] arr = new byte[1024];
                int length = System.in.read(arr);
                buffer.put(arr, 0, length);
                buffer.flip();
                client1.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
