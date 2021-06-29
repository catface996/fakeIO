package com.io.netty.rpc.client.factory;

import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;

import com.io.netty.rpc.client.callback.ResponseMappingCallback;
import com.io.netty.rpc.client.socket.SocketPool;
import com.io.netty.rpc.convert.BeanByteConvert;
import com.io.netty.rpc.protocol.RpcHeader;
import com.io.netty.rpc.protocol.RpcRequestBody;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/29 10:24 上午
 */
@Slf4j
public class ServiceFactory {

    public static <T> T getBean(Class<T> clazz) {

        ClassLoader classLoader = clazz.getClassLoader();
        Class<?>[] methodInfos = {clazz};

        return (T)Proxy.newProxyInstance(classLoader, methodInfos, (proxy, method, args) -> {
            // 1.根据接口及调用方法构造出要调用的服务名
            String serviceName = clazz.getName();
            String methodName = method.getName();
            // 构造请求体
            RpcRequestBody rpcRequestBody = new RpcRequestBody(serviceName, methodName, method.getParameterTypes(),
                args, method.getReturnType());
            byte[] bodyBytes = BeanByteConvert.objectToBytes(rpcRequestBody);
            //log.info("bodyBytes size:{}",bodyBytes.length);

            // 构造请求头
            RpcHeader rpcHeader = RpcHeader.createRequestHeader(bodyBytes.length);
            byte[] headBytes = BeanByteConvert.objectToBytes(rpcHeader);
            //log.info("headBytes size:{}",headBytes.length);

            // 请求头和请求体序列化成byte数组
            ByteBuf requestBuf = PooledByteBufAllocator.DEFAULT.buffer(headBytes.length + bodyBytes.length);
            requestBuf.writeBytes(headBytes).writeBytes(bodyBytes);

            // 注册回调
            CompletableFuture<Object> resFuture = new CompletableFuture<>();
            ResponseMappingCallback.registerCallback(rpcHeader.getRequestId(), resFuture);

            // 获取通信的socket
            NioSocketChannel nioSocketChannel = SocketPool.getChannel(serviceName);
            // 此处可以调用sync()或者不调用均可,调用则等待发送数据完成
            nioSocketChannel.writeAndFlush(requestBuf).sync();

            return resFuture.get();
        });
    }

}
