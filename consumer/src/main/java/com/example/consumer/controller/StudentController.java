package com.example.consumer.controller;

import com.example.feign.student.StudentFeign;
import com.example.feign.student.domain.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentController {

    @Autowired
    private StudentFeign studentFeign;

    @RequestMapping("/student/info")
    public Student studentInfo(@RequestParam("studentId") Long studentId) {
        return studentFeign.studentInfo(studentId);
    }

    @RequestMapping("/student/all")
    public List<Student> studentAll() {
        return studentFeign.studentAll();
    }

    @RequestMapping("/student/create")
    public String studentCreate(@RequestParam("name") String name, @RequestParam("gender") Integer gender) {
        return studentFeign.studentCreate(name, gender);
    }

    @RequestMapping("/student/update")
    public String studentUpdate(@RequestParam("studentId") Long studentId,
                                @RequestParam("name") String name, @RequestParam("gender") Integer gender) {
        return studentFeign.studentUpdate(studentId, name, gender);
    }

    @RequestMapping("/student/delete")
    public String studentDelete(@RequestParam("studentId") Long studentId) {
        return studentFeign.studentDelete(studentId);
    }
}
