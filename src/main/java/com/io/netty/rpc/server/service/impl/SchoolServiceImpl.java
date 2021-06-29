package com.io.netty.rpc.server.service.impl;

import com.io.netty.rpc.server.service.SchoolService;
import com.io.netty.rpc.server.service.param.Student;
import com.io.netty.rpc.server.service.result.Teacher;
import lombok.extern.slf4j.Slf4j;

/**
 * @author by catface
 * @date 2021/6/29 3:29 下午
 */
@Slf4j
public class SchoolServiceImpl implements SchoolService {
    /**
     * 招老师
     *
     * @param student 学生
     * @return 老师
     */
    @Override
    public Teacher findTeacher(Student student) {
        log.info("找老师的学生:{}", student);
        return new Teacher("大大", 30, 5);
    }
}
