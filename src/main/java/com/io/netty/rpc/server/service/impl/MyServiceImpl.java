package com.io.netty.rpc.server.service.impl;

import com.io.netty.rpc.server.service.MyService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/29 11:36 上午
 */
@Slf4j
public class MyServiceImpl implements MyService {
    /**
     * 打招呼接口
     *
     * @param name 姓名
     * @return 应答
     */
    @Override
    public String hello(String name) {
        log.info("接收到打招呼的同学:{}", name);
        return "hello " + name;
    }
}
