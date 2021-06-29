package com.io.netty.rpc.client;

import com.io.netty.rpc.client.factory.ServiceFactory;
import com.io.netty.rpc.client.socket.SocketPool;
import com.io.netty.rpc.server.service.MyService;
import com.io.netty.rpc.server.service.SchoolService;
import com.io.netty.rpc.server.service.param.Student;
import com.io.netty.rpc.server.service.result.Teacher;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/29 10:21 上午
 */
@Slf4j
public class RpcClient {

    public static void main(String[] args) {

        SocketPool.initPool();
        MyService myService = ServiceFactory.getBean(MyService.class);
        String ans = myService.hello("大猫");
        log.info("ans:{}", ans);

        Student student = new Student("大猫", 10);
        SchoolService schoolService = ServiceFactory.getBean(SchoolService.class);
        Teacher teacher = schoolService.findTeacher(student);
        log.info("找到的老师是:{}", teacher);

    }
}
