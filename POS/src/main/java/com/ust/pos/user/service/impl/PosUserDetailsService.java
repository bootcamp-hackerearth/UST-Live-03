package com.ust.pos.user.service.impl;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PosUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userService.findByUserName(username);
        if (userDto == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        List<SimpleGrantedAuthority> authorities = Collections.emptyList();
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            authorities = userDto.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }
        return new User(userDto.getUsername(), userDto.getPassword(), authorities);
    }
}