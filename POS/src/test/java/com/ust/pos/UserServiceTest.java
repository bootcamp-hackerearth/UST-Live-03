package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
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
import java.util.List;
import java.util.Optional;

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

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("test@ust.com");
        userDto.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("test@ust.com");
        user.setPassword("encodedPassword123");
        user.setStatus(true);
        user.setDeleted(false);
    }

    @Test
    @DisplayName("Find By Username - Success")
    void findByUserName_Success() {
        when(userRepository.findByUsername("test@ust.com")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.findByUserName("test@ust.com");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("test@ust.com", result.getUsername());
    }

    @Test
    @DisplayName("Find By Username - Not Found")
    void findByUserName_NotFound() {
        when(userRepository.findByUsername("test@ust.com")).thenReturn(null);

        UserDto result = userService.findByUserName("test@ust.com");

        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("Save User - Success")
    void save_Success() {
        when(userRepository.findByUsername("test@ust.com")).thenReturn(null);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        UserDto result = userService.save(userDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("User registered successfully", result.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Save User - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        user.setDeleted(false);
        when(userRepository.findByUsername("test@ust.com")).thenReturn(user);

        UserDto result = userService.save(userDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Email already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Save User - Failure: Previously Deleted")
    void save_Failure_PreviouslyDeleted() {
        user.setDeleted(true);
        when(userRepository.findByUsername("test@ust.com")).thenReturn(user);

        UserDto result = userService.save(userDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Find All Users - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findByDeletedFalse(pageable)).thenReturn(userPage);
        when(modelMapper.map(eq(userPage.getContent()), any(Type.class))).thenReturn(List.of(userDto));

        WsDto<UserDto> result = userService.findAll(pageable);

        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertFalse(result.getDtoList().isEmpty());
    }

    @Test
    @DisplayName("Update User - Success")
    void update_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.update(userDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("User updated successfully", result.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Update User - Failure: Not Found")
    void update_Failure_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = userService.update(userDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Update User - Failure: Target User Soft Deleted")
    void update_Failure_TargetUserDeleted() {
        user.setDeleted(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.update(userDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("was previously deleted"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Update User - Failure: Email Conflict With Another User")
    void update_Failure_EmailConflict() {
        userDto.setUsername("newemail@ust.com");
        User conflictingUser = new User();
        conflictingUser.setUsername("newemail@ust.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("newemail@ust.com")).thenReturn(conflictingUser);

        UserDto result = userService.update(userDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(userRepository.findByUsername("test@ust.com")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.toggleStatus("test@ust.com");

        Assertions.assertFalse(user.isStatus());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Delete User - Success")
    void delete_Success() {
        when(userRepository.findByUsername("test@ust.com")).thenReturn(user);

        boolean result = userService.delete("test@ust.com");

        Assertions.assertTrue(result);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Delete User - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(userRepository.findByUsername("test@ust.com")).thenReturn(null);

        boolean result = userService.delete("test@ust.com");

        Assertions.assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }
}