package com.ust.pos.api;

import com.ust.pos.config.JWTUtility;
import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenGenerationController {
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final AuthenticationProvider authenticationProvider;
    private final JWTUtility jwtUtility;

    public TokenGenerationController(UserDetailsService userDetailsService, UserService userService, AuthenticationProvider authenticationProvider, JWTUtility jwtUtility) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.authenticationProvider = authenticationProvider;
        this.jwtUtility = jwtUtility;
    }

    @PostMapping("/api/authenticate")
    public UserDto authenticate(@RequestBody UserDto userDto) {
        try {
            authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getUsername());
            final String token = jwtUtility.generateToken(userDetails);
            UserDto userDto1 = userService.findByUserName(userDto.getUsername());
            userDto1.setToken(token);
            return userDto1;
        } catch (Exception e) {
            return new UserDto("Error");
        }
    }

    @PostMapping("/api/validateToken")
    public Boolean validateToken(@RequestBody UserDto jwtRequest) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
            return jwtUtility.validateToken(jwtRequest.getToken(), userDetails);
        } catch (Exception e) {
            return false;
        }
    }
}