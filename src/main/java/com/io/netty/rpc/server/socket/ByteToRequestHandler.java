package com.io.netty.rpc.server.socket;

import java.util.List;

import com.io.netty.rpc.convert.BeanByteConvert;
import com.io.netty.rpc.protocol.RpcHeader;
import com.io.netty.rpc.protocol.RpcRequest;
import com.io.netty.rpc.protocol.RpcRequestBody;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/29 12:28 下午
 */
@Slf4j
public class ByteToRequestHandler extends ByteToMessageDecoder {

    // ByteToMessageDecoder 会将为读取的字节延后到下次读取

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list)
        throws Exception {
        log.debug("receive request data,size:{}", byteBuf.readableBytes());
        while (byteBuf.readableBytes() >= RpcHeader.HEADER_SIZE) {
            byte[] headerBytes = new byte[RpcHeader.HEADER_SIZE];
            byteBuf.getBytes(byteBuf.readerIndex(), headerBytes);
            RpcHeader rpcHeader = BeanByteConvert.bytesToObject(headerBytes, RpcHeader.class);
            log.debug("rpcHeader:{}", rpcHeader);
            if (byteBuf.readableBytes() >= RpcHeader.HEADER_SIZE + rpcHeader.getSize()) {
                byteBuf.readBytes(RpcHeader.HEADER_SIZE);
                byte[] bodyBytes = new byte[rpcHeader.getSize()];
                byteBuf.readBytes(bodyBytes);
                RpcRequestBody rpcRequestBody = BeanByteConvert.bytesToObject(bodyBytes, RpcRequestBody.class);
                RpcRequest rpcRequest = new RpcRequest(rpcHeader, rpcRequestBody);
                NioSocketChannel channel = (NioSocketChannel)channelHandlerContext.channel();
                SocketPool.register(rpcRequest, channel);
                list.add(rpcRequest);
            } else {
                break;
            }
        }
    }
}
