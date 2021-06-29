package com.io.netty.rpc.server.service;

import com.io.netty.rpc.server.service.param.Student;
import com.io.netty.rpc.server.service.result.Teacher;

/**
 * @author by catface
 * @date 2021/6/29 3:26 下午
 */
public interface SchoolService {

    /**
     * 招老师
     *
     * @param student 学生
     * @return 老师
     */
    Teacher findTeacher(Student student);
}
