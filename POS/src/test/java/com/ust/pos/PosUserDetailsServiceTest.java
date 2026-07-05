package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.user.service.UserService;
import com.ust.pos.user.service.impl.PosUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.mockito.Mockito.when;

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
        userDto.setUsername("john.doe@example.com");
        userDto.setPassword("encodedPassword");
        userDto.setRoles(List.of("ROLE_USER", "ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Load User By Username - Success with Roles")
    void loadUserByUsername_Success() {
        when(userService.findByUserName("john.doe@example.com")).thenReturn(userDto);

        UserDetails userDetails = posUserDetailsService.loadUserByUsername("john.doe@example.com");

        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals("john.doe@example.com", userDetails.getUsername());
        Assertions.assertEquals("encodedPassword", userDetails.getPassword());
        Assertions.assertEquals(2, userDetails.getAuthorities().size());

        boolean hasAdminRole = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        Assertions.assertTrue(hasAdminRole);
    }

    @Test
    @DisplayName("Load User By Username - Success with Null Roles List")
    void loadUserByUsername_Success_NullRoles() {
        userDto.setRoles(null);
        when(userService.findByUserName("john.doe@example.com")).thenReturn(userDto);

        UserDetails userDetails = posUserDetailsService.loadUserByUsername("john.doe@example.com");

        Assertions.assertNotNull(userDetails);
        Assertions.assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    @DisplayName("Load User By Username - Failure: User Not Found fallback")
    void loadUserByUsername_Failure_UserNotFound() {
        when(userService.findByUserName("unknown@example.com")).thenReturn(null);

        Assertions.assertThrows(UsernameNotFoundException.class, () ->
                posUserDetailsService.loadUserByUsername("unknown@example.com")
        );
    }
}