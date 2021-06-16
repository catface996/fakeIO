package com.io.buffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import sun.misc.Unsafe;

@Slf4j
public class BufferIO {

    byte[] context = "abcdefghijklmn".getBytes(StandardCharsets.UTF_8);

    private static Unsafe getUnsafe() {
        try {
            Class clazz = Unsafe.class;
            Field field = clazz.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe)field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void whatIsBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        log.info("init buffer:{}", buffer);
        buffer.put(context);
        log.info("after put buffer:{}", buffer);
        buffer.flip();
        log.info("flip buffer:{}", buffer);
        for (int i = 0; i < 5; i++) {
            byte b = buffer.get();
            log.info("get byte:{}", (char)b);
            log.info("get buffer:{}", buffer);
        }
        buffer.compact();
        log.info("compact buffer:{}", buffer);
        for (int i = 0; i < 5; i++) {
            buffer.put((byte)('o' + i));
            log.info("put buffer:{}", buffer);
        }
    }

    /**
     * 分配jvm堆上的缓冲区
     */
    @Test
    public void allocateJvmHeapBuffer() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024 * 1024);
        log.info("init buffer:{}", buffer);
        buffer.put(context);
        log.info("after put buffer:{}", buffer);
        buffer.flip();
        log.info("flip buffer:{}", buffer);
        for (int i = 0; i < 5; i++) {
            byte b = buffer.get();
            log.info("get byte:{}", (char)b);
            log.info("get buffer:{}", buffer);
        }
        buffer.compact();
        System.in.read();
        log.info("compact buffer:{}", buffer);
        for (int i = 0; i < 5; i++) {
            buffer.put((byte)('o' + i));
            log.info("put buffer:{}", buffer);
        }
    }

    /**
     * 分配java进程怼上的缓冲区,此堆是内核对进程分配的堆空间,非jvm堆空间
     */
    @Test
    public void allocateProgressHeapBuffer() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024 * 1024);
        log.info("init buffer:{}", buffer);
        buffer.put(context);
        log.info("after put buffer:{}", buffer);
        buffer.flip();
        log.info("flip buffer:{}", buffer);
        for (int i = 0; i < 5; i++) {
            byte b = buffer.get();
            log.info("get byte:{}", (char)b);
            log.info("get buffer:{}", buffer);
        }
        buffer.compact();
        System.in.read();
        log.info("compact buffer:{}", buffer);
        for (int i = 0; i < 5; i++) {
            buffer.put((byte)('o' + i));
            log.info("put buffer:{}", buffer);
        }
    }

    /**
     * 使用unsafe类分配直接内存
     * <p>
     * 注意,Usafe类无法直接使用,需要用反射方式获得
     * <p>
     * unsafe 分配的内存不在direct中体现
     *
     * @throws IOException
     */
    @Test
    public void unSafeAllocateMemory() throws IOException {
        //Unsafe unsafe = Unsafe.getUnsafe();
        Unsafe unsafe = getUnsafe();
        long size = unsafe.allocateMemory(1024 * 1024 * 1024 * 2L);
        System.out.println(size);
        System.in.read();
    }

    @Test
    public void mappedBuffer() throws Exception {
        RandomAccessFile raf = new RandomAccessFile("./target/Test2.txt", "rw");
        FileChannel fc = raf.getChannel();
        //将test.txt文件所有数据映射到虚拟内存，并只读
        MappedByteBuffer mappedByteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size());
        mappedByteBuffer.load();
        while (!mappedByteBuffer.isLoaded()) {
        }
        System.out.println("Load success.");
        System.in.read();
        mappedByteBuffer.force();
    }

}
