package com.JoAI.dto;

import org.springframework.stereotype.Component;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    private String message;
}
