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
    void loadUserByUsernameSuccessTest() {

        UserDto userDto = new UserDto();
        userDto.setUsername("admin");
        userDto.setPassword("encodedPwd");

        Mockito.when(userService.findByUserName("admin"))
                .thenReturn(userDto);

        UserDetails userDetails =
                posUserDetailsService.loadUserByUsername("admin");

        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals("admin", userDetails.getUsername());
        Assertions.assertEquals("encodedPwd", userDetails.getPassword());
    }

    @Test
    void loadUserByUsernameUserNotFoundTest() {

        Mockito.when(userService.findByUserName("admin"))
                .thenReturn(null);

        UsernameNotFoundException ex =
                Assertions.assertThrows(
                        UsernameNotFoundException.class,
                        () -> posUserDetailsService.loadUserByUsername("admin")
                );

        Assertions.assertEquals(
                "User not found: admin",
                ex.getMessage()
        );
    }
}