package com.JoAI.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.JoAI.model.User;
import com.JoAI.service.JoAIService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/jo")
@CrossOrigin(origins="*")
@Slf4j
public class JoAIController {

    JoAIService joAIService;

    JoAIController(JoAIService joAIService) {
        this.joAIService = joAIService;
    }

    @PostMapping("/")
    public ResponseEntity<?> getResponse(@RequestBody String message){
        log.info("message : {}",message);
        return ResponseEntity.ok().body(joAIService.getResponse(message));
    }

    @PostMapping("/check")
    public ResponseEntity<?> getHello(@RequestBody String message){
        log.info("message : {}",message);
        return ResponseEntity.ok().body("Hello");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
        log.info("Users : {}",user);
        return ResponseEntity.ok().body("Hello");
    }
}
