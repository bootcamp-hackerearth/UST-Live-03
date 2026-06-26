package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.User;
import com.ust.pos.modell.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.*;

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
        UserDto dto = new UserDto();
        when(userRepository.findByUsernameAndDeletedFalse("test")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);
        UserDto result = userService.findByUserName("test");
        assertNotNull(result);
        verify(userRepository).findByUsernameAndDeletedFalse("test");
    }

    @Test
    void testFindByUserName_UserNotFound() {
        when(userRepository.findByUsernameAndDeletedFalse("test")).thenReturn(null);
        UserDto result = userService.findByUserName("test");
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
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSave_UserDeletedExists() {
        UserDto dto = new UserDto();
        dto.setUsername("test");
        User deleted = new User();
        deleted.setDeleted(true);
        when(userRepository.findByUsername("test")).thenReturn(deleted);
        UserDto result = userService.save(dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("was deleted"));
        verify(userRepository, never()).save(any());
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
        assertEquals("test", user.getIdentifier());
    }

    @Test
    void testUpdate_UserNotFound() {
        when(userRepository.findByUsernameAndDeletedFalse("old")).thenReturn(null);
        UserDto dto = new UserDto();
        UserDto result = userService.update("old", dto);
        assertFalse(result.isSuccess());
        assertEquals("User not found", result.getMessage());
    }

    @Test
    void testUpdate_UsernameAlreadyExists() {
        User existing = new User();
        when(userRepository.findByUsernameAndDeletedFalse("old")).thenReturn(existing);
        when(userRepository.findByUsername("new")).thenReturn(new User());
        UserDto dto = new UserDto();
        dto.setUsername("new");
        UserDto result = userService.update("old", dto);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdate_SameUsername() {
        User user = new User();
        user.setUsername("test");
        when(userRepository.findByUsernameAndDeletedFalse("test")).thenReturn(user);
        UserDto dto = new UserDto();
        dto.setUsername("test");
        dto.setName("Updated");
        dto.setPhoneNo("123");
        UserDto result = userService.update("test", dto);
        assertTrue(result.isSuccess());
        assertEquals("User updated successfully", result.getMessage());
        verify(userRepository).save(user);
        assertEquals("Updated", user.getName());
    }

    @Test
    void testUpdate_NewUsernameSuccess() {
        User user = new User();
        when(userRepository.findByUsernameAndDeletedFalse("old")).thenReturn(user);
        when(userRepository.findByUsername("new")).thenReturn(null);
        UserDto dto = new UserDto();
        dto.setUsername("new");
        UserDto result = userService.update("old", dto);
        assertTrue(result.isSuccess());
        assertEquals("User updated successfully", result.getMessage());
        verify(userRepository).save(user);
        assertEquals("new", user.getUsername());
    }

    @Test
    void testDelete_UserExists() {
        User user = new User();
        when(userRepository.findByUsernameAndDeletedFalse("test")).thenReturn(user);
        userService.delete("test");
        verify(userRepository).save(user);
    }

    @Test
    void testDelete_UserNotFound() {
        when(userRepository.findByUsernameAndDeletedFalse("test")).thenReturn(null);
        userService.delete("test");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 2);
        List<User> users = List.of(new User());
        Page<User> page = new PageImpl<>(users, pageable, 1);
        List<UserDto> dtoList = List.of(new UserDto());
        when(userRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(users), any(Type.class))).thenReturn(dtoList);
        WsDto<UserDto> result = userService.findAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
        verify(userRepository).findAllByDeletedFalse(pageable);
    }
}