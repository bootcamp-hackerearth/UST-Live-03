package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByUserNameSuccessTest() {
        User user = new User();
        user.setUsername("admin");

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);

        UserDto result = userService.findByUserName("admin");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("admin", result.getUsername());
    }

    @Test
    void findByUserNameFailureTest() {
        when(userRepository.findByUsername("admin"))
                .thenReturn(null);

        UserDto result = userService.findByUserName("admin");

        Assertions.assertNull(result);
    }

    @Test
    void saveSuccessTest() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setPassword("password");

        User user = new User();

        when(userRepository.findByUsername("admin"))
                .thenReturn(null);

        when(modelMapper.map(dto, User.class))
                .thenReturn(user);

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        UserDto result = userService.save(dto);

        Assertions.assertEquals("admin", result.getUsername());

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);
    }

    @Test
    void saveAlreadyExistsTest() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");

        User existingUser = new User();
        existingUser.setDeleted(false);

        when(userRepository.findByUsername("admin"))
                .thenReturn(existingUser);

        UserDto result = userService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "User with username/email - admin already exists",
                result.getMessage()
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void saveDeletedUserTest() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");

        User existingUser = new User();
        existingUser.setDeleted(true);

        when(userRepository.findByUsername("admin"))
                .thenReturn(existingUser);

        UserDto result = userService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "User with username/email - admin was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserNotFoundTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "User with username/email - admin not found",
                result.getMessage()
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateDuplicateUsernameTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newuser");

        User existingUser = new User();
        existingUser.setUsername("olduser");

        User duplicateUser = new User();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.findByUsername("newuser"))
                .thenReturn(duplicateUser);

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "User with username/email - newuser already exists",
                result.getMessage()
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateSuccessSameUsernameTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        User existingUser = new User();
        existingUser.setUsername("admin");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        UserDto result = userService.update(dto);

        Assertions.assertEquals("admin", result.getUsername());

        verify(modelMapper).map(dto, existingUser);
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateSuccessUniqueUsernameTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newuser");

        User existingUser = new User();
        existingUser.setUsername("olduser");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.findByUsername("newuser"))
                .thenReturn(null);

        UserDto result = userService.update(dto);

        Assertions.assertEquals("newuser", result.getUsername());

        verify(modelMapper).map(dto, existingUser);
        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteTest() {
        User user = new User();

        when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        userService.delete("admin");

        verify(userRepository).findByUsername("admin");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<User> users = List.of(
                new User(),
                new User()
        );

        Page<User> page = new PageImpl<>(users, pageable, 2);

        List<UserDto> dtoList = List.of(
                new UserDto(),
                new UserDto()
        );

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();

        when(userRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(users, listType))
                .thenReturn(dtoList);

        WsDto<UserDto> result = userService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }
}