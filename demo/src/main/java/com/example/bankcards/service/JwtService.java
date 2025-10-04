package com.example.bankcards.service;

import com.example.bankcards.entity.CardUser;
import com.example.bankcards.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserRepository userRepository;
    @Value("${app.secret-key}")
    private String secretKey;

    public String generateToken(String username){
        CardUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Could not generate token. User is not in database"));
        return Jwts.builder()
                .subject(username)
                .claims().add("roles", user.getRoles())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*120))
                .and()
                .signWith(getKey(),Jwts.SIG.HS384)
                .compact();
    }

    public String extractSub(String token){
        try {
            return extractAllClaims(token).getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        try {
            final String username = extractSub(token);
            Claims claims = extractAllClaims(token);

            return (username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(claims));
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    private SecretKey getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
