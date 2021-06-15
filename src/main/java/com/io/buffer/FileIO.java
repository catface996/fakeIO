package com.io.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class FileIO {

    byte[] context = "abcdefghijklmn".getBytes(StandardCharsets.UTF_8);

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



}
