package ru.gizka.api.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.gizka.api.config.security.jwt.JwtFilter;
import ru.gizka.api.model.appUser.AppUser;
import ru.gizka.api.model.appUser.AuthUser;
import ru.gizka.api.model.appUser.Role;
import ru.gizka.api.service.appUser.AppUserService;

import java.util.Optional;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    public static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final AppUserService appUserService;
    private final JwtFilter jwtFilter;
    private final AuthEntryPoint authEntryPoint;

    @Autowired
    public SecurityConfig(AppUserService appUserService,
                          JwtFilter jwtFilter,
                          AuthEntryPoint authEntryPoint) {
        this.appUserService = appUserService;
        this.jwtFilter = jwtFilter;
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return login -> {
            log.debug("Ищем пользователя: {}", login);
            Optional<AppUser> optionalUser = appUserService.getByLoginOptional(login);
            return new AuthUser(optionalUser.orElseThrow(
                    () -> new BadCredentialsException(String.format("Неверные учетные данные: %s", login))
            ));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.debug("Модуль шифрования...");
        return PASSWORD_ENCODER;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**");
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authConfig) throws Exception {
        log.debug("Менеджер аутентификации...");
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("Фильтры аутентификации...");
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
                                .requestMatchers("/api/user/registration").anonymous()
                                .requestMatchers("/api/user/token").permitAll()
                                .requestMatchers("/api/**").authenticated())
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint(authEntryPoint))
                .httpBasic(withDefaults())
                .sessionManagement((sessionManagement) ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
