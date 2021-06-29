package com.io.netty.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author by catface
 * @date 2021/6/29 11:12 上午
 */
@Data
@AllArgsConstructor
public class RpcRequest {

    private RpcHeader rpcHeader;

    private RpcRequestBody rpcRequestBody;

    public RpcHeader getRpcHeader() {

        return rpcHeader;
    }

    public void setRpcHeader(RpcHeader rpcHeader) {
        this.rpcHeader = rpcHeader;
    }

    public RpcRequestBody getRpcRequestBody() {
        return rpcRequestBody;
    }

    public void setRpcRequestBody(RpcRequestBody rpcRequestBody) {
        this.rpcRequestBody = rpcRequestBody;
    }
}
