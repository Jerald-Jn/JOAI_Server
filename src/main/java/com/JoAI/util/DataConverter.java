package com.JoAI.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.JoAI.dto.ChatMsgResponse;
import com.JoAI.exception.CustomRuntimeException;
import com.JoAI.model.ChatMessage;
import com.JoAI.model.User;
import com.JoAI.repository.UserRepo;

@Component
public class DataConverter {

    UserRepo userRepo;

    DataConverter(UserRepo userRepo) {
        this.userRepo =  userRepo;
    }
    
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username= auth.getName(); // username
            Optional<User> user = userRepo.findById(username);
            if(user.isPresent()){
                return user.get();
            }
        }
        throw new CustomRuntimeException("User not found");
    }

    public List<ChatMsgResponse> chatMsgResponse() {
        List<ChatMessage> list = getCurrentUser().getList();
        List<ChatMsgResponse> chaList = new ArrayList<>();
        for (ChatMessage message : list) {
            chaList.add(ChatMsgResponse.builder().role(message.getRole()).message(message.getMessage()).build());
        }
        return chaList;
    }
}
