package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.modell.User;
import com.ust.pos.modell.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByUserName_UserExists() {
        User user = new User();
        user.setUsername("test");
        UserDto dto = new UserDto();
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);
        UserDto result = userService.findByUserName("test");
        assertNotNull(result);
        verify(userRepository).findByUsername("test");
    }

    @Test
    void testFindByUserName_UserNotFound() {
        when(userRepository.findByUsername("test")).thenReturn(null);
        UserDto result = userService.findByUserName("test");
        assertNull(result);
    }

    @Test
    void testSave_UserAlreadyExists() {
        UserDto dto = new UserDto();
        dto.setUsername("test");
        when(userRepository.findByUsername("test")).thenReturn(new User());
        UserDto result = userService.save(dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_Success() {
        UserDto dto = new UserDto();
        dto.setUsername("test");
        dto.setPassword("plain");
        User user = new User();
        when(userRepository.findByUsername("test")).thenReturn(null);
        when(modelMapper.map(dto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        UserDto result = userService.save(dto);
        assertTrue(result.isSuccess());
        assertEquals("User created successfully", result.getMessage());
        verify(userRepository).save(user);
        assertEquals("encoded", user.getPassword());
    }

    @Test
    void testUpdate_UserNotFound() {
        UserDto dto = new UserDto();
        dto.setUsername("new");
        when(userRepository.findByUsername("old")).thenReturn(null);
        UserDto result = userService.update("old", dto);
        assertFalse(result.isSuccess());
        assertEquals("User not found", result.getMessage());
    }

    @Test
    void testUpdate_UsernameAlreadyExists() {
        User existingUser = new User();
        UserDto dto = new UserDto();
        dto.setUsername("new");
        when(userRepository.findByUsername("old")).thenReturn(existingUser);
        when(userRepository.findByUsername("new")).thenReturn(new User());
        UserDto result = userService.update("old", dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testUpdate_Success_SameUsername() {
        User user = new User();
        user.setUsername("test");
        UserDto dto = new UserDto();
        dto.setUsername("test");
        dto.setName("Updated");
        dto.setPhoneNo("123");
        when(userRepository.findByUsername("test")).thenReturn(user);
        UserDto result = userService.update("test", dto);
        assertTrue(result.isSuccess());
        assertEquals("User updated successfully", result.getMessage());
        verify(userRepository).save(user);
        assertEquals("Updated", user.getName());
    }

    @Test
    void testUpdate_Success_NewUsername() {
        User user = new User();
        UserDto dto = new UserDto();
        dto.setUsername("newUser");
        when(userRepository.findByUsername("oldUser")).thenReturn(user);
        when(userRepository.findByUsername("newUser")).thenReturn(null);
        UserDto result = userService.update("oldUser", dto);
        assertTrue(result.isSuccess());
        assertEquals("User updated successfully", result.getMessage());
        verify(userRepository).save(user);
        assertEquals("newUser", user.getUsername());
    }

    @Test
    void testDelete() {
        userService.delete("testUser");
        verify(userRepository, times(1)).deleteByUsername("testUser");
    }

    @Test
    void findAllTest() {
        User user = new User();
        user.setIdentifier("Admin");
        UserDto userDto = new UserDto();
        userDto.setIdentifier("Admin");
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 2), users.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(Mockito.eq(users), Mockito.any(java.lang.reflect.Type.class))).thenReturn(userDtos);
        List<UserDto> response = userService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }
}