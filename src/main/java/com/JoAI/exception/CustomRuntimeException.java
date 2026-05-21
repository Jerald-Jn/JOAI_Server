package com.JoAI.exception;

public class CustomRuntimeException extends RuntimeException {
    
    public CustomRuntimeException(String message, Exception e){
        super(message,e);
    }

    public CustomRuntimeException(Exception e){
        super(e.getMessage(),e);
    }

    public CustomRuntimeException(String message){
        super(message);
    }
}
