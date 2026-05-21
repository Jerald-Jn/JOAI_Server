package com.JoAI.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.JoAI.model.User;
import com.JoAI.repository.UserRepo;

@Component
public class PrincpleUser implements UserDetailsService {

    UserRepo userRepo;

    public PrincpleUser(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found - " + username));
        if (user != null) {
            return new UserDetailsTemp(user);
        }
        return null;
    }

}
