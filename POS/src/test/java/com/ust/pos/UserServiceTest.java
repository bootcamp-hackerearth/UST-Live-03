package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void saveTestSuccess() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");
        userDto.setPassword("password");
        User user = new User();
        user.setUsername("admin@test.com");

        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(null);
        Mockito.when(modelMapper.map(userDto, User.class)).thenReturn(user);
        Mockito.when(passwordEncoder.encode("password")).thenReturn("encodedPwd");
        UserDto response = userService.save(userDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void saveTestFailure_UserAlreadyExists() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");
        User existing = new User();
        existing.setUsername("admin@test.com");
        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(existing);
        UserDto response = userService.save(userDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "admin@test.com already exists", response.getMessage());
    }

    @Test
    void findByUsernameTest() {
        User user = new User();
        user.setUsername("admin@test.com");
        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");

        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        UserDto response = userService.findByUserName("admin@test.com");
        Assertions.assertEquals("admin@test.com", response.getUsername());
    }

    @Test
    void updateTestSuccess() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("admin@test.com");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("admin@test.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(existingUser)).thenReturn(existingUser);
        UserDto response = userService.update(userDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure_UserNotFound() {
        UserDto userDto = new UserDto();
        userDto.setId(99L);
        userDto.setUsername("admin@test.com");
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());
        UserDto response = userService.update(userDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "admin@test.com not found", response.getMessage());
    }

    @Test
    void updateTestFailure_DuplicateUsername() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("new@test.com");
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("old@test.com");
        User anotherUser = new User();
        anotherUser.setUsername("new@test.com");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByUsername("new@test.com")).thenReturn(anotherUser);
        UserDto response = userService.update(userDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "new@test.com already exists", response.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(userRepository).deleteByUsername("admin@test.com");
        userService.delete("admin@test.com");
        Mockito.verify(userRepository, Mockito.times(1)).deleteByUsername("admin@test.com");
    }

    @Test
    void findAllTest() {
        User user = new User();
        user.setUsername("admin@test.com");
        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(users),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(userDtos);
        List<UserDto> response = userService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("admin@test.com", response.get(0).getUsername());
    }
}