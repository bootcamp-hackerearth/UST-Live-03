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

    @Mock
    private UserService userService;

    @InjectMocks
    private PosUserDetailsService posUserDetailsService;

    @Test
    void loadUserByUsernameTest() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");
        userDto.setPassword("hashedpassword");

        Mockito.when(userService.findByUserName("admin@test.com")).thenReturn(userDto);

        UserDetails response = posUserDetailsService.loadUserByUsername("admin@test.com");

        Assertions.assertEquals("admin@test.com", response.getUsername());
        Assertions.assertEquals("hashedpassword", response.getPassword());
    }

    @Test
    void loadUserByUsernameNotFoundTest() {
        Mockito.when(userService.findByUserName("unknown@test.com")).thenReturn(null);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            posUserDetailsService.loadUserByUsername("unknown@test.com");
        });
    }
}