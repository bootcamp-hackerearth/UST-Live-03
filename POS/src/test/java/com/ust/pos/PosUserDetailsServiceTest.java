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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PosUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private PosUserDetailsService posUserDetailsService;

    private UserDto buildUserDto(String username, String password, List<String> roles) {
        UserDto dto = new UserDto();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setRoles(roles);
        return dto;
    }

    @Test
    void loadUserByUsername_Success_WithMultipleRoles() {
        UserDto userDto = buildUserDto("admin", "encoded_pass", Arrays.asList("ROLE_ADMIN", "ROLE_USER"));
        Mockito.when(userService.findByUserName("admin")).thenReturn(userDto);

        UserDetails result = posUserDetailsService.loadUserByUsername("admin");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("admin", result.getUsername());
        Assertions.assertEquals("encoded_pass", result.getPassword());
        Assertions.assertEquals(2, result.getAuthorities().size());

        List<String> authorityNames = result.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        Assertions.assertTrue(authorityNames.contains("ROLE_ADMIN"));
        Assertions.assertTrue(authorityNames.contains("ROLE_USER"));
    }

    @Test
    void loadUserByUsername_Success_WithSingleRole() {
        UserDto userDto = buildUserDto("manager", "pass", List.of("ROLE_MANAGER"));
        Mockito.when(userService.findByUserName("manager")).thenReturn(userDto);

        UserDetails result = posUserDetailsService.loadUserByUsername("manager");

        Assertions.assertEquals(1, result.getAuthorities().size());
        Assertions.assertEquals(
                "ROLE_MANAGER",
                result.getAuthorities().iterator().next().getAuthority()
        );
    }

    @Test
    void loadUserByUsername_EmptyRoles_ReturnsEmptyAuthorities() {
        UserDto userDto = buildUserDto("guest", "pass", Collections.emptyList());
        Mockito.when(userService.findByUserName("guest")).thenReturn(userDto);

        UserDetails result = posUserDetailsService.loadUserByUsername("guest");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_NullRoles_ReturnsEmptyAuthorities() {
        UserDto userDto = buildUserDto("nullUser", "pass", null);
        Mockito.when(userService.findByUserName("nullUser")).thenReturn(userDto);

        UserDetails result = posUserDetailsService.loadUserByUsername("nullUser");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        Mockito.when(userService.findByUserName("unknown")).thenReturn(null);

        UsernameNotFoundException ex = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> posUserDetailsService.loadUserByUsername("unknown")
        );

        Assertions.assertTrue(ex.getMessage().contains("unknown"),
                "Exception message should include the missing username");
    }

    @Test
    void loadUserByUsername_ReturnsEnabledAndUnlockedUser() {
        UserDto userDto = buildUserDto("active", "pass", List.of("ROLE_USER"));
        Mockito.when(userService.findByUserName("active")).thenReturn(userDto);

        UserDetails result = posUserDetailsService.loadUserByUsername("active");

        Assertions.assertTrue(result.isEnabled());
        Assertions.assertTrue(result.isAccountNonExpired());
        Assertions.assertTrue(result.isAccountNonLocked());
        Assertions.assertTrue(result.isCredentialsNonExpired());
    }

    @Test
    void loadUserByUsername_PasswordIsPreservedAsIs() {
        UserDto userDto = buildUserDto("user", "$2a$10$hashedvalue", List.of("ROLE_USER"));
        Mockito.when(userService.findByUserName("user")).thenReturn(userDto);

        UserDetails result = posUserDetailsService.loadUserByUsername("user");

        Assertions.assertEquals("$2a$10$hashedvalue", result.getPassword());
    }

    @Test
    void loadUserByUsername_CallsUserServiceExactlyOnce() {
        UserDto userDto = buildUserDto("admin", "pass", List.of("ROLE_ADMIN"));
        Mockito.when(userService.findByUserName("admin")).thenReturn(userDto);

        posUserDetailsService.loadUserByUsername("admin");

        Mockito.verify(userService, Mockito.times(1)).findByUserName("admin");
        Mockito.verifyNoMoreInteractions(userService);
    }
}