package com.io.netty.rpc.protocol;

import java.io.Serializable;

/**
 * @author by catface
 * @date 2021/6/29 10:36 上午
 */
public class RpcRequestBody implements Serializable {

    private static final long serialVersionUID = 8728555812800971670L;

    private String serviceName;
    private String method;
    private Class<?>[] argTypes;
    private Object[] args;
    private Class<?> resultType;

    public RpcRequestBody(String serviceName, String method, Class<?>[] argTypes, Object[] args, Class<?> resultType) {
        this.serviceName = serviceName;
        this.method = method;
        this.argTypes = argTypes;
        this.args = args;
        this.resultType = resultType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Class<?>[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(Class<?>[] argTypes) {
        this.argTypes = argTypes;
    }
}
