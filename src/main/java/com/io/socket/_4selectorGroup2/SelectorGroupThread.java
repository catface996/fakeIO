package com.io.socket._4selectorGroup2;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/25 11:02 上午
 */
@Slf4j
public class SelectorGroupThread extends Thread {

    int socketNum = 0;
    private final LinkedBlockingDeque<SocketChannel> socketNeedRegister;
    private Selector selector;

    public SelectorGroupThread() {
        socketNeedRegister = new LinkedBlockingDeque<>();
        try {
            selector = Selector.open();
        } catch (Exception e) {
            log.error("创建selector异常.", e);
        }
        this.start();
    }

    public void add(SocketChannel channel) {
        socketNeedRegister.addLast(channel);
        selector.wakeup();
    }

    @Override
    public void run() {
        super.run();
        for (; ; ) {
            try {
                socketNum = selector.select();
                if (socketNum > 0) {
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        handleRead(keys.next());
                        keys.remove();
                    }
                }
                while (!socketNeedRegister.isEmpty()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    socketNeedRegister.pollFirst().register(selector, SelectionKey.OP_READ, buffer);
                }
            } catch (Exception e) {
                log.error("selector.select() error", e);
            }
        }
    }

    private void handleRead(SelectionKey key) {
        ByteBuffer buffer = (ByteBuffer)key.attachment();
        SocketChannel channel = (SocketChannel)key.channel();
        try {
            for (; ; ) {
                buffer.clear();
                int num = channel.read(buffer);
                if (num > 0) {
                    buffer.flip();
                    byte[] data = new byte[buffer.limit()];
                    buffer.get(data, 0, buffer.limit());
                    System.out.print(
                        Thread.currentThread().getName() + " print " + channel.getRemoteAddress() + " " + new String(
                            data));
                } else if (num == 0) {
                    break;
                } else {
                    key.cancel();
                    System.out.println(channel.getRemoteAddress() + ":close..");
                    break;
                }
            }
        } catch (Exception e) {
            log.error("read from socket error.", e);
        }
    }
}
