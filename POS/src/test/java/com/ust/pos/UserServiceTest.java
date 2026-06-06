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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;

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
    void findByUserNameSuccessTest() {
        User user = new User();
        user.setUsername("john");

        UserDto dto = new UserDto();
        dto.setUsername("john");

        Mockito.when(userRepository.findByUsername("john")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto result = userService.findByUserName("john");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("john", result.getUsername());
    }

    @Test
    void findByUserNameFailureTest() {
        Mockito.when(userRepository.findByUsername("john")).thenReturn(null);

        UserDto result = userService.findByUserName("john");

        Assertions.assertNull(result);
    }

    @Test
    void saveSuccessTest() {
        UserDto dto = new UserDto();
        dto.setUsername("john");
        dto.setPassword("pass");

        User user = new User();
        user.setUsername("john");

        Mockito.when(userRepository.findByUsername("john")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, User.class)).thenReturn(user);
        Mockito.when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        UserDto result = userService.save(dto);

        Assertions.assertEquals("john", result.getUsername());
        verify(passwordEncoder).encode("pass");
        verify(userRepository).save(user);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        User existing = new User();
        existing.setUsername("john");

        UserDto dto = new UserDto();
        dto.setUsername("john");

        Mockito.when(userRepository.findByUsername("john")).thenReturn(existing);

        UserDto result = userService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateFailureUserNotFoundTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("john");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void updateFailureUsernameAlreadyExistsTest() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("old");

        User duplicate = new User();
        duplicate.setUsername("new");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("new");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.findByUsername("new")).thenReturn(duplicate);

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void updateSuccessSameUsernameTest() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("john");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("john");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        UserDto result = userService.update(dto);

        Assertions.assertEquals("john", result.getUsername());
        verify(modelMapper).map(dto, existing);
        verify(userRepository).save(existing);
    }

    @Test
    void updateSuccessDifferentUsernameNotTakenTest() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("old");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newname");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.findByUsername("newname")).thenReturn(null);

        UserDto result = userService.update(dto);

        verify(modelMapper).map(dto, existing);
        verify(userRepository).save(existing);
        Assertions.assertEquals("newname", result.getUsername());
    }

    @Test
    void deleteTest() {
        userService.delete("john");
        verify(userRepository).deleteByUsername("john");
    }

    @Test
    void findAllTest() {
        List<User> users = Arrays.asList(new User(), new User());
        List<UserDto> dtoList = Arrays.asList(new UserDto(), new UserDto());

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(Mockito.eq(users), Mockito.any(Type.class))).thenReturn(dtoList);

        List<UserDto> result = userService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
        Mockito.verify(userRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(Mockito.eq(users), Mockito.any(Type.class));
    }

    @Test
    void findByIdentifierTest() {
        UserDto result = userService.findByIdentifier("any");
        Assertions.assertNull(result);
    }
}