package com.io.netty.rpc.server.service.param;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author by catface
 * @date 2021/6/29 3:27 下午
 */
@Data
@AllArgsConstructor
public class Student implements Serializable {

    private static final long serialVersionUID = -4557900749976807075L;

    private String name;

    private int age;
}
