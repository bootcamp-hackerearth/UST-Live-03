package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
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

    @InjectMocks
    private UserServiceImpl service;

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("123");
        user.setStatus(true);
        return user;
    }

    @Test
    void findByUserNameSuccess() {

        User user = buildUser();

        UserDto dto = new UserDto();

        when(userRepository.findByUsername("john")).thenReturn(user);

        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto result = service.findByUserName("john");

        assertNotNull(result);

    }

    @Test
    void findByUserNameNull() {

        when(userRepository.findByUsername("john")).thenReturn(null);

        when(modelMapper.map(null, UserDto.class)).thenReturn(null);

        UserDto result = service.findByUserName("john");

        assertNull(result);

    }

    @Test
    void saveSuccess() {

        UserDto dto = new UserDto();

        dto.setUsername("john");
        dto.setPassword("123");

        User user = buildUser();

        when(userRepository.findByUsername("john")).thenReturn(null);

        when(modelMapper.map(dto, User.class)).thenReturn(user);

        when(passwordEncoder.encode("123")).thenReturn("ENC");

        UserDto result = service.save(dto);

        verify(passwordEncoder).encode("123");

        verify(userRepository).save(user);

        assertEquals("john", result.getUsername());

    }

    @Test
    void saveAlreadyExists() {

        User existing = buildUser();

        UserDto dto = new UserDto();

        dto.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(existing);

        UserDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("User with username/email - john already exists", result.getMessage());

        verify(userRepository, never()).save(any());

    }

    @Test
    void saveDeletedUserExists() {

        User existing = buildUser();

        existing.setDeleted(true);

        UserDto dto = new UserDto();

        dto.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(existing);

        UserDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertTrue(result.getMessage().contains("soft deleted"));

    }

    @Test
    void updateSameUsername() {

        User existing = buildUser();

        UserDto dto = new UserDto();

        dto.setId(1L);

        dto.setUsername("john");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        doNothing().when(modelMapper).map(dto, existing);

        UserDto result = service.update(dto);

        verify(modelMapper).map(dto, existing);

        verify(userRepository).save(existing);

        assertEquals(dto, result);

    }

    @Test
    void updateUsernameChangedAvailable() {

        User existing = buildUser();

        UserDto dto = new UserDto();

        dto.setId(1L);

        dto.setUsername("jack");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        when(userRepository.findByUsername("jack")).thenReturn(null);

        doNothing().when(modelMapper).map(dto, existing);

        UserDto result = service.update(dto);

        verify(userRepository).save(existing);

        assertEquals(dto, result);

    }

    @Test
    void updateUsernameAlreadyExists() {

        User existing = buildUser();

        User another = new User();

        another.setUsername("jack");

        UserDto dto = new UserDto();

        dto.setId(1L);

        dto.setUsername("jack");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        when(userRepository.findByUsername("jack")).thenReturn(another);

        UserDto result = service.update(dto);

        assertFalse(result.isSuccess());

        assertEquals("User with username/email - jack already exists", result.getMessage());

        verify(userRepository, never()).save(any());

    }

    @Test
    void updateUserNotFound() {

        UserDto dto = new UserDto();

        dto.setId(1L);

        dto.setUsername("john");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(dto));

    }

    @Test
    void deleteSuccess() {

        User user = buildUser();

        when(userRepository.findByUsername("john")).thenReturn(user);

        boolean result = service.delete("john");

        assertTrue(result);

        verify(userRepository).save(user);

    }

    @Test
    void deleteNotFound() {

        when(userRepository.findByUsername("john")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.delete("john"));

    }

    @Test
    void findAllSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        List<User> users = List.of(buildUser());

        List<UserDto> dtos = List.of(new UserDto());

        Page<User> page = new PageImpl<>(users, pageable, 1);

        when(userRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtos);

        WsDto<UserDto> ws = service.findAll(pageable);

        assertEquals(1, ws.getDtoList().size());

        assertEquals(1, ws.getTotalRecords());

    }

    @Test
    void findAllEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page = new PageImpl<>(Collections.emptyList());

        when(userRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<UserDto> ws = service.findAll(pageable);

        assertEquals(0, ws.getDtoList().size());

    }

    @Test
    void findAllSpecificationSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<User> spec = mock(Specification.class);

        List<User> users = List.of(buildUser());

        List<UserDto> dtos = List.of(new UserDto());

        Page<User> page = new PageImpl<>(users);

        when(userRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtos);

        WsDto<UserDto> ws = service.findAll(spec, pageable, "john");

        assertEquals("john", ws.getKeyword());

        assertEquals(1, ws.getDtoList().size());

    }

    @Test
    void findAllSpecificationEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<User> spec = mock(Specification.class);

        Page<User> page = new PageImpl<>(Collections.emptyList());

        when(userRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<UserDto> ws = service.findAll(spec, pageable, "abc");

        assertEquals("abc", ws.getKeyword());

        assertEquals(0, ws.getDtoList().size());

    }

}