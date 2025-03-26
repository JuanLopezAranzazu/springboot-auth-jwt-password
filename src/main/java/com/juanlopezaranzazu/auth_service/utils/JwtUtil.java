package com.juanlopezaranzazu.auth_service.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final Key key; // Clave secreta
    private final Long expiration; // Tiempo de expiración

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") Long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    // Generar Token
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extraer Usuario desde Token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Verificar si el Token es válido
    public boolean validateToken(String token, String email) {
        return email.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    // Extraer una propiedad del Token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extraer Todos los Claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // Verificar Expiración del Token
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}