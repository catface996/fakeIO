package com.io.socket._4selectorGroup2;

import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author by catface
 * @date 2021/6/25 11:30 上午
 */
public class WorkGroup {

    SelectorGroupThread[] threads;
    private final AtomicInteger socketId = new AtomicInteger(-1);

    public WorkGroup(int num) {
        if (num <= 0) {
            throw new RuntimeException("线程数需大于0");
        }
        threads = new SelectorGroupThread[num];
        for (int i = 0; i < num; i++) {
            threads[i] = new SelectorGroupThread();
        }
    }

    public void register(SocketChannel channel) {
        int index = socketId.incrementAndGet() % threads.length;
        threads[index].add(channel);
    }
}
