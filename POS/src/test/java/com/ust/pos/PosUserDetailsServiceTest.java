package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import com.ust.pos.user.service.impl.PosUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class PosUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private PosUserDetailsService posUserDetailsService;

    @Test
    void loadUserByUsername_Success_WithRoles() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin");
        userDto.setPassword("encoded_pass");
        userDto.setRoles(Arrays.asList("ROLE_ADMIN", "ROLE_USER"));

        Mockito.when(userService.findByUserName("admin")).thenReturn(userDto);

        UserDetails result = posUserDetailsService.loadUserByUsername("admin");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("admin", result.getUsername());

        Assertions.assertFalse(result.getAuthorities().isEmpty());
        Assertions.assertEquals(2, result.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_Success_NoRoles() {
        UserDto userDto = new UserDto();
        userDto.setUsername("guest");
        userDto.setPassword("pass");
        userDto.setRoles(Collections.emptyList());

        Mockito.when(userService.findByUserName("guest")).thenReturn(userDto);

        UserDetails result = posUserDetailsService.loadUserByUsername("guest");

        Assertions.assertTrue(result.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        Mockito.when(userService.findByUserName("unknown")).thenReturn(null);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            posUserDetailsService.loadUserByUsername("unknown");
        });
    }

    @Test
    void loadUserByUsername_NullRoles_ReturnsEmptyAuthorities() {
        UserDto userDto = new UserDto();
        userDto.setUsername("nullUser");
        userDto.setPassword("pass");
        userDto.setRoles(null);

        Mockito.when(userService.findByUserName("nullUser")).thenReturn(userDto);

        UserDetails result = posUserDetailsService.loadUserByUsername("nullUser");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getAuthorities().isEmpty());
    }
}