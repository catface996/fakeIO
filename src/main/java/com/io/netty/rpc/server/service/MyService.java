package com.io.netty.rpc.server.service;

/**
 * @author by catface
 * @date 2021/6/29 10:21 上午
 */
public interface MyService {

    /**
     * 打招呼接口
     *
     * @param name 姓名
     * @return 应答
     */
    String hello(String name);
}
