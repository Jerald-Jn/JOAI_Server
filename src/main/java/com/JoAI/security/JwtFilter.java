package com.JoAI.security;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    JwtService jwtService;
    ApplicationContext applicationContext;
    JwtBlacklist jwtBlacklist;

    public JwtFilter(JwtService jwtService, ApplicationContext applicationContext, JwtBlacklist jwtBlacklist) {
        this.jwtService = jwtService;
        this.applicationContext = applicationContext;
        this.jwtBlacklist = jwtBlacklist;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        String userName = null;
        
        // if(token!=null && token.startsWith("Basic ")){
        //     if(jwtBlacklist.isTokenBlacked(token.substring(6))){
        //         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //         return;
        // }
        // filterChain.doFilter(request, response);
        // return;
        // }else
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            userName = jwtService.getUsernameByToken(token);

            if (jwtBlacklist.isTokenBlacked(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
            }

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = applicationContext.getBean(PrincpleUser.class).loadUserByUsername(userName);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                            null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
