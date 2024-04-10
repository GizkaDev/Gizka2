package ru.gizka.api.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.suitability.minutes}")
    private String suitability;

    public JwtService() {
    }

    public String generateToken(String username) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(Integer.parseInt(suitability)).toInstant());
        log.info("Генерация токена для пользователя: {}", username);
        return JWT.create()
                .withSubject("User details")
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withIssuer("Database")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("Database")
                .build();

        DecodedJWT jwt = verifier.verify(token);
        log.info("Токен верифицирован для пользователя: {}", jwt.getClaim("username").asString());
        return jwt.getClaim("username").asString();
    }

    public Boolean verifyAdmin(String secret){
        log.info("Верификация администратора...");
        return secret.equals(this.secret);
    }
}
