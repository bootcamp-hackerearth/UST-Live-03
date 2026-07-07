package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;
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

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setIdentifier("USR-001");
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setStatus(true);
        user.setDeleted(false);
        user.setRoles(new ArrayList<>(Collections.singletonList("ROLE_USER")));

        userDto = new UserDto();
        userDto.setIdentifier("USR-001");
        userDto.setUsername("testuser");
        userDto.setPassword("rawPassword");
        userDto.setRoles(Collections.singletonList("ROLE_USER"));
    }

    @Test
    void testFindByUserName_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserDto result = userService.findByUserName("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindByUserName_ThrowsResourceNotFoundException() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> userService.findByUserName("testuser"));
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindByIdentifier_Success() {
        when(userRepository.findByIdentifier("USR-001")).thenReturn(user);

        UserDto result = userService.findByIdentifier("USR-001");

        assertNotNull(result);
        assertEquals("USR-001", result.getIdentifier());
        verify(userRepository, times(1)).findByIdentifier("USR-001");
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        when(userRepository.findByIdentifier("USR-001")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> userService.findByIdentifier("USR-001"));
        verify(userRepository, times(1)).findByIdentifier("USR-001");
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
    void testSave_WhenUserAlreadyExistsAndNotDeleted() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserDto result = userService.save(userDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSave_WhenUserAlreadyExistsButDeleted() {
        user.setDeleted(true);
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserDto result = userService.save(userDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSave_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.save(userDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("User created successfully", result.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdate_WhenUserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        UserDto result = userService.update(userDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdate_WhenUserDeleted() {
        user.setDeleted(true);
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserDto result = userService.update(userDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdate_SuccessWithNewRolesAndNewPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.encode("rawPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(userDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("User updated successfully", result.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdate_SuccessWithEmptyDtoRolesRetainsExistingRoles() {
        userDto.setRoles(null);
        userDto.setPassword(null);
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.update(userDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDelete_WhenUserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        userService.delete("testuser");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDelete_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.delete("testuser");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        when(userRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<UserDto> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
        verify(userRepository, times(1)).findByDeletedFalse(pageable);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        Specification<User> spec = mock(Specification.class);
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        WsDto<UserDto> result = userService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
        assertFalse(result.getDtoList().isEmpty());
        verify(userRepository, times(1)).findAll(spec, pageable);
    }

    @Test
    void testToggleStatus_WhenUserNotFound() {
        when(userRepository.findByIdentifier("USR-001")).thenReturn(null);

        UserDto result = userService.toggleStatus("USR-001");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testToggleStatus_Success() {
        when(userRepository.findByIdentifier("USR-001")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.toggleStatus("USR-001");

        assertNotNull(result);
        assertFalse(result.isStatus());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testFindIfTrue() {
        when(userRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(Collections.singletonList(user));

        List<UserDto> result = userService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(userRepository, times(1)).findByStatusIsTrueAndDeletedFalse();
    }
}