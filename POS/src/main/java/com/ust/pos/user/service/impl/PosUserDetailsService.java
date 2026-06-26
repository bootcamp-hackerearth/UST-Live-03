package com.ust.pos.user.service.impl;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PosUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public PosUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userService.findByUserName(username);
        if (userDto == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        List<String> roles = userDto.getRoles() != null ? userDto.getRoles() : Collections.emptyList();
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return org.springframework.security.core.userdetails.User
                .withUsername(userDto.getUsername())
                .password(userDto.getPassword())
                .authorities(authorities)
                .build();
    }
}