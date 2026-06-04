package com.ust.pos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class WebSecurityConfig {

    public static final String JAVA_IN_USE_SECURITY_SCHEME = "JavaInUseSecurityScheme";

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) {
        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->

                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()

                        .requestMatchers("/login", "/register", "/api/authenticate", "/api/validateToken", "/swagger-ui/**", "/v3/**").permitAll()

                        .anyRequest().authenticated()

                )

                .logout(org.springframework.security.config.annotation.web.configurers.LogoutConfigurer::permitAll);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }


    @Bean
    public AuthenticationManager authenticationManager(

            AuthenticationConfiguration config) {

        return config.getAuthenticationManager();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {

        auth.userDetailsService(userDetailsService)

                .passwordEncoder(bCryptPasswordEncoder);

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Allow the specific origin

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        configuration.setAllowCredentials(true); // Allow credentials if needed

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration); // Apply CORS settings to all paths

        return source;

    }

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()

                .info(new Info().title("JavaInUse Authentication Service"))

                .addSecurityItem(new SecurityRequirement().addList(JAVA_IN_USE_SECURITY_SCHEME))

                .components(new Components().addSecuritySchemes(JAVA_IN_USE_SECURITY_SCHEME, new SecurityScheme()

                        .name(JAVA_IN_USE_SECURITY_SCHEME).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));

    }

    @Bean
    public AuthenticationProvider userDetailsAuthProvider() {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);

        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);

        return authenticationProvider;

    }
}