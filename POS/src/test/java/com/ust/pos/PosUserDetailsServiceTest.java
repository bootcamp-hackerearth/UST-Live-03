package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import com.ust.pos.user.service.impl.PosUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PosUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private PosUserDetailsService posUserDetailsService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setUsername("admin");
        userDto.setPassword("$2a$10$encryptedPassword");
        userDto.setRoles(Arrays.asList("ROLE_ADMIN", "ROLE_USER"));
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {

        when(userService.findByUserName("admin")).thenReturn(userDto);

        UserDetails userDetails = posUserDetailsService.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("$2a$10$encryptedPassword", userDetails.getPassword());

        assertEquals(2, userDetails.getAuthorities().size());

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
        );

        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
        );

        verify(userService, times(1)).findByUserName("admin");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {

        when(userService.findByUserName("admin")).thenReturn(null);


        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> posUserDetailsService.loadUserByUsername("admin")
                );

        assertEquals("User not found: admin", exception.getMessage());

        verify(userService, times(1)).findByUserName("admin");
    }
}