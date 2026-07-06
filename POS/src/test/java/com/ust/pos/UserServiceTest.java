package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("encodedPassword");
        user.setStatus(true);
        user.setDeleted(false);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("admin");
        userDto.setPassword("password");
    }

    @Test
    void testFindByUserName() {

        when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        when(modelMapper.map(user, UserDto.class))
                .thenReturn(userDto);

        UserDto result = userService.findByUserName("admin");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
    }

    @Test
    void testSave_Success() {

        when(userRepository.findByUsername("admin"))
                .thenReturn(null);

        when(modelMapper.map(userDto, User.class))
                .thenReturn(user);

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        UserDto result = userService.save(userDto);

        assertNotNull(result);

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);
    }

    @Test
    void testSave_AlreadyExists() {

        when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        UserDto result = userService.save(userDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeleted() {

        user.setDeleted(true);

        when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        UserDto result = userService.save(userDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testUpdate_Success() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        doNothing().when(modelMapper)
                .map(userDto, user);

        UserDto result = userService.update(userDto);

        assertNotNull(result);

        verify(userRepository).save(user);
    }

    @Test
    void testUpdate_UserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_DuplicateUsername() {

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("olduser");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testDelete() {

        when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        userService.delete("admin");

        assertTrue(user.isDeleted());
        assertFalse(user.isStatus());

        verify(userRepository).save(user);
    }

    @Test
    void testChangeToggleStatus() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(modelMapper.map(user, UserDto.class))
                .thenReturn(userDto);

        UserDto result =
                userService.changeToggleStatus(1L, false);

        assertNotNull(result);
        assertFalse(user.isStatus());

        verify(userRepository).save(user);
    }

    @Test
    void testFindActiveStatus() {

        User inactiveUser = new User();
        inactiveUser.setStatus(false);

        when(userRepository.findAll())
                .thenReturn(List.of(user, inactiveUser));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(userDto));

        List<UserDto> result =
                userService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page =
                new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(userDto));

        WsDto<UserDto> result =
                userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<User> specification =
                mock(Specification.class);

        Page<User> page =
                new PageImpl<>(Collections.singletonList(user));

        when(userRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(userDto));

        WsDto<UserDto> result =
                userService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());

        verify(userRepository)
                .findAll(specification, pageable);
    }
}