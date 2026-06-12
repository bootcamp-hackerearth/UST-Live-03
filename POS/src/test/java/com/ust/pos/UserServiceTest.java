package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
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
    @DisplayName("Find By Username - Success")
    void findByUserName_Success() {
        when(userRepository.findByUsername("test@example.com")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.findByUserName("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
    }

    @Test
    @DisplayName("Find By Username - Not Found")
    void findByUserName_NotFound() {
        when(userRepository.findByUsername("none")).thenReturn(null);
        assertNull(userService.findByUserName("none"));
    }

    @Test
    @DisplayName("Save User - Success")
    void save_Success() {
        when(userRepository.findByUsername("test@example.com")).thenReturn(null);
        when(modelMapper.map(any(UserDto.class), eq(User.class))).thenReturn(user);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encoded");

        UserDto result = userService.save(userDto);

        assertTrue(result.isSuccess());
        assertEquals("User registered successfully", result.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Save User - Failure: Email Already Exists")
    void save_AlreadyExists() {
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(user);

        UserDto result = userService.save(userDto);

        assertFalse(result.isSuccess());
        assertEquals("Sorry! That Email already exists.", result.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update User - Failure: User Not Found")
    void update_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertEquals("User with username/email - test@example.com not found", result.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update User - Success: Same Username")
    void update_Success_SameUsername() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.update(userDto);

        assertTrue(result.isSuccess());
        assertEquals("User updated successfully", result.getMessage());
        verify(modelMapper).map(userDto, user);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Update User - Success: Changed Username")
    void update_Success_ChangedUsername() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("old@example.com");

        userDto.setUsername("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("new@example.com")).thenReturn(null);

        UserDto result = userService.update(userDto);

        assertTrue(result.isSuccess());
        assertEquals("User updated successfully", result.getMessage());
        verify(modelMapper).map(userDto, existingUser);
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Update User - Failure: Duplicate Username Conflict")
    void update_Failure_DuplicateUsernameConflict() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("old@example.com");

        userDto.setUsername("other@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("other@example.com")).thenReturn(new User());

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertEquals("User with username/email - other@example.com already exists", result.getMessage());
        verify(userRepository, never()).save(existingUser);
    }

    @Test
    @DisplayName("Find All - Paginated Success with WsDto Mapping")
    void findAll_Paginated() {
        Pageable pageable = PageRequest.of(1, 10);
        List<User> userList = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(userList, pageable, 25);
        List<UserDto> dtoList = Collections.singletonList(userDto);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(modelMapper.map(eq(userList), any(Type.class))).thenReturn(dtoList);

        WsDto<UserDto> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(dtoList, result.getDtoList());
        assertEquals(25, result.getTotalRecords());
        assertEquals(3, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(1, result.getPage());
        verify(userRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Toggle Status - Logic Flip")
    void toggleStatus_Logic() {
        user.setStatus(true);
        when(userRepository.findByUsername("test@example.com")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.toggleStatus("test@example.com");

        assertFalse(user.isStatus());
        verify(userRepository).save(user);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Delete User - Success")
    void delete_Success() {
        userService.delete("test@example.com");
        verify(userRepository).deleteByUsername("test@example.com");
    }
}