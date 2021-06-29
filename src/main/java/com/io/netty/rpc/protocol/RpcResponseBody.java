package com.io.netty.rpc.protocol;

import java.io.Serializable;

import lombok.Data;

/**
 * @author by catface
 * @date 2021/6/29 3:02 下午
 */
@Data
public class RpcResponseBody implements Serializable {

    private Object result;
    private Class<?> resultType;

    public RpcResponseBody(Object result, Class<?> resultType) {
        this.result = result;
        this.resultType = resultType;
    }
}
