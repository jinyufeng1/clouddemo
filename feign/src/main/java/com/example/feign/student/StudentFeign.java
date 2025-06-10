package com.example.feign.student;

import com.example.feign.student.domain.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("provider") // 去注册中心找 provider 服务
public interface StudentFeign {
    // 实体字段和原接口一致就可以, 甚至可以不是同一个类型
    @RequestMapping("/student/info")
    Student studentInfo(@RequestParam("studentId") Long studentId);

    @RequestMapping("/student/all")
    List<Student> studentAll();

    @RequestMapping("/student/create")
    String studentCreate(@RequestParam("name") String name, @RequestParam("gender") Integer gender);

    @RequestMapping("/student/update")
    String studentUpdate(@RequestParam("studentId") Long studentId,
                         @RequestParam("name") String name, @RequestParam("gender") Integer gender);

    @RequestMapping("/student/delete")
    String studentDelete(@RequestParam("studentId") Long studentId);
}
