package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.User;
import com.ust.pos.modell.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
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
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("User not found", result.getMessage());
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
        void testFindAll_withTypeTokenMapping() {
            Pageable pageable = PageRequest.of(0, 2);
            User user = new User();
            user.setId(1L);
            user.setName("Test User");
            List<User> userList = List.of(user);
            Page<User> userPage = new PageImpl<>(userList, pageable, 1);
            UserDto userDto = new UserDto();
            userDto.setName("Test User");
            List<UserDto> dtoList = List.of(userDto);
            when(userRepository.findAll(pageable)).thenReturn(userPage);
            when(modelMapper.map(eq(userList), any(Type.class))).thenReturn(dtoList);
            WsDto<UserDto> result = userService.findAll(pageable);
            assertNotNull(result);
            assertEquals(1, result.getDtoList().size());
            assertEquals("Test User", result.getDtoList().get(0).getName());
            assertEquals(1, result.getTotalRecords());
            assertEquals(1, result.getTotalPage());
            assertEquals(2, result.getSizePerPage());
            assertEquals(0, result.getPage());
            verify(userRepository, times(1)).findAll(pageable);
            verify(modelMapper, times(1)).map(eq(userList), any(Type.class));
        }
    }
