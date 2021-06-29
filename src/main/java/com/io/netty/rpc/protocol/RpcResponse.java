package com.io.netty.rpc.protocol;

import java.io.Serializable;

import lombok.Data;

/**
 * @author by catface
 * @date 2021/6/29 11:25 上午
 */
@Data
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = -620908472946149503L;

    private RpcHeader rpcHeader;

    private RpcResponseBody rpcResponseBody;

    public RpcResponse(RpcHeader rpcHeader, RpcResponseBody rpcResponseBody) {
        this.rpcHeader = rpcHeader;
        this.rpcResponseBody = rpcResponseBody;
    }
}
