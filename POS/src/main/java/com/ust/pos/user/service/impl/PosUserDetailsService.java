package com.ust.pos.user.service.impl;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PosUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public PosUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        UserDto userDto = userService.findByUserName(identifier);
        if (userDto == null) {
            throw new UsernameNotFoundException("User not found: " + identifier);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String authority : userDto.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(authority));
        }

        return org.springframework.security.core.userdetails.User.withUsername(userDto.getUsername()).password(userDto.getPassword()).authorities(authorities).build();
    }
}