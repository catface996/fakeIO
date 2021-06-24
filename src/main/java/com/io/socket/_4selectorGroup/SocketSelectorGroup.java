package com.io.socket._4selectorGroup;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * @author by catface
 * @date 2021/6/24 11:12 上午
 */
public class SocketSelectorGroup {

    private static final int PORT = 9090;
    private static final int GROUP_NUM = 3;
    private ServerSocketChannel server;
    private LinkedList<Selector> selectors;

    public static void main(String[] args) {
        SocketSelectorGroup group = new SocketSelectorGroup();
        group.initServer();
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
            // 根据分组数量,创建selector列表
            initSelectors(GROUP_NUM);
            // 单独线程启动accept
            acceptSocketAndJoinSelector();
            // 启动与组数相同的线程读取数据
            read();

            TimeUnit.HOURS.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化selector列表
     *
     * @param groupNum 分组数量
     */
    public void initSelectors(int groupNum) {
        selectors = new LinkedList<>();
        try {
            for (int i = 0; i < groupNum; i++) {
                selectors.addLast(Selector.open());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSocket(SocketChannel socket) {
        try {
            //TODO 当前模式有个弊端,当selector在执行select()时,会阻塞
            Selector selector = selectors.peekFirst();
            socket.register(selector, SelectionKey.OP_READ);
            selectors.addLast(selector);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void acceptSocketAndJoinSelector() {
        new Thread(() -> {
            try {
                for (; ; ) {
                    SocketChannel socket = server.accept();
                    if (socket != null) {
                        socket.configureBlocking(false);
                        addSocket(socket);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void read() {
        for (Selector tempSelector : selectors) {
            new Thread(() -> {
                try {
                    for (; ; ) {
                        if (tempSelector.keys().size() <= 0) {
                            continue;
                        }
                        //TODO  设置超时时间,避免上次调用阻塞,无法检查新加入的socket的状态,这里是个优化点
                        if (tempSelector.select(5) > 0) {
                            Iterator<SelectionKey> keys = tempSelector.selectedKeys().iterator();
                            while (keys.hasNext()) {
                                SelectionKey key = keys.next();
                                keys.remove();
                                handleReadable(key);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
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
