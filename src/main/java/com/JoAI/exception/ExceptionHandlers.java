package com.JoAI.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlers {
    Logger log=LoggerFactory.getLogger(ExceptionHandlers.class);

    @ExceptionHandler(CustomRuntime.class)
    public ResponseEntity<String> customRuntimeExceptionHandler(Exception e) {
        log.info("CustomRuntimeException -> :{} ", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
