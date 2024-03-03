package ru.gizka.api.config.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.gizka.api.model.AppUser;
import ru.gizka.api.model.AuthUser;
import ru.gizka.api.service.AppUserService;
import ru.gizka.api.service.JwtService;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AppUserService appUserService;

    @Autowired
    public JwtFilter(JwtService jwtService,
                     AppUserService appUserService) {
        this.jwtService = jwtService;
        this.appUserService = appUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            if (jwt.isEmpty() || jwt.isBlank()) {
                log.error("Токен отсутствует");
                throw new JWTVerificationException("Токен отсутствует");
            } else {
                try {
                    String username = jwtService.validateToken(jwt);
                    Optional<AppUser> optionalUser = appUserService.getByLogin(username);
                    log.info("Извлечен токен из логина:{}", username);

                    UserDetails userDetails = new AuthUser(optionalUser.orElseThrow(
                            () -> {
                                log.error("Пользователь не найден: {}", username);
                                throw new EntityNotFoundException(String.format("Пользователь не найден: %s", username));
                            }
                    ));

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                                    userDetails.getAuthorities());
                    log.info("Токен проверен для пользователя: {}", username);

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (JWTVerificationException e) {
                    log.error("Токен не валиден", e);
                    throw new JWTVerificationException("Токен не валиден");
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}