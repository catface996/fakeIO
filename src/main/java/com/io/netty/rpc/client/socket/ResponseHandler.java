package com.io.netty.rpc.client.socket;

import com.io.netty.rpc.client.callback.ResponseMappingCallback;
import com.io.netty.rpc.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/29 11:50 上午
 */
@Slf4j
public class ResponseHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse rpcResponse = (RpcResponse)msg;
        ResponseMappingCallback.runCallback(rpcResponse);
    }

}
