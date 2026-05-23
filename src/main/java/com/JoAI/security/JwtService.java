package com.JoAI.security;

// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.JoAI.exception.CustomRuntimeException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

    @Value("${ec.private.key}")
    String privateKey;
    @Value("${ec.public.key}")
    String publicKey;

    public String generateToken(String userName) {
        Instant instant = Instant.now();
        LocalDate localDate = LocalDate.now().plusMonths(1);
        return Jwts.builder()
                .claims()
                .subject(userName)
                .issuedAt(Date.from(instant))
                .expiration(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .and()
                .signWith(generatePrivateKey())
                .compact();
    }

    private PrivateKey generatePrivateKey() {
        byte[] tokenKey=null;
        try {
        if(privateKey.startsWith("-----BEGIN PRIVATE KEY-----")){
            tokenKey = getKey(privateKey).getBytes();

        }
        byte[] secretKeyBytes = Base64.getDecoder().decode(tokenKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(secretKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CustomRuntimeException("Unable to generate Ec private key: " + e.getMessage());
        }
    }

    private PublicKey generatePublicKey1() {
        String tokenKey = "";
        try {
        if (publicKey.startsWith("-----BEGIN PUBLIC KEY-----")) {
            tokenKey = getKey(publicKey);
        }
        byte[] secretKeyBytes = Base64.getDecoder().decode(tokenKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(secretKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CustomRuntimeException("Unable to generate Ec private key: " + e.getMessage());
        }
    }

    private String getKey(String data) {
        StringBuilder builder = new StringBuilder();
        builder.append(data.replace(System.lineSeparator(), "")
                .replace("-----BEGIN PRIVATE KEY-----", "").replaceAll("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "").replaceAll("-----END PUBLIC KEY-----", ""));
        return builder.toString();
    }

    public String getUsernameByToken(String token) {
        try {
            return extractClaims(token, Claims::getSubject);
        } catch (Exception e) {
            throw new CustomRuntimeException("error in 'getUsernameByToken' method");
        }
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
        try {
            final Claims claims = extractAllClaims(token);
            return claimResolver.apply(claims);
        } catch (Exception e) {
            throw new CustomRuntimeException("error in 'extractClaims' method");
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(generatePublicKey1())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new CustomRuntimeException("error in 'extractAllClaims' method");
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String userName = getUsernameByToken(token);
            return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            throw new CustomRuntimeException("error in 'validateToken' method");
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            throw new CustomRuntimeException("error in 'isTokenExpired' method");
        }
    }

    private Date extractExpiration(String token) {
        try {
            return extractClaims(token, Claims::getExpiration);
        } catch (Exception e) {
            throw new CustomRuntimeException("error in 'extractExpiration' method");
        }
    }

}
