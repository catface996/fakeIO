package com.io.netty.rpc.server.service.result;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author by catface
 * @date 2021/6/29 3:27 下午
 */
@Data
@AllArgsConstructor
public class Teacher implements Serializable {

    private static final long serialVersionUID = 2195899117855794027L;

    private String name;

    private int age;

    private int workAge;
}
