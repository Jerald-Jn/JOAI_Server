package com.JoAI.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "User")
public class User {
    UUID id;
    @Id
    String userName;
    String email;
    String password;
    int usedTokens;
    LocalDate usageDate;
    private List<ChatMessage> list = new ArrayList<>();

    @Override
    public String toString() {
        return "User [id=" + id + ", userName=" + userName + ", email=" + email + ", password=" + password
                + ", usedTokens=" + usedTokens + "List = "+ list + "]";
    }
}
