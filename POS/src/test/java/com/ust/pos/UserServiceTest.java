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
        user.setUsername("user1");

        UserDto dto = new UserDto();
        dto.setUsername("user1");

        when(userRepository.findByUsername("user1"))
                .thenReturn(user);

        when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);

        UserDto result = userService.findByUserName("user1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("user1", result.getUsername());
    }

    @Test
    void findByUserName_NotFound() {

        when(userRepository.findByUsername("user1"))
                .thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findByUserName("user1")
        );

        Assertions.assertEquals(
                "User with username 'user1' not found",
                exception.getMessage()
        );
    }

    @Test
    void save_NewUser() {

        UserDto dto = new UserDto();
        dto.setUsername("user1");
        dto.setPassword("pass");

        User user = new User();

        when(userRepository.findByUsername("user1"))
                .thenReturn(null);

        when(modelMapper.map(dto, User.class))
                .thenReturn(user);

        when(passwordEncoder.encode("pass"))
                .thenReturn("encoded");

        when(userRepository.save(user))
                .thenReturn(user);

        UserDto result = userService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("user1", result.getUsername());

        verify(passwordEncoder).encode("pass");
        verify(userRepository).save(user);
    }

    @Test
    void save_UserExists() {

        User existing = new User();

        UserDto dto = new UserDto();
        dto.setUsername("user1");

        when(userRepository.findByUsername("user1"))
                .thenReturn(existing);

        UserDto result = userService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_UserExists() {

        User existing = new User();
        existing.setUsername("user1");
        existing.setId(1L);

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("user1");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(userRepository.save(existing))
                .thenReturn(existing);

        UserDto result = userService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("user1", result.getUsername());

        verify(modelMapper).map(dto, existing);
        verify(userRepository).save(existing);
    }

    @Test
    void update_UserNotFound() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("user1");

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_UsernameConflict() {

        User existing = new User();
        existing.setUsername("old");
        existing.setId(1L);

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("new");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(userRepository.findByUsername("new"))
                .thenReturn(new User());

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        User user = new User();

        when(userRepository.findByUsername("user1"))
                .thenReturn(user);

        userService.delete("user1");

        verify(userRepository).findByUsername("user1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        User user1 = new User();
        User user2 = new User();

        Page<User> page = new PageImpl<>(
                List.of(user1, user2),
                pageable,
                2
        );

        List<UserDto> dtoList =
                List.of(new UserDto(), new UserDto());

        when(userRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<UserDto> result =
                userService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2,
                result.getContent().size());
        Assertions.assertEquals(0,
                result.getPage());
        Assertions.assertEquals(10,
                result.getSizePerPage());
        Assertions.assertEquals(1,
                result.getTotalPages());
        Assertions.assertEquals(2,
                result.getTotalRecords());

        verify(userRepository)
                .findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(userRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<UserDto> result =
                userService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(
                result.getContent().isEmpty());
        Assertions.assertEquals(0,
                result.getTotalRecords());

        verify(userRepository)
                .findByIsDeletedFalse(pageable);
    }
}