package com.example.bankcards.service;

import com.example.bankcards.entity.CardUser;
import com.example.bankcards.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserRepository userRepository;
    @Value("${app.secret-key}")
    private String secretKey;

    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        CardUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Could not generate token. User is not in database"));
        return Jwts.builder()
                .subject(username)
                .claims().add("roles", user.getRoles())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*120))
                .and()
                .signWith(getKey())
                .compact();
    }

    private Key getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
