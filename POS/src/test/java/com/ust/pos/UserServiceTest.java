package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void find_success() {

        User user = new User();
        user.setUsername("admin");

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        when(userRepository.findByUsername("admin")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto response = userService.findByUserName("admin");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("admin", response.getUsername());
    }

    @Test
    void find_notFound() {

        when(userRepository.findByUsername("admin")).thenReturn(null);

        UserDto response = userService.findByUserName("admin");

        Assertions.assertNull(response);
    }

    @Test
    void find_deletedUser() {

        User user = new User();
        user.setDeleted(true);

        when(userRepository.findByUsername("admin")).thenReturn(user);

        UserDto response = userService.findByUserName("admin");

        Assertions.assertNull(response);
    }

    @Test
    void save_success() {

        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setPassword("123");

        User user = new User();

        when(userRepository.findByUsername("admin")).thenReturn(null);
        when(modelMapper.map(dto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("123")).thenReturn("encoded123");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto response = userService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("User created successfully", response.getMessage());

        verify(passwordEncoder).encode("123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void save_userAlreadyExists() {

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        when(userRepository.findByUsername("admin")).thenReturn(new User());

        UserDto response = userService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("User already exists"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void update_success() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        User existing = new User();
        existing.setId(1L);
        existing.setUsername("admin");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        UserDto response = userService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("User updated successfully", response.getMessage());

        verify(userRepository).save(existing);
    }

    @Test
    void update_userNotFound() {

        UserDto dto = new UserDto();
        dto.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.update(dto));

        Assertions.assertEquals("User not found", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void update_deletedUser() {

        UserDto dto = new UserDto();
        dto.setId(1L);

        User user = new User();
        user.setDeleted(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.update(dto));

        Assertions.assertEquals("User with id '1' is deleted", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void update_usernameAlreadyExists() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newuser");

        User existing = new User();
        existing.setId(1L);
        existing.setUsername("olduser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("newuser")).thenReturn(new User());

        UserDto response = userService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("Username already exists"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_success() {

        User user = new User();

        when(userRepository.findByUsername("admin")).thenReturn(user);

        userService.delete("admin");

        Assertions.assertTrue(user.getDeleted());

        verify(userRepository).save(user);
    }

    @Test
    void delete_userNotFound() {

        when(userRepository.findByUsername("admin")).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.delete("admin"));

        Assertions.assertEquals("User with username 'admin' not found", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void findAll_success() {

        User user = new User();
        user.setUsername("admin@test.com");

        UserDto dto = new UserDto();
        dto.setUsername("admin@test.com");

        List<User> users = List.of(user);
        List<UserDto> dtoList = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page = new PageImpl<>(users);

        when(userRepository.findByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(users), ArgumentMatchers.<Type>any())).thenReturn(dtoList);

        WsDto<UserDto> response = userService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("admin@test.com", response.getDtoList().get(0).getUsername());
    }

    @Test
    void findAll_withSpecification() {

        Specification<User> specification = (root, query, cb) -> cb.conjunction();

        User user = new User();
        user.setUsername("admin@test.com");

        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(eq(specification), any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any())).thenReturn(List.of(new UserDto()));

        WsDto<UserDto> response = userService.findAll(specification, PageRequest.of(0, 10));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
    }
}