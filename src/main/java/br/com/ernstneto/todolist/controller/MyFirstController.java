package br.com.ernstneto.todolist.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/firstroute")
public class MyFirstController {
    
    
    @GetMapping("/")
    public String firstMessage(){
        return "My first message from Spring Boot!";
    }
}
