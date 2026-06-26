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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PosUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private PosUserDetailsService posUserDetailsService;

    @Test
    void testLoadUserByUsername_Success() {

        UserDto userDto = new UserDto();
        userDto.setUsername("john");
        userDto.setPassword("password123");
        userDto.setRoles(List.of("ROLE_USER", "ROLE_ADMIN"));

        when(userService.findByUserName("john")).thenReturn(userDto);

        UserDetails userDetails = posUserDetailsService.loadUserByUsername("john");

        assertNotNull(userDetails);
        assertEquals("john", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());

        assertEquals(2, userDetails.getAuthorities().size());

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_USER".equals(authority.getAuthority())));

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));

        verify(userService, times(1)).findByUserName("john");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {

        when(userService.findByUserName("unknown")).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> posUserDetailsService.loadUserByUsername("unknown")
        );

        assertEquals("User not found: unknown", exception.getMessage());

        verify(userService, times(1)).findByUserName("unknown");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void testLoadUserByUsername_NoRoles() {

        UserDto userDto = new UserDto();
        userDto.setUsername("guest");
        userDto.setPassword("guest123");
        userDto.setRoles(List.of());

        when(userService.findByUserName("guest")).thenReturn(userDto);

        UserDetails userDetails = posUserDetailsService.loadUserByUsername("guest");

        assertNotNull(userDetails);
        assertEquals("guest", userDetails.getUsername());
        assertEquals("guest123", userDetails.getPassword());

        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(userService, times(1)).findByUserName("guest");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void testLoadUserByUsername_SingleRole() {

        UserDto userDto = new UserDto();
        userDto.setUsername("employee");
        userDto.setPassword("emp123");
        userDto.setRoles(List.of("ROLE_USER"));

        when(userService.findByUserName("employee")).thenReturn(userDto);

        UserDetails userDetails = posUserDetailsService.loadUserByUsername("employee");

        assertNotNull(userDetails);
        assertEquals("employee", userDetails.getUsername());
        assertEquals("emp123", userDetails.getPassword());

        assertEquals(1, userDetails.getAuthorities().size());

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_USER".equals(authority.getAuthority())));

        verify(userService, times(1)).findByUserName("employee");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void testLoadUserByUsername_EmptyUsername() {

        when(userService.findByUserName("")).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> posUserDetailsService.loadUserByUsername("")
        );

        assertEquals("User not found: ", exception.getMessage());

        verify(userService, times(1)).findByUserName("");
        verifyNoMoreInteractions(userService);
    }
}