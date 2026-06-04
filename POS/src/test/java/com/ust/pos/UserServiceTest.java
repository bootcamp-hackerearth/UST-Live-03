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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {
        UserDto dto = new UserDto();
        dto.setUsername("user1");
        dto.setPassword("pass");

        User user = new User();
        user.setUsername("user1");

        when(userRepository.findByUsername("user1")).thenReturn(null);
        when(modelMapper.map(dto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        UserDto response = userService.save(dto);

        assertEquals("user1", response.getUsername());
        Assertions.assertNull(response.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    void saveFailureTest() {
        User existing = new User();
        existing.setUsername("user1");

        UserDto dto = new UserDto();
        dto.setUsername("user1");

        when(userRepository.findByUsername("user1")).thenReturn(existing);

        UserDto response = userService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest_WhenUsernameSame() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("user1");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("user1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        UserDto response = userService.update(dto);

        assertEquals("user1", response.getUsername());
        verify(modelMapper).map(dto, existing);
        verify(userRepository).save(existing);
    }

    @Test
    void updateSuccessTest_WhenUsernameChangedAndNotExists() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("oldName");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newName");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("newName")).thenReturn(null);

        UserDto response = userService.update(dto);

        assertEquals("newName", response.getUsername());
        verify(modelMapper).map(dto, existing);
        verify(userRepository).save(existing);
    }

    @Test
    void updateFailureUserNotFoundTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("user1");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto response = userService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateFailureUsernameExistsTest() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("old");

        User another = new User();
        another.setUsername("new");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("new");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("new")).thenReturn(another);

        UserDto response = userService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        userService.delete("user1");
        verify(userRepository).deleteByUsername("user1");
    }

    @Test
    void findByUserNameSuccessTest() {
        User user = new User();
        user.setUsername("user1");

        UserDto dto = new UserDto();
        dto.setUsername("user1");

        when(userRepository.findByUsername("user1")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto response = userService.findByUserName("user1");

        assertNotNull(response);
        assertEquals("user1", response.getUsername());
    }

    @Test
    void findByUserNameFailureTest() {
        when(userRepository.findByUsername("user1")).thenReturn(null);

        UserDto response = userService.findByUserName("user1");

        Assertions.assertNull(response);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<User> users = List.of(new User(), new User());
        Page<User> page = new PageImpl<>(users);

        List<UserDto> dtoList = List.of(new UserDto(), new UserDto());

        when(userRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(users),
                any(java.lang.reflect.Type.class)
        )).thenReturn(dtoList);

        List<UserDto> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(userRepository).findAll(pageable);
    }
}