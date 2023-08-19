package com.arvindsri84.apps.helloworld;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SayHello {

    @GetMapping
    public String Hello(String name){
        if( name == null || name.trim().length() == 0){
            return "Hello World!";
        }
        return "Hello, " + name + "!";
    }

}
