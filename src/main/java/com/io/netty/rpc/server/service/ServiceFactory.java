package com.io.netty.rpc.server.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.io.netty.rpc.protocol.RpcRequest;
import com.io.netty.rpc.protocol.RpcRequestBody;
import com.io.netty.rpc.server.service.impl.MyServiceImpl;
import com.io.netty.rpc.server.service.impl.SchoolServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/29 11:41 上午
 */
@Slf4j
public class ServiceFactory {

    private static final Map<String, Object> SERVICE_MAPPING = new HashMap<>();

    static {
        SERVICE_MAPPING.put(MyService.class.getName(), new MyServiceImpl());
        SERVICE_MAPPING.put(SchoolService.class.getName(), new SchoolServiceImpl());
    }

    public static Object callService(RpcRequest request) {
        try {
            RpcRequestBody requestBody = request.getRpcRequestBody();
            Object service = SERVICE_MAPPING.get(requestBody.getServiceName());
            Class<?> clazz = Class.forName(request.getRpcRequestBody().getServiceName());
            Method method = clazz.getMethod(requestBody.getMethod(), requestBody.getArgTypes());
            return method.invoke(service, requestBody.getArgs());
        } catch (Exception e) {
            log.error("call service error.", e);
        }
        return null;
    }
}
