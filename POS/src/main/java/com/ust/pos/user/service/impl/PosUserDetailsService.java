package com.ust.pos.user.service.impl;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Primary
@Service
public class PosUserDetailsService implements UserDetailsService {

    private final UserService userService;

    PosUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        UserDto userDto = userService.findByUserName(identifier);
        if (userDto == null) {
            throw new UsernameNotFoundException("User not found: " + identifier);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(userDto.getRoles().get(0)));

        return org.springframework.security.core.userdetails.User
                .withUsername(userDto.getUsername())
                .password(userDto.getPassword()).authorities(authorities)
                .build();
    }
}