package com.io.netty.loop;

import java.util.concurrent.TimeUnit;

import io.netty.channel.nio.NioEventLoopGroup;

/**
 * @author by catface
 * @date 2021/6/28 3:08 下午
 */
public class NioEventLoopDemo {

    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        group.execute(() -> {
            try {
                for (; ; ) {
                    System.out.println(Thread.currentThread().getName() + ":hello one");
                    TimeUnit.SECONDS.sleep(5);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        group.execute(() -> {
            try {
                for (; ; ) {
                    System.out.println(Thread.currentThread().getName() + ":hello two");
                    TimeUnit.SECONDS.sleep(2);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
