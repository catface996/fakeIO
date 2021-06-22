package com.io.socket._4selector;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author by catface
 * @date 2021/6/22 3:51 下午
 */
public class SocketSelector {

    public static final int PORT = 9090;
    private ServerSocketChannel server;
    private Selector selector;

    public static void main(String[] args) {
        SocketSelector server = new SocketSelector();
        server.startServer();
    }

    public void startServer() {

        initServer();

        try {
            while (selector.select() > 0) {
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey socket = keys.next();
                    if (socket.isAcceptable()) {
                        // 处理建立socket连接请求
                        handleAcceptable(socket);
                    } else if (socket.isReadable()) {
                        // 处理socket数据通信请求
                        handleReadable(socket);
                    }
                    keys.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initServer() {
        try {
            System.out.println("输入任意字符,进入下一步,启动server...");
            System.in.read();
            // 创建server
            server = ServerSocketChannel.open();
            // 设置server为非阻塞模式,此设置对accept()系统调用生效
            // 有请求时,返回新创建的文件描述符,否则返回-1,不阻塞当前进程
            server.configureBlocking(false);
            // 绑定监听端口
            server.bind(new InetSocketAddress(PORT));
            // 在支持epoll的linux中,默认创建的额是epoll,可以通过修改jvm参数来指定select或者poll
            selector = Selector.open();
            // 将server对应的fd注册到epoll中
            // 如果是select 或者 poll,不会将文件描述符注册到内核
            server.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("server start success...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理建立连接请求
     *
     * @param selectionKey
     */
    public void handleAcceptable(SelectionKey selectionKey) {
        try {
            ServerSocketChannel ssc = (ServerSocketChannel)selectionKey.channel();
            SocketChannel client = ssc.accept();
            // 设置新建立的socket连接为非阻塞模式,对后续的read()或者recv()生效.
            client.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 注册新建立的连接到epoll中,或者是select和poll
            client.register(selector, SelectionKey.OP_READ, buffer);
            System.out.println("-------------------------------------------");
            System.out.println("新客户端：" + client.getRemoteAddress());
            System.out.println("-------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理读写请求
     *
     * @param selectionKey
     */
    public void handleReadable(SelectionKey selectionKey) {
        SocketChannel socket = (SocketChannel)selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        buffer.clear();
        try {
            while (true) {
                int num = socket.read(buffer);
                if (num > 0) {
                    buffer.flip();
                    byte[] aaa = new byte[buffer.limit()];
                    buffer.get(aaa);
                    String b = new String(aaa);
                    System.out.println(socket.socket().getPort() + " : " + b);
                    socket.write(ByteBuffer.wrap(aaa));
                    buffer.clear();
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
