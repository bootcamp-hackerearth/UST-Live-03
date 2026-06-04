package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness =
                           org.mockito.quality.Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    void saveTest() {
        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");
        userDto.setPassword("srujan");

        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(null);
        User user = new User();
        Mockito.when(modelMapper.map(userDto, User.class)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        UserDto response = userService.save(userDto);

        Assertions.assertEquals("Admin", response.getUsername());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");

        User existingUser = new User();
        existingUser.setUsername("Admin");

        Mockito.when(userRepository.findByUsername("Admin"))
                .thenReturn(existingUser);

        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByUserNameTest() {
        User user = new User();
        user.setIdentifier("Admin");

        UserDto userDto = new UserDto();
        userDto.setIdentifier("Admin");

        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.findByUserName("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }
    @Test
    void updateTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("admin@test.com");

        User user = new User();
        user.setUsername("admin@test.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto response = userService.update(userDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestNotFound() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("admin@test.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestDuplicateUsername() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("new@test.com");

        User existingUser = new User();
        existingUser.setUsername("old@test.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByUsername("new@test.com")).thenReturn(new User());

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        User user = new User();
        user.setUsername("other@test.com");

        UserDto userDto = new UserDto();
        userDto.setUsername("other@test.com");

        Authentication authentication = new UsernamePasswordAuthenticationToken("admin@test.com", null, List.of());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(userRepository.findByUsername("other@test.com")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.delete("other@test.com");

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void deleteTestSameUser() {
        User user = new User();
        user.setUsername("admin@test.com");

        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");

        Authentication authentication = new UsernamePasswordAuthenticationToken("admin@test.com", null, List.of());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.delete("admin@test.com");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cannot delete the logged in User", response.getMessage());
    }

    @Test
    void deleteTestNoAuthentication() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        UserDto response = userService.delete("admin@test.com");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {
        User user = new User();
        user.setIdentifier("Admin");

        UserDto userDto = new UserDto();
        userDto.setIdentifier("Admin");

        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0,
                2), users.size());

        Pageable pageable = PageRequest.of(0,
                50, Sort.by(new ArrayList<>()));

        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(users),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(userDtos);

        List<UserDto> response = userService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findByStatusTest() {
        User user = new User();
        user.setIdentifier("Admin");
        UserDto userDto = new UserDto();
        userDto.setIdentifier("Admin");

        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        Mockito.when(userRepository.findByStatusIsTrue()).thenReturn(users);
        Mockito.when(modelMapper.map(
                Mockito.eq(users),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(userDtos);

        List<UserDto> response = userService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleTestActive() {

        User user = new User();
        user.setStatus(false);
        UserDto userDto = new UserDto();
        userDto.setStatus(true);
        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        UserDto response = userService.toggleStatus("Admin");
        Assertions.assertTrue(response.isStatus());

    }

    @Test
    void toggleTestInactive() {

        User user = new User();
        user.setStatus(true);
        UserDto userDto = new UserDto();
        userDto.setStatus(false);
        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        UserDto response = userService.toggleStatus("Admin");
        Assertions.assertFalse(response.isStatus());
    }
}