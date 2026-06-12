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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

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
    void findByUserName_Found() {

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
        Assertions.assertEquals("admin",
                result.getUsername());
    }

    @Test
    void findByUserName_NotFound() {

        when(userRepository.findByUsername("admin"))
                .thenReturn(null);

        UserDto result = userService.findByUserName("admin");

        Assertions.assertNull(result);
    }

    @Test
    void save_NewUser() {

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

        when(userRepository.save(user))
                .thenReturn(user);

        UserDto result = userService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("admin",
                result.getUsername());

        verify(passwordEncoder)
                .encode("password");

        Assertions.assertEquals(
                "encodedPassword",
                user.getPassword());

        verify(userRepository)
                .save(user);
    }

    @Test
    void save_UserAlreadyExists() {

        User existing = new User();

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        when(userRepository.findByUsername("admin"))
                .thenReturn(existing);

        UserDto result = userService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(userRepository, never())
                .save(any());

        verify(passwordEncoder, never())
                .encode(anyString());
    }

    @Test
    void update_UserNotFound() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(userRepository, never())
                .save(any());
    }

    @Test
    void update_UsernameAlreadyExists() {

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("admin");

        User anotherUser = new User();
        anotherUser.setUsername("manager");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("manager");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.findByUsername("manager"))
                .thenReturn(anotherUser);

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(userRepository, never())
                .save(any());
    }

    @Test
    void update_SameUsername() {

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("admin");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        UserDto result = userService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("admin",
                result.getUsername());

        verify(modelMapper)
                .map(dto, existingUser);

        verify(userRepository)
                .save(existingUser);
    }

    @Test
    void update_NewUniqueUsername() {

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("admin");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("manager");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.findByUsername("manager"))
                .thenReturn(null);

        UserDto result = userService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("manager",
                result.getUsername());

        verify(modelMapper)
                .map(dto, existingUser);

        verify(userRepository)
                .save(existingUser);
    }

    @Test
    void deleteTest() {

        doNothing().when(userRepository)
                .deleteByUsername("admin");

        userService.delete("admin");

        verify(userRepository)
                .deleteByUsername("admin");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<User> users =
                List.of(new User(), new User());

        Page<User> page =
                new PageImpl<>(
                        users,
                        pageable,
                        2
                );

        List<UserDto> dtoList =
                List.of(new UserDto(), new UserDto());

        when(userRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(users), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<UserDto> result =
                userService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertEquals(
                2,
                result.getContent().size());

        Assertions.assertEquals(
                0,
                result.getPage());

        Assertions.assertEquals(
                10,
                result.getSizePerPage());

        Assertions.assertEquals(
                1,
                result.getTotalPages());

        Assertions.assertEquals(
                2,
                result.getTotalRecords());

        verify(userRepository)
                .findAll(pageable);

        verify(modelMapper)
                .map(eq(users), any(Type.class));
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<User> emptyList = List.of();

        Page<User> page =
                new PageImpl<>(emptyList);

        when(userRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(emptyList), any(Type.class)))
                .thenReturn(List.of());

        WsDto<UserDto> result =
                userService.findAll(pageable);

        Assertions.assertNotNull(result);

        Assertions.assertTrue(
                result.getContent().isEmpty());

        Assertions.assertEquals(0,
                result.getTotalRecords());

        verify(userRepository)
                .findAll(pageable);
    }
}