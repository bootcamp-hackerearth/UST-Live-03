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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PosUserDetailsServiceTest {

    public static final String ADMIN = "admin";
    @InjectMocks
    private PosUserDetailsService posUserDetailsService;

    @Mock
    private UserService userService;

    @Test
    void loadUserByUsernameSuccessTest() {

        UserDto userDto = new UserDto();
        userDto.setUsername(ADMIN);
        userDto.setPassword("encodedPwd");
        userDto.setRoles(List.of("ROLE_ADMIN", "ROLE_USER"));

        Assertions.assertNotNull(userDto.getRoles());
        assertEquals(2, userDto.getRoles().size());

        when(userService.findByUserName(ADMIN))
                .thenReturn(userDto);

        UserDetails userDetails =
                posUserDetailsService.loadUserByUsername(ADMIN);

        assertEquals(2, userDetails.getAuthorities().size());

        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
        );

        assertTrue(
                userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
        );
    }

    @Test
    void loadUserByUsernameUserNotFoundTest() {

        when(userService.findByUserName(ADMIN))
                .thenReturn(null);

        UsernameNotFoundException exception =
                Assertions.assertThrows(
                        UsernameNotFoundException.class,
                        () -> posUserDetailsService.loadUserByUsername(ADMIN)
                );

        assertEquals(
                "User not found: admin",
                exception.getMessage()
        );

        Mockito.verify(userService).findByUserName(ADMIN);
    }
}
