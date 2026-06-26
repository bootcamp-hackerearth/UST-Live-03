package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("pass");
        user.setStatus(true);
        user.setDeleted(false);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testUser");
        userDto.setPassword("pass");
    }

    // ✅ FIND BY USERNAME
    @Test
    void testFindByUserName() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.findByUserName("testUser");

        assertNotNull(result);
    }

    // ✅ SAVE - NEW USER
    @Test
    void testSave_NewUser() {
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        doNothing().when(userService).setAuditFields(user, true);

        UserDto result = userService.save(userDto);

        assertNotNull(result);
        verify(passwordEncoder).encode("pass");
        verify(userRepository).save(user);
    }

    // ✅ SAVE - ALREADY EXISTS
    @Test
    void testSave_AlreadyExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        UserDto result = userService.save(userDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ SAVE - SOFT DELETED
    @Test
    void testSave_SoftDeleted() {
        user.setDeleted(true);

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        UserDto result = userService.save(userDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    // ✅ UPDATE - SUCCESS
    @Test
    void testUpdate_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // FIX: Dropped the useless eq(...) wrappers while preserving your behavior stubbing
        doNothing().when(modelMapper).map(userDto, user);
        doNothing().when(userService).setAuditFields(user, false);

        // Act
        UserDto result = userService.update(userDto);

        // Assert
        assertNotNull(result);

        verify(modelMapper).map(userDto, user);
        verify(userService).setAuditFields(user, false);
        verify(userRepository).save(user);
    }


    // ✅ UPDATE - USER NOT FOUND
    @Test
    void testUpdate_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    // ✅ UPDATE - USERNAME ALREADY EXISTS
    @Test
    void testUpdate_UsernameExists() {
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");

        userDto.setUsername("newUser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("newUser")).thenReturn(anotherUser);

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ DELETE (SOFT DELETE)
    @Test
    void testDelete() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        doNothing().when(userService).softDelete(user);
        doNothing().when(userService).setAuditFields(user, false);

        userService.delete("testUser");

        verify(userRepository).save(user);
    }

    // ✅ CHANGE TOGGLE STATUS
    @Test
    void testChangeToggleStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.changeToggleStatus(1L, false);

        assertNotNull(result);
        assertFalse(user.isStatus());
        verify(userRepository).save(user);
    }

    // ✅ FIND ACTIVE STATUS
    @Test
    void testFindActiveStatus() {
        user.setStatus(true);

        User inactive = new User();
        inactive.setStatus(false);

        List<User> users = List.of(user, inactive);

        when(userRepository.findAll()).thenReturn(users);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(userDto));

        List<UserDto> result = userService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ✅ FIND ALL (Pagination)
    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(userDto));

        WsDto<UserDto> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }
}