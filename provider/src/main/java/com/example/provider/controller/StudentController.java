package com.example.provider.controller;

import com.example.provider.service.StudentService;
import com.example.provider.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentController {

    @Autowired
    private StudentService service;

    @RequestMapping("/student/info")
    public Student studentInfo(@RequestParam("studentId") Long studentId) {
        return service.getById(studentId);
    }

    @RequestMapping("/student/all")
    public List<Student> studentAll() {
        return service.getAll();
    }

    @RequestMapping("/student/create")
    public String studentCreate(@RequestParam("name") String name, @RequestParam("gender") Integer gender) {
        Student student = new Student();
        student.setName(name);
        student.setGender(gender);
        return service.insert(student) ? "成功" : "失败";
    }

    @RequestMapping("/student/update")
    public String studentUpdate(@RequestParam("studentId") Long studentId,
                                @RequestParam("name") String name, @RequestParam("gender") Integer gender) {
        Student student = new Student();
        student.setId(studentId);
        student.setName(name);
        student.setGender(gender);
        return service.update(student) ? "成功" : "失败";
    }

    @RequestMapping("/student/delete")
    public String studentDelete(@RequestParam("studentId") Long studentId) {
        return service.delete(studentId) ? "成功" : "失败";
    }
}
