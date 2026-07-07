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
import java.util.Set;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger JWT_LOGGER = LoggerFactory.getLogger(JwtFilter.class);
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/authenticate",
            "/api/validateToken",
            "/register",
            "/login",
            "/api/role/list");

    private final JWTUtility jwtUtility;
    private final UserDetailsService userService;

    public JwtFilter(JWTUtility jwtUtility, UserDetailsService userService) {
        this.jwtUtility = jwtUtility;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();

        if (isPublicPath(path)) {
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
                    JWT_LOGGER.warn("Invalid JWT format");
                }
            }

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(userName);

                if (BooleanUtils.isTrue(jwtUtility.validateToken(token, userDetails))) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        } catch (Exception e) {
            JWT_LOGGER.error("JWT Error: {}", e.getMessage());
            filterChain.doFilter(request, response);
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.contains(path) || path.startsWith("/api/security");
    }
}