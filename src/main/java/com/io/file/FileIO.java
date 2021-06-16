package com.io.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author by catface
 * @date 2021/6/16 1:54 下午
 */
@Slf4j
public class FileIO {

    public static byte[] DATA;

    static {
        DATA = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            DATA[i] = (byte)(i % 255);
        }
    }

    @Test
    public void normalWriteFile() throws IOException, InterruptedException {
        File file = new File("./target/Test1.txt");
        FileOutputStream out = new FileOutputStream(file);
        for (int times = 0; times < 5; times++) {
            // 每隔5秒写入1G,最多写入5G
            for (int i = 0; i < 1024 * 1024 * 100; i++) {
                out.write(DATA);
            }
            TimeUnit.SECONDS.sleep(10);
        }
    }

    @Test
    public void writeFileUseBufferInHeap() throws Exception {
        File file = new File("./target/Test2.txt");
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        long start = System.currentTimeMillis();
        for (int times = 0; times < 5; times++) {
            for (int i = 0; i < 1024 * 1024; i++) {
                out.write(DATA);
            }
        }
        long end = System.currentTimeMillis();
        log.info("write success.duration:{}", end - start);
        // 4679ms 写入 5.24G
    }

    @Test
    public void mappedBuffer2() throws Exception {
        RandomAccessFile raf = new RandomAccessFile("./target/Test3.txt", "rw");
        FileChannel fc = raf.getChannel();
        long start = System.currentTimeMillis();
        for (int times = 0; times < 5; times++) {
            MappedByteBuffer mappedByteBuffer = fc.map(MapMode.READ_WRITE, fc.size(), 1024L * 1024 * 1024);
            // 每隔5秒写入1G,最多写入5G
            for (int i = 0; i < 1024 * 1024; i++) {
                mappedByteBuffer.put(DATA);
            }
            mappedByteBuffer.force();
        }
        long end = System.currentTimeMillis();
        log.info("write success.duration:{}", end - start);
        // 5745 ms 写入 5.24G
    }
}
