package com.arvindsri84.apps.helloworld;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/unsecure")
public class SayHello {

    @GetMapping( path = "/hello")
    public String Hello(String name){
        if( name == null || name.trim().length() == 0){
            return "Hello my friend!";
        }
        return "Hello, " + name + "!";
    }

}
