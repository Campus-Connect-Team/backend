package com.campusconnect.backend.config.web;

import com.campusconnect.backend.user.service.UserService;
import com.campusconnect.backend.util.jwt.JwtProvider;
import com.campusconnect.backend.util.security.CustomAuthenticationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final JwtFilter jwtFilter;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfiguration()))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy((SessionCreationPolicy.STATELESS)))
                .authorizeHttpRequests(authorizeRequest -> authorizeRequest
                                .requestMatchers(
                                        "/",
                                        "/users/log-in",
                                        "/users/sign-up/**",
                                        "/swagger-ui/index.html",
                                        "/v3/api-docs/**",
                                        "/h2-console/**",
                                        "/swagger-ui/swagger-ui-standalone-preset.js",
                                        "/swagger-ui/swagger-initializer.js",
                                        "/swagger-ui/swagger-ui-bundle.js",
                                        "/swagger-ui/swagger-ui.css",
                                        "/swagger-ui/index.css",
                                        "/swagger-ui/favicon-32x32.png",
                                        "/swagger-ui/favicon-16x16.png",
                                        "/api-docs/json/swagger-config",
                                        "/api-docs/json").permitAll()
                                .anyRequest()
                                .authenticated()
                )

//                .formLogin(customizer -> customizer
//                        .loginPage("/users/log-in")
//                        .loginProcessingUrl("/users/log-in").permitAll()
//                        .defaultSuccessUrl("/")
//                        .usernameParameter("studentNumber")
//                        .passwordParameter("password")
//                )

                .logout(customizer -> customizer
                        .logoutUrl("/users/log-out").permitAll()
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.addAllowedHeader("*");  // "Authorization", "Cache-Control", "Content-Type"
        configuration.addAllowedMethod("*");  // "GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS" ...
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);

        return urlBasedCorsConfigurationSource;
    }

    @Bean
    CustomAuthenticationHandler customAuthenticationHandler() {
        return new CustomAuthenticationHandler();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }
}

