package com.JoAI.security;

import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class JwtBlacklist {

    private final Set<String> blacklistedTokens = new HashSet<>();

    public boolean blackToken(String token) {
        return blacklistedTokens.add(token);
    }

    public boolean isTokenBlacked(String token) {
        return blacklistedTokens.contains(token);
    }
}
    

