package com.io.netty.rpc.protocol;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

/**
 * @author by catface
 * @date 2021/6/29 10:35 上午
 */
@Data
public class RpcHeader implements Serializable {
    public static final int HEADER_SIZE = 98;
    int flag;
    private int size;
    private long requestId;

    private RpcHeader() {

    }

    public static RpcHeader createRequestHeader(int size) {
        RpcHeader header = new RpcHeader();
        header.requestId = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        header.size = size;
        header.flag = Integer.MAX_VALUE - 1;
        return header;
    }

    public static RpcHeader createResponseHeader(long requestId) {
        RpcHeader header = new RpcHeader();
        header.requestId = requestId;
        header.flag = Integer.MAX_VALUE - 2;
        return header;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
