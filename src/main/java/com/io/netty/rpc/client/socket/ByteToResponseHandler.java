package com.io.netty.rpc.client.socket;

import java.util.List;

import com.io.netty.rpc.convert.BeanByteConvert;
import com.io.netty.rpc.protocol.RpcHeader;
import com.io.netty.rpc.protocol.RpcResponse;
import com.io.netty.rpc.protocol.RpcResponseBody;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author by catface
 * @date 2021/6/29 12:27 下午
 */
public class ByteToResponseHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list)
        throws Exception {
        while (byteBuf.readableBytes() >= RpcHeader.HEADER_SIZE) {
            byte[] headerBytes = new byte[RpcHeader.HEADER_SIZE];
            byteBuf.getBytes(byteBuf.readerIndex(), headerBytes);
            RpcHeader rpcHeader = BeanByteConvert.bytesToObject(headerBytes, RpcHeader.class);
            if (byteBuf.readableBytes() >= RpcHeader.HEADER_SIZE + rpcHeader.getSize()) {
                byteBuf.readBytes(RpcHeader.HEADER_SIZE);
                byte[] bodyBytes = new byte[rpcHeader.getSize()];
                byteBuf.readBytes(bodyBytes);
                RpcResponseBody responseBody = BeanByteConvert.bytesToObject(bodyBytes, RpcResponseBody.class);
                RpcResponse response = new RpcResponse(rpcHeader, responseBody);
                list.add(response);
            } else {
                break;
            }
        }
    }
}
