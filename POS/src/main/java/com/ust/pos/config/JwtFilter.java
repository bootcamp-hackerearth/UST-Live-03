package com.ust.pos.config;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private com.ust.pos.config.JWTUtility jwtUtility;

    @Autowired
    private UserDetailsService userService;

    @Override
    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest httpServletRequest,
                                    jakarta.servlet.http.HttpServletResponse httpServletResponse, jakarta.servlet.FilterChain filterChain)
            throws jakarta.servlet.ServletException, IOException {
        String authorization = httpServletRequest.getHeader("Authorization");
        String token = null;
        String userName = null;

        try {
            if (authorization != null && authorization.startsWith("Bearer ")) {
                token = authorization.substring(7);
                userName = jwtUtility.getUsernameFromToken(token);
            }
            if (null != userName && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(userName);

                boolean isValidToken =
                        jwtUtility.validateToken(token, userDetails);

                if (isValidToken) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(httpServletRequest));

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }

            filterChain.doFilter(httpServletRequest, httpServletResponse);

        } catch (ExpiredJwtException ex) {
            httpServletResponse.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "The token is not valid."
            );
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth");
    }

}