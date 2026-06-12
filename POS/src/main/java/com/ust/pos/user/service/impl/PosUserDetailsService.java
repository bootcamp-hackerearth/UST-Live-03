package com.ust.pos.user.service.impl;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PosUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        UserDto userDto = userService.findByIdentifier(identifier);
        if (userDto == null) {
            throw new UsernameNotFoundException("User not found: " + identifier);
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(userDto.getUsername())
                .password(userDto.getPassword())
                .build();
    }

}