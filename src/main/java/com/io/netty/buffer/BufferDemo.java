package com.io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

/**
 * @author by catface
 * @date 2021/6/28 2:55 下午
 */
public class BufferDemo {

    public static void main(String[] args) {

        ByteBuf buf = PooledByteBufAllocator.DEFAULT.heapBuffer(8, 10);
        print(buf);

        buf.writeBytes(new byte[] {1, 2, 3, 4, 5});
        print(buf);

        buf.writeBytes(new byte[] {1, 2, 3, 4, 5});
        print(buf);

        buf.writeBytes(new byte[] {1, 2, 3, 4, 5});
        print(buf);

    }

    public static void print(ByteBuf buf) {
        System.out.println("buf.isReadable()    :" + buf.isReadable());
        System.out.println("buf.readerIndex()   :" + buf.readerIndex());
        System.out.println("buf.readableBytes() :" + buf.readableBytes());
        System.out.println("buf.isWritable()    :" + buf.isWritable());
        System.out.println("buf.writerIndex()   :" + buf.writerIndex());
        System.out.println("buf.writableBytes() :" + buf.writableBytes());
        System.out.println("buf.capacity()      :" + buf.capacity());
        System.out.println("buf.maxCapacity()   :" + buf.maxCapacity());
        System.out.println("buf.isDirect()      :" + buf.isDirect());
        System.out.println("--------------");
    }

}
