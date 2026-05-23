package com.JoAI.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.JoAI.dto.ChatRequest;
import com.JoAI.model.User;
import com.JoAI.repository.UserRepo;
import com.JoAI.service.JoAIService;


@RestController()
@RequestMapping("/jo")
public class JoAIController {

    JoAIService joAIService;
    Logger log=LoggerFactory.getLogger(JoAIController.class);

    @Autowired
    UserRepo repo;

    JoAIController(JoAIService joAIService) {
        this.joAIService = joAIService;
    }

    @PostMapping("/")
    public ResponseEntity<?> getResponse(@RequestBody ChatRequest chatRequest){
        String message = chatRequest.getMessage();
        log.info("message : {}",message);
        return ResponseEntity.ok().body(joAIService.getResponse(message));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory() {
        log.info("get chat history");
        return ResponseEntity.ok().body(joAIService.getHistoty());
    }

    @GetMapping("/")
    public ResponseEntity<?> getHello(){
        log.info("chcek the API");
        return ResponseEntity.ok().body("Hello API works fine");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
        log.info("Users : {}",user);
        return joAIService.login(user);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        log.info("Users : {}",user);
        try {
            return joAIService.register(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
