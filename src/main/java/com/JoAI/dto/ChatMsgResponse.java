package com.JoAI.dto;

import org.springframework.stereotype.Component;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMsgResponse {
    String role;
    String message;
}
