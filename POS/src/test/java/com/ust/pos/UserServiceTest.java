package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");
        user.setPassword("encodedPassword");
        user.setStatus(true);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("test@example.com");
        userDto.setPassword("rawPassword");
    }

    @Test
    void findByUserName_Success() {
        when(userRepository.findByUsername("test@example.com")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.findByUserName("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
    }

    @Test
    void findByUserName_NotFound() {
        when(userRepository.findByUsername("none")).thenReturn(null);
        assertNull(userService.findByUserName("none"));
    }

    @Test
    void save_Success() {
        when(userRepository.findByUsername("test@example.com")).thenReturn(null);
        when(modelMapper.map(any(UserDto.class), eq(User.class))).thenReturn(user);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encoded");

        UserDto result = userService.save(userDto);

        assertTrue(result.isSuccess());
        verify(userRepository).save(user);
    }

    @Test
    void save_AlreadyExists() {
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(user);

        UserDto result = userService.save(userDto);

        assertFalse(result.isSuccess());
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserDto result = userService.update(userDto);
        assertFalse(result.isSuccess());
    }

    @Test
    void update_Success_SameUsername() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.update(userDto);

        assertTrue(result.isSuccess());
        verify(userRepository).save(user);
    }

    @Test
    void update_Success_ChangedUsername() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("old@example.com");

        userDto.setUsername("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("new@example.com")).thenReturn(null);

        UserDto result = userService.update(userDto);

        assertTrue(result.isSuccess());
        verify(userRepository).save(existingUser);
    }

    @Test
    void update_Failure_DuplicateUsernameConflict() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("old@example.com");

        userDto.setUsername("other@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("other@example.com")).thenReturn(new User());

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        verify(userRepository, never()).save(existingUser);
    }


    @Test
    @DisplayName("Find All - Paginated")
    void findAll_Paginated() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> userList = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(userList);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(modelMapper.map(eq(userList), any(Type.class))).thenReturn(Collections.singletonList(userDto));

        List<UserDto> result = userService.findAll(pageable);

        assertEquals(1, result.size());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void toggleStatus_Logic() {
        user.setStatus(true);
        when(userRepository.findByUsername("test@example.com")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        userService.toggleStatus("test@example.com");

        assertFalse(user.isStatus());
        verify(userRepository).save(user);
    }

    @Test
    void delete_Success() {
        userService.delete("test@example.com");
        verify(userRepository).deleteByUsername("test@example.com");
    }
}