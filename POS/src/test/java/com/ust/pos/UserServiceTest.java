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
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void findByUserNameSuccessTest() {
        User user = new User();
        user.setUsername("user1");

        UserDto dto = new UserDto();
        dto.setUsername("user1");

        when(userRepository.findByUsername("user1")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto result = userService.findByUserName("user1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("user1", result.getUsername());
    }

    @Test
    void findByUserNameFailureTest() {
        when(userRepository.findByUsername("user1")).thenReturn(null);

        UserDto result = userService.findByUserName("user1");

        Assertions.assertNull(result);
    }

    @Test
    void saveTestSuccess() {
        UserDto dto = new UserDto();
        dto.setUsername("user1");
        dto.setPassword("pass");

        User user = new User();

        when(userRepository.findByUsername("user1")).thenReturn(null);
        when(modelMapper.map(dto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        UserDto result = userService.save(dto);

        verify(userRepository).save(user);
        Assertions.assertEquals("user1", result.getUsername());
    }

    @Test
    void saveTestFailure() {
        UserDto dto = new UserDto();
        dto.setUsername("user1");

        User existing = new User();
        existing.setUsername("user1");

        when(userRepository.findByUsername("user1")).thenReturn(existing);

        UserDto result = userService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateTestIdNotFound() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("user1");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateTestDuplicateUsername() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("user2");

        User existing = new User();
        existing.setUsername("user1");

        User duplicate = new User();
        duplicate.setUsername("user2");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("user2")).thenReturn(duplicate);

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("user1");

        User existing = new User();
        existing.setUsername("user1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        UserDto result = userService.update(dto);

        Assertions.assertEquals("user1", result.getUsername());
        verify(modelMapper).map(dto, existing);
        verify(userRepository).save(existing);
    }

    @Test
    void deleteTest() {
        userService.delete("user1");
        verify(userRepository).deleteByUsername("user1");
    }

    @Test
    void findAllTest() {
        Pageable pageable = mock(Pageable.class);
        Page<User> page = mock(Page.class);

        List<User> users = List.of(new User(), new User());
        List<UserDto> dtoList = List.of(new UserDto(), new UserDto());

        when(userRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(users);
        when(modelMapper.map(eq(users), any(Type.class))).thenReturn(dtoList);

        List<UserDto> result = userService.findAll(pageable);

        Assertions.assertEquals(2, result.size());

        verify(userRepository).findAll(pageable);
        verify(page).getContent();
        verify(modelMapper).map(eq(users), any(Type.class));
    }
}