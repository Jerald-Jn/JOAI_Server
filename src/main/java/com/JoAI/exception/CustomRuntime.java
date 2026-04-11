package com.JoAI.exception;

public class CustomRuntime extends RuntimeException {
    
    public CustomRuntime(String message, Exception e){
        super(message,e);
    }

    CustomRuntime(String message){
        super(message);
    }
}
