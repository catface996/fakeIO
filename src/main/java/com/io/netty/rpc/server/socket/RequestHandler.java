package com.io.netty.rpc.server.socket;

import com.io.netty.rpc.protocol.RpcHeader;
import com.io.netty.rpc.protocol.RpcRequest;
import com.io.netty.rpc.protocol.RpcResponseBody;
import com.io.netty.rpc.server.service.ServiceFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/29 2:24 下午
 */
@Slf4j
public class RequestHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest rpcRequest = (RpcRequest)msg;
        Object result = ServiceFactory.callService(rpcRequest);
        RpcResponseBody rpcResponseBody = new RpcResponseBody(result, String.class);
        RpcHeader rpcHeader = RpcHeader.createResponseHeader(rpcRequest.getRpcHeader().getRequestId());
        SocketPool.sendResponse(rpcHeader, rpcResponseBody);
    }
}
