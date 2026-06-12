package com.ust.pos.api;

import com.ust.pos.config.JWTUtility;
import com.ust.pos.dto.UserDto;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenGenerationController {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JWTUtility jwtUtility;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @PostMapping("/api/authenticate")
    public ResponseEntity<Object> authenticate(@RequestBody UserDto userDto) {
        try {
            authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDto.getUsername(), userDto.getPassword()
                    )
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getUsername());
            final String token = jwtUtility.generateToken(userDetails);
            return ResponseEntity.ok(new UserDto(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Authentication failed.");
        }
    }

    @PostMapping("/api/validateToken")
    public Boolean validateToken(@RequestBody UserDto jwtRequest) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername
                    (jwtRequest.getUsername());
            return jwtUtility.validateToken(jwtRequest.getToken(), userDetails);
        } catch (Exception e) {
            return false;
        }
    }
}