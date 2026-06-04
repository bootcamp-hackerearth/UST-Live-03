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

@ExtendWith(MockitoExtension.class)
class PosUserDetailsServiceTest {

    @InjectMocks
    private PosUserDetailsService posUserDetailsService;

    @Mock
    private UserService userService;

    @Test
    void loadUserByUsername_Success() {
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setPassword("password123");
        Mockito.when(userService.findByUserName("testUser")).thenReturn(userDto);
        UserDetails userDetails = posUserDetailsService.loadUserByUsername("testUser");
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals("testUser", userDetails.getUsername());
        Assertions.assertEquals("password123", userDetails.getPassword());
    }

    @Test
    void loadUserByUsername_UserNotFound() {
        Mockito.when(userService.findByUserName("unknown")).thenReturn(null);
        UsernameNotFoundException ex = Assertions.assertThrows(UsernameNotFoundException.class,
                () -> posUserDetailsService.loadUserByUsername("unknown")
        );
        Assertions.assertTrue(ex.getMessage().contains("User not found"));
    }
}

