package com.io.netty.rpc.server.socket;

import java.util.concurrent.ConcurrentHashMap;

import com.io.netty.rpc.convert.BeanByteConvert;
import com.io.netty.rpc.protocol.RpcHeader;
import com.io.netty.rpc.protocol.RpcRequest;
import com.io.netty.rpc.protocol.RpcResponse;
import com.io.netty.rpc.protocol.RpcResponseBody;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/29 1:34 下午
 */
@Slf4j
public class SocketPool {

    // TODO 简化处理

    private static final ConcurrentHashMap<Long, NioSocketChannel> REQUEST_CHANNEL_MAPPING = new ConcurrentHashMap<>();

    public static void register(RpcRequest rpcRequest, NioSocketChannel nioSocketChannel) {
        REQUEST_CHANNEL_MAPPING.putIfAbsent(rpcRequest.getRpcHeader().getRequestId(), nioSocketChannel);
    }

    public static void sendResponse(RpcHeader rpcHeader, RpcResponseBody rpcResponseBody) {
        NioSocketChannel nioSocketChannel = REQUEST_CHANNEL_MAPPING.get(rpcHeader.getRequestId());
        try {
            byte[] bodyBytes = BeanByteConvert.objectToBytes(rpcResponseBody);
            rpcHeader.setSize(bodyBytes.length);
            byte[] headerBytes = BeanByteConvert.objectToBytes(rpcHeader);
            ByteBuf responseBuf = PooledByteBufAllocator.DEFAULT.buffer(bodyBytes.length + bodyBytes.length);
            responseBuf.writeBytes(headerBytes).writeBytes(bodyBytes);
            nioSocketChannel.writeAndFlush(responseBuf).sync();
            log.debug("send rpc response success;{}", new RpcResponse(rpcHeader, rpcResponseBody));
        } catch (Exception e) {
            log.info("rpc response to byte error.", e);
        }
    }

}
