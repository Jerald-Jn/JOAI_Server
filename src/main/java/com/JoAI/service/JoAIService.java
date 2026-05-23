package com.JoAI.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.stereotype.Service;

import com.JoAI.dto.ChatMsgResponse;
import com.JoAI.exception.CustomRuntimeException;
import com.JoAI.model.ChatMessage;
import com.JoAI.model.User;
import com.JoAI.repository.UserRepo;
import com.JoAI.security.JwtService;
import com.JoAI.util.DataConverter;

@Service
public class JoAIService {

    private final GoogleGenAiChatModel chatModel;
    private static final int DAILY_LIMIT = 10000;
    UserRepo userRepo;
    AuthenticationManager authenticationManager;
    DataConverter dataConverter;
    JwtService jwtService;

    JoAIService(GoogleGenAiChatModel chatModel, UserRepo userRepo, AuthenticationManager authenticationManager,
            DataConverter dataConverter, JwtService jwtService) {
        this.chatModel = chatModel;
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.dataConverter = dataConverter;
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> getResponse(String message) {
        String response = null;
        try {
            LocalDate today = LocalDate.now();
            User user = dataConverter.getCurrentUser();
            if (!today.equals(user.getUsageDate()) || user.getUsageDate()==null) {
                user.setUsedTokens(0);
                user.setUsageDate(today);
            }
            int userUsedTokens = user.getUsedTokens();
            if (DAILY_LIMIT > userUsedTokens) {
                ChatResponse chatResponse = chatModel.call(new Prompt(message));
                if (chatResponse != null) {
                    user.getList().add(ChatMessage.builder().role("User").message(message).build());
                    int usedTokens = chatResponse.getMetadata().getUsage().getTotalTokens();
                    userUsedTokens = userUsedTokens + usedTokens;
                    user.setUsedTokens(userUsedTokens);
                    Generation generation = chatResponse.getResult();
                    response = generation != null ? generation.getOutput().getText() : "";
                    user.getList().add(ChatMessage.builder().role("AI").message(response).build());
                    userRepo.save(user);
                }
            } else {
                response = "Daily token limit exceeded";
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            }
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<?> register(@NotNull User user) {
        try {
            user = userRepo.findById(user.getUserName()).orElse(user);
            if (user.getId() == null) {
                UUID userId = UUID.randomUUID();
                user.setPassword(new BCryptPasswordEncoder(BCryptVersion.$2Y, 6).encode(user.getPassword()));
                user.setId(userId);
                userRepo.save(user);
            } else {
                return ResponseEntity.badRequest().body(user.getUserName() + " already registered");
            }
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(user.getUserName() + " Registered");
    }

    public ResponseEntity<?> login(@NotNull User user) {
        try {
            if (userRepo.findById(user.getUserName()).isPresent()) {
                Authentication authentication = authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
                if (authentication.isAuthenticated()) {
                    String token = jwtService.generateToken(user.getUserName());
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
                }
            }
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
        return new ResponseEntity<>(user.getUserName() + " not Found", HttpStatus.NOT_FOUND);
    }

    public List<ChatMsgResponse> getHistoty() {
        List<ChatMsgResponse> chatMsgResponses;
        try {
            chatMsgResponses = dataConverter.chatMsgResponse();
        } catch (Exception e) {
            throw new CustomRuntimeException(e);
        }
        return chatMsgResponses;
    }

}
