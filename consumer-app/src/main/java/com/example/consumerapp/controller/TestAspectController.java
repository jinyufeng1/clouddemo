package com.example.consumerapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAspectController {

    @RequestMapping("/test/security/aspect")
    public void testSecurityAspect(){
        System.out.println("public void testSecurityAspect()");
    }
}
