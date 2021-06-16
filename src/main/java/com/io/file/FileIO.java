package com.io.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * @author by catface
 * @date 2021/6/16 1:54 下午
 */
public class FileIO {

    public static String PATH = "./target/Test.txt";
    public static byte[] DATA = "1234567890".getBytes(StandardCharsets.UTF_8);

    @Test
    public void normalWriteFile() throws IOException, InterruptedException {
        File file = new File(PATH);
        FileOutputStream out = new FileOutputStream(file);
        for (int times = 0; times < 5; times++) {
            // 每隔5秒写入1G,最多写入5G
            for (int i = 0; i < 1024 * 1024 * 100; i++) {
                out.write(DATA);
            }
            TimeUnit.SECONDS.sleep(10);
        }
    }
}
