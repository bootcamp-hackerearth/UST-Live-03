package com.ust.pos.api.tokengeneration;

import com.ust.pos.config.JWTUtility;
import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenGenerationController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JWTUtility jwtUtility;

    @Autowired
    private UserService userService;

    @PostMapping("/api/authenticate")
    public UserDto authenticate(@RequestBody UserDto userDto) {
        try {
            authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword())
            );

            UserDto persistedUser = userService.findByUserName(userDto.getUsername());
            UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getUsername());
            final String token = jwtUtility.generateToken(userDetails);

            UserDto response = new UserDto(token);
            response.setUsername(persistedUser.getUsername());
            response.setRoles(persistedUser.getRoles());
            return response;
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