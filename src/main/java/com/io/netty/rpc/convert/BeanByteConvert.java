package com.io.netty.rpc.convert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author by catface
 * @date 2021/6/29 10:56 上午
 */
public class BeanByteConvert {
    /**
     * 对象转字节数组
     */
    public static byte[] objectToBytes(Object obj) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(out);
        outputStream.writeObject(obj);
        return out.toByteArray();
    }

    /**
     * 字节数组转对象
     */
    public static <T> T bytesToObject(byte[] bytes, Class<T> clazz) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        ObjectInputStream sIn = new ObjectInputStream(in);
        return (T)sIn.readObject();
    }
}
