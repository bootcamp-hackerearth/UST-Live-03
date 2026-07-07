package com.ust.pos.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    private final JWTUtility jwtUtility;

    private final UserDetailsService userService;

    public JwtFilter(JWTUtility jwtUtility,UserDetailsService userService){
        this.jwtUtility=jwtUtility;
        this.userService=userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.equals("/api/authenticate")
                || path.equals("/api/validateToken")
                || path.startsWith("/api/security")
                || path.equals("/register")
                || path.equals("/login")) {

            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        try {

            if (authorization != null && authorization.startsWith("Bearer ")) {

                token = authorization.substring(7);

                if (token != null && token.contains(".")) {
                    userName = jwtUtility.getUsernameFromToken(token);
                } else {
                    logger.warn("Invalid JWT format");
                }
            }

            if (userName != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userService.loadUserByUsername(userName);

                if (BooleanUtils.isTrue(
                        jwtUtility.validateToken(token, userDetails))) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token expired");

        } catch (Exception e) {

            LOGGER.error("JWT Error: {}", e.getMessage(), e);

            filterChain.doFilter(request, response);
        }
    }
}