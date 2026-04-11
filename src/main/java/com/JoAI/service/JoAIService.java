package com.JoAI.service;

import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.stereotype.Service;

import com.JoAI.exception.CustomRuntime;

@Service
public class JoAIService {

    private final GoogleGenAiChatModel chatModel;

    JoAIService(GoogleGenAiChatModel chatModel) {
        this.chatModel =chatModel;
    }

    public String getResponse(String message) throws CustomRuntime {
        String response = null;
        try {
            response = chatModel.call(message);
        } catch (Exception e) {
            throw new CustomRuntime(message,e);
        }
        return response;
    }
}
