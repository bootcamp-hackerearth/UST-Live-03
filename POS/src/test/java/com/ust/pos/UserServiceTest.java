package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private UserServiceImpl userService;

    private User userEntity;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userEntity = new User();
        userEntity.setId(1L);
        userEntity.setIdentifier("USR-100");
        userEntity.setUsername("john_doe");
        userEntity.setPassword("encodedPassword");
        userEntity.setStatus(true);
        userEntity.setDeleted(false);
        userEntity.setRoles(new ArrayList<>(Collections.singletonList("ROLE_USER")));

        userDto = new UserDto();
        userDto.setIdentifier("USR-100");
        userDto.setUsername("john_doe");
        userDto.setPassword("rawPassword");
        userDto.setRoles(Collections.singletonList("ROLE_USER"));
    }

    @Test
    void testFindByUserName() {
        when(userRepository.findByUsername("john_doe")).thenReturn(userEntity);

        UserDto result = userService.findByUserName("john_doe");

        assertNotNull(result);
        assertEquals("john_doe", result.getUsername());
    }

    @Test
    void testFindByIdentifier() {
        when(userRepository.findByIdentifier("USR-100")).thenReturn(userEntity);

        UserDto result = userService.findByIdentifier("USR-100");

        assertNotNull(result);
        assertEquals("USR-100", result.getIdentifier());
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> userService.save(null));
    }

    @Test
    void testSave_WhenUsernameIsNull() {
        userDto.setUsername(null);
        assertThrows(IllegalArgumentException.class, () -> userService.save(userDto));
    }

    @Test
    void testSave_WhenUserExistsAndNotDeleted() {
        when(userRepository.findByUsername("john_doe")).thenReturn(userEntity);

        UserDto result = userService.save(userDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenUserWasPreviouslyDeleted() {
        userEntity.setDeleted(true);
        when(userRepository.findByUsername("john_doe")).thenReturn(userEntity);

        UserDto result = userService.save(userDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(userRepository.findByUsername("john_doe")).thenReturn(null);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userEntity);

        UserDto result = userService.save(userDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("User created successfully", result.getMessage());
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdate_WhenUserNotFound() {
        when(userRepository.findByUsername("john_doe")).thenReturn(null);

        UserDto result = userService.update(userDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenUsergetDeleted() {
        userEntity.setDeleted(true);
        when(userRepository.findByUsername("john_doe")).thenReturn(userEntity);

        UserDto result = userService.update(userDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_SuccessWithNewPasswordAndRoles() {
        when(userRepository.findByUsername("john_doe")).thenReturn(userEntity);
        when(passwordEncoder.encode("rawPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userEntity);

        UserDto result = userService.update(userDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("User updated successfully", result.getMessage());
        verify(passwordEncoder, times(1)).encode("rawPassword");
    }

    @Test
    void testUpdate_SuccessWithEmptyPasswordAndEmptyRoles() {
        userDto.setPassword("");
        userDto.setRoles(null);
        userEntity.setRoles(new ArrayList<>(Collections.singletonList("ROLE_USER")));

        when(userRepository.findByUsername("john_doe")).thenReturn(userEntity);
        when(userRepository.save(any(User.class))).thenReturn(userEntity);

        UserDto result = userService.update(userDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testDelete_WhenUserNotFound() {
        when(userRepository.findByUsername("john_doe")).thenReturn(null);

        userService.delete("john_doe");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDelete_Success() {
        when(userRepository.findByUsername("john_doe")).thenReturn(userEntity);
        when(userRepository.save(any(User.class))).thenReturn(userEntity);

        userService.delete("john_doe");

        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> entityList = Collections.singletonList(userEntity);
        Page<User> page = new PageImpl<>(entityList, pageable, 1);

        when(userRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<UserDto> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_WhenUserNotFound() {
        when(userRepository.findByIdentifier("USR-100")).thenReturn(null);

        UserDto result = userService.toggleStatus("USR-100");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        userEntity.setStatus(true);
        when(userRepository.findByIdentifier("USR-100")).thenReturn(userEntity);
        when(userRepository.save(any(User.class))).thenReturn(userEntity);

        UserDto result = userService.toggleStatus("USR-100");

        assertNotNull(result);
        assertFalse(result.isStatus());
    }

    @Test
    void testFindIfTrue() {
        List<User> activeUsers = Collections.singletonList(userEntity);
        when(userRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(activeUsers);

        List<UserDto> result = userService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}