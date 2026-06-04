package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import com.ust.pos.user.service.impl.PosUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PosUserDetailsServiceTest {

    @InjectMocks
    private PosUserDetailsService service;

    @Mock
    private UserService userService;

    @Test
    void loadUserByUsername_shouldHandleSuccessAndFailure() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin");
        userDto.setPassword("encodedPwd");
        when(userService.findByUserName("admin"))
                .thenReturn(userDto);
        UserDetails userDetails =
                service.loadUserByUsername("admin");
        assertAll(
                () -> assertNotNull(userDetails),
                () -> assertEquals("admin", userDetails.getUsername()),
                () -> assertEquals("encodedPwd", userDetails.getPassword())
        );
        when(userService.findByUserName("missing"))
                .thenReturn(null);
        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing")
        );
        assertEquals("User not found: missing", ex.getMessage());
    }
}