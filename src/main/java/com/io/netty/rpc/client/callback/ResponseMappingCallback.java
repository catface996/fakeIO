package com.io.netty.rpc.client.callback;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.io.netty.rpc.protocol.RpcResponse;

/**
 * @author by catface
 * @date 2021/6/29 11:08 上午
 */
public class ResponseMappingCallback {

    private static final ConcurrentHashMap<Long, CompletableFuture<Object>> MAPPING = new ConcurrentHashMap<>();

    public static void registerCallback(long requestId, CompletableFuture<Object> completableFuture) {
        MAPPING.putIfAbsent(requestId, completableFuture);
    }

    public static void runCallback(RpcResponse response) {
        CompletableFuture<Object> completableFuture = MAPPING.get(response.getRpcHeader().getRequestId());
        completableFuture.complete(response.getRpcResponseBody().getResult());
        MAPPING.remove(response.getRpcHeader().getRequestId());
    }
}
