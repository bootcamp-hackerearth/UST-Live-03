package com.ust.pos.api;

import com.ust.pos.config.JWTUtility;
import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.RoleValidateDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.node.service.NodeService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
public class TokenGenerationController {
    UserDetailsService userDetailsService;
    private final AuthenticationProvider authenticationProvider;
    private final JWTUtility jwtUtility;
    private final NodeService nodeService;

    public TokenGenerationController(UserDetailsService userDetailsService, AuthenticationProvider authenticationProvider, JWTUtility jwtUtility, NodeService nodeService) {
        this.userDetailsService = userDetailsService;
        this.authenticationProvider = authenticationProvider;
        this.jwtUtility = jwtUtility;
        this.nodeService = nodeService;
    }

    @PostMapping("/api/authenticate")
    public UserDto authenticate(@RequestBody UserDto userDto) {
        try {
            Authentication authentication = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));

            if (authentication == null) {
                return new UserDto("Error");
            }
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            final String token = jwtUtility.generateToken(userDetails);
            return new UserDto(token);
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

    @PostMapping("/api/roleValidation")
    public  boolean roleValidation(@RequestBody RoleValidateDto roleValidateDto){
        try{
            UserDetails userDetails = userDetailsService.loadUserByUsername(roleValidateDto.getUsername());
            if(jwtUtility.validateToken(roleValidateDto.getToken(), userDetails)){
                List<String> userRoles = roleValidateDto.getRoles();
                String url = roleValidateDto.getUrl();
                NodeDto nodeDto = nodeService.findByPath("/"+url);
                List<String> nodeRoles = nodeDto.getRoles();
                if(!Collections.disjoint(nodeRoles, userRoles)){
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}