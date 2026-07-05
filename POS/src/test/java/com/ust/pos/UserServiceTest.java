package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;
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
        userDto.setUsername("john.doe@example.com");
        userDto.setPassword("plainPassword");

        user = new User();
        user.setId(1L);
        user.setUsername("john.doe@example.com");
        user.setPassword("encodedPassword");
        user.setStatus(true);
        user.setDeleted(false);
    }

    @Test
    @DisplayName("Find By Username - Success")
    void findByUserName_Success() {
        when(userRepository.findByUsername("john.doe@example.com")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.findByUserName("john.doe@example.com");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("john.doe@example.com", result.getUsername());
    }

    @Test
    @DisplayName("Find By Username - Failure: Not Found Exception")
    void findByUserName_Failure_NotFound() {
        when(userRepository.findByUsername("john.doe@example.com")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.findByUserName("john.doe@example.com"));
    }

    @Test
    @DisplayName("Save User - Success")
    void save_Success() {
        when(userRepository.findByUsername("john.doe@example.com")).thenReturn(null);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        UserDto result = userService.save(userDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("User registered successfully", result.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Save User - Failure: Email Already Exists")
    void save_Failure_AlreadyExists() {
        user.setDeleted(false);
        when(userRepository.findByUsername("john.doe@example.com")).thenReturn(user);

        UserDto result = userService.save(userDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("Email already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Save User - Failure: Previously Soft-Deleted")
    void save_Failure_PreviouslyDeleted() {
        user.setDeleted(true);
        when(userRepository.findByUsername("john.doe@example.com")).thenReturn(user);

        UserDto result = userService.save(userDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("previously deleted"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Find All Users - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findByDeletedFalse(pageable)).thenReturn(userPage);
        when(modelMapper.map(eq(userPage.getContent()), any(Type.class))).thenReturn(List.of(userDto));

        WsDto<UserDto> result = userService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    @DisplayName("Find All Users with Specification - Success")
    void findAll_WithSpecification_Success() {
        Specification<User> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(spec, pageable)).thenReturn(userPage);
        when(modelMapper.map(eq(userPage.getContent()), any(Type.class))).thenReturn(List.of(userDto));

        WsDto<UserDto> result = userService.findAll(spec, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
    }

    @Test
    @DisplayName("Update User - Success Same Username")
    void update_Success_SameUsername() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.update(userDto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals("User updated successfully", result.getMessage());
        verify(userRepository).save(user);
        verify(modelMapper).map(userDto, user);
    }

    @Test
    @DisplayName("Update User - Success Changed Username")
    void update_Success_ChangedUsername() {
        userDto.setUsername("new.email@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("new.email@example.com")).thenReturn(null);

        UserDto result = userService.update(userDto);

        Assertions.assertTrue(result.isSuccess());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Update User - Failure: User Not Found")
    void update_Failure_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = userService.update(userDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Update User - Failure: Already Soft-Deleted")
    void update_Failure_DeletedUser() {
        user.setDeleted(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.update(userDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("previously deleted"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Update User - Failure: New Username Conflict")
    void update_Failure_UsernameConflict() {
        userDto.setUsername("conflict@example.com");
        User conflictingUser = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("conflict@example.com")).thenReturn(conflictingUser);

        UserDto result = userService.update(userDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(userRepository.findByUsername("john.doe@example.com")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.toggleStatus("john.doe@example.com");

        Assertions.assertFalse(user.isStatus());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Delete User - Success")
    void delete_Success() {
        when(userRepository.findByUsername("john.doe@example.com")).thenReturn(user);

        boolean result = userService.delete("john.doe@example.com");

        Assertions.assertTrue(result);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Delete User - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(userRepository.findByUsername("john.doe@example.com")).thenReturn(null);

        boolean result = userService.delete("john.doe@example.com");

        Assertions.assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }
}