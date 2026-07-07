package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("password");
        user.setDeleted(false);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("john");
        userDto.setPassword("password");
    }

    //=========================================================
    // findByUserName()
    //=========================================================

    @Test
    void testFindByUserName_Success() {

        when(userRepository.findByUsernameAndDeletedFalse("john"))
                .thenReturn(user);

        UserDto result = userService.findByUserName("john");

        assertNotNull(result);
        assertEquals("john", result.getUsername());

        verify(userRepository).findByUsernameAndDeletedFalse("john");
    }

    @Test
    void testFindByUserName_NotFound() {

        when(userRepository.findByUsernameAndDeletedFalse("john"))
                .thenReturn(null);

        UserDto result = userService.findByUserName("john");

        assertNull(result);

        verify(userRepository).findByUsernameAndDeletedFalse("john");
    }

    //=========================================================
    // save()
    //=========================================================

    @Test
    void testSave_Success() {

        when(userRepository.findByUsernameAndDeletedFalse("john"))
                .thenReturn(null);

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        User resultUser = modelMapper.map(userDto, User.class);
        resultUser.setPassword("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(resultUser);

        UserDto result = userService.save(userDto);

        assertTrue(result.isSuccess() || !result.getUsername().isEmpty());

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testSave_UserAlreadyExists() {

        when(userRepository.findByUsernameAndDeletedFalse("john"))
                .thenReturn(user);

        UserDto result = userService.save(userDto);

        assertFalse(result.isSuccess());
        assertEquals(
                UserServiceImpl.USER_WITH_USERNAME_EMAIL + "john already exists",
                result.getMessage());

        verify(userRepository, never()).save(any());
    }

    //=========================================================
    // update()
    //=========================================================

    @Test
    void testUpdate_Success() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto result = userService.update(userDto);

        assertNotNull(result);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdate_UserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertEquals(
                UserServiceImpl.USER_WITH_USERNAME_EMAIL + "john not found",
                result.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdate_DuplicateUsername() {

        User existing = new User();
        existing.setId(1L);
        existing.setUsername("oldUsername");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(userRepository.findByUsernameAndDeletedFalse("john"))
                .thenReturn(user);

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertEquals(
                UserServiceImpl.USER_WITH_USERNAME_EMAIL + "john already exists",
                result.getMessage());

        verify(userRepository, never()).save(any());
    }

    //=========================================================
    // delete()
    //=========================================================

    @Test
    void testDelete_UserExists() {

        when(userRepository.findByUsernameAndDeletedFalse("john"))
                .thenReturn(user);

        userService.delete("john");

        assertTrue(user.isDeleted());

        verify(userRepository).save(user);
    }

    @Test
    void testDelete_UserNotFound() {

        when(userRepository.findByUsernameAndDeletedFalse("john"))
                .thenReturn(null);

        userService.delete("john");

        verify(userRepository, never()).save(any());
    }

    //=========================================================
    // findAll()
    //=========================================================

    @Test
    void testFindAll() {

        List<User> users = List.of(user);

        when(userRepository.findByDeletedFalse())
                .thenReturn(users);

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();

        List<UserDto> mapped =
                modelMapper.map(users, listType);

        List<UserDto> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userRepository).findByDeletedFalse();
    }

    //=========================================================
    // findAll(search,pageable)
    //=========================================================

    @Test
    void testFindAll_WithSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)))
                .thenReturn(page);

        Page<UserDto> result = userService.findAll("john", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(userRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable));
    }

    @Test
    void testFindAll_WithoutSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Page<UserDto> result = userService.findAll("", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        verify(userRepository).findByDeletedFalse(pageable);
    }

    @Test
    void testFindAll_WithNullSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Page<UserDto> result = userService.findAll(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(userRepository).findByDeletedFalse(pageable);
    }
}