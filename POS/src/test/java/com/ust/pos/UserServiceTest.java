package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import static org.junit.jupiter.api.Assertions.*;
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
    void findByUserNameSuccess() {

        User user = new User();
        user.setUsername("admin");

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);

        UserDto response = userService.findByUserName("admin");

        assertNotNull(response);
        assertEquals("admin", response.getUsername());
    }

    @Test
    void findByUserNameFailure() {

        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(null);

        UserDto response = userService.findByUserName("admin");

        Assertions.assertNull(response);
    }

    @Test
    void saveTestSuccess() {

        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setPassword("plain");

        User user = new User();

        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, User.class))
                .thenReturn(user);

        Mockito.when(passwordEncoder.encode("plain"))
                .thenReturn("encoded");

        UserDto response = userService.save(dto);

        assertEquals("admin", response.getUsername());
        assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(userRepository).save(user);
        verify(passwordEncoder).encode("plain");
    }

    @Test
    void saveTestDuplicate() {

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(new User());

        UserDto response = userService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
    }

    @Test
    void updateTestNotFound() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserDto response = userService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
    }

    @Test
    void updateTestUsernameConflict() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newUser");

        User existing = new User();
        existing.setUsername("oldUser");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(userRepository.findByUsername("newUser"))
                .thenReturn(new User()); // conflict

        UserDto response = userService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
    }

    @Test
    void updateTestSuccess() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        User existing = new User();
        existing.setUsername("admin");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(userRepository.save(existing))
                .thenReturn(existing);

        UserDto response = userService.update(dto);

        assertEquals("admin", response.getUsername());
        assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(modelMapper).map(dto, existing);
        verify(userRepository).save(existing);
    }

    @Test
    void deleteTest() {

        userService.delete("admin");

        verify(userRepository).deleteByUsername("admin");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<User> users = List.of(new User());
        Page<User> page = new PageImpl<>(users);

        List<UserDto> dtos = List.of(new UserDto());

        when(userRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(users),
                any(Type.class)
        )).thenReturn(dtos);

        List<UserDto> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userRepository).findAll(pageable);
    }

    @Test
    void updateTest_usernameChanged_noDuplicate() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newUser");

        User existing = new User();
        existing.setUsername("oldUser");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(userRepository.findByUsername("newUser"))
                .thenReturn(null);

        UserDto response = userService.update(dto);

        assertTrue(response.isSuccess());

        verify(modelMapper).map(dto, existing);
        verify(userRepository).save(existing);
    }

    @Test
    void updateTest_usernameSame_duplicateExists_shortCircuit() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        User existing = new User();
        existing.setUsername("admin");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        UserDto response = userService.update(dto);

        assertTrue(response.isSuccess());

        verify(modelMapper).map(dto, existing);
        verify(userRepository).save(existing);

        verify(userRepository, never()).findByUsername(anyString());
    }

}