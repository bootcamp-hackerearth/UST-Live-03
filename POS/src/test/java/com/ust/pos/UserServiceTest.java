package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        userDto.setSuccess(true);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encoded-password");
    }

    @Test
    void testFindAll_EmptyPage() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<User> emptyPage =
                new PageImpl<>(
                        Collections.emptyList(),
                        pageable,
                        0
                );

        when(userRepository.findAll(pageable))
                .thenReturn(emptyPage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<UserDto> result =
                userService.findAll(pageable);

        assertNotNull(result);

        assertNotNull(result.getDtoList());
        assertTrue(result.getDtoList().isEmpty());

        assertEquals(0, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(userRepository)
                .findAll(pageable);
    }

    @Test
    void testFindAll_Metadata() {

        Pageable pageable =
                PageRequest.of(2, 5);

        Page<User> page =
                new PageImpl<>(
                        List.of(user),
                        pageable,
                        21
                );

        when(userRepository.findAll(pageable))
                .thenReturn(page);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(userDto));

        WsDto<UserDto> result =
                userService.findAll(pageable);

        assertEquals(21, result.getTotalRecords());
        assertEquals(page.getTotalPages(), result.getTotalPages());
        assertEquals(5, result.getSizePerPage());
        assertEquals(2, result.getPage());
    }

    @Test
    void testFindByUserName() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.findByUserName("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testFindByUserName_NotFound() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(null);

        UserDto result =
                userService.findByUserName("unknown");

        assertNull(result);

        verify(userRepository)
                .findByUsername("unknown");

        verify(modelMapper, never())
                .map(any(), eq(UserDto.class));
    }

    @Test
    void testSave_UserAlreadyExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserDto result = userService.save(userDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSave_NewUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");

        UserDto result = userService.save(userDto);

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);
        assertNotNull(result);
    }

    @Test
    void testSave_EncodesPasswordBeforeSave() {

        when(userRepository.findByUsername("testuser"))
                .thenReturn(null);

        when(modelMapper.map(userDto, User.class))
                .thenReturn(user);

        when(passwordEncoder.encode("password"))
                .thenReturn("ENCODED");

        userService.save(userDto);

        assertEquals("ENCODED", user.getPassword());

        verify(passwordEncoder)
                .encode("password");

        verify(userRepository)
                .save(user);
    }

    @Test
    void testUpdate_UserNotFoundById() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdate_UsernameAlreadyExists() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("existinguser");

        userDto.setUsername("existinguser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("existinguser")).thenReturn(anotherUser);

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdate_Success() {
        userDto.setUsername("newuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("newuser")).thenReturn(null);

        UserDto result = userService.update(userDto);

        verify(modelMapper).map(userDto, user);
        verify(userRepository).save(user);
        assertNotNull(result);
    }

    @Test
    void testUpdate_UsernameNotChanged_ShouldUpdateSuccessfully() {

        user.setId(1L);
        user.setUsername("sameuser");

        userDto.setId(1L);
        userDto.setUsername("sameuser");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto result = userService.update(userDto);

        assertTrue(result.isSuccess());
        verify(modelMapper).map(userDto, user);
        verify(userRepository).save(user);
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void testDelete() {
        doNothing().when(userRepository).deleteByUsername("testuser");

        userService.delete("testuser");

        verify(userRepository).deleteByUsername("testuser");
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> userPage =
                new PageImpl<>(
                        Collections.singletonList(user),
                        pageable,
                        1
                );

        when(userRepository.findAll(pageable))
                .thenReturn(userPage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(userDto));

        WsDto<UserDto> result =
                userService.findAll(pageable);

        assertNotNull(result);

        assertNotNull(result.getDtoList());
        assertEquals(1, result.getDtoList().size());

        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());

        verify(userRepository)
                .findAll(pageable);

        verify(modelMapper)
                .map(anyList(), any(Type.class));
    }
}