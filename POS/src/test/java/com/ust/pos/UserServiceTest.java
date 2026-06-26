package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByUserNameTest() {
        User user = new User();
        UserDto dto = new UserDto();

        when(userRepository.findByUsername("u1")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        assertNotNull(service.findByUserName("u1"));
    }

    @Test
    void saveSuccessTest() {
        UserDto dto = new UserDto();
        dto.setUsername("u1");
        dto.setPassword("pwd");

        User mapped = new User();

        when(userRepository.findByUsername("u1")).thenReturn(null);
        when(modelMapper.map(dto, User.class)).thenReturn(mapped);
        when(passwordEncoder.encode("pwd")).thenReturn("encoded");

        UserDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(userRepository).save(mapped);
    }

    @Test
    void saveDuplicateActiveTest() {
        UserDto dto = new UserDto();
        dto.setUsername("u1");

        User existing = new User();
        existing.setDeleted(false);

        when(userRepository.findByUsername("u1")).thenReturn(existing);

        UserDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void saveDuplicateDeletedTest() {
        UserDto dto = new UserDto();
        dto.setUsername("u1");

        User existing = new User();
        existing.setDeleted(true);

        when(userRepository.findByUsername("u1")).thenReturn(existing);

        UserDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("u1");

        User existing = new User();
        existing.setUsername("u1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        UserDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(userRepository).save(existing);
    }

    @Test
    void updateUserNotFoundTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("u1");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void updateDuplicateUsernameTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newUser");

        User existing = new User();
        existing.setUsername("oldUser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("newUser")).thenReturn(new User());

        UserDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void deleteTest() {
        User user = new User();
        user.setDeleted(false);

        when(userRepository.findByUsername("u1")).thenReturn(user);

        service.delete("u1");

        assertTrue(user.isDeleted());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<User> page = new PageImpl<>(List.of(new User()), pageable, 1);

        when(userRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new UserDto()));

        WsDto<UserDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void getCurrentUserSuccessTest() {
        Authentication auth = mock(Authentication.class);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("u1");
        when(auth.getPrincipal()).thenReturn("u1");

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertTrue(service.getCurrentUser("u1"));
    }

    @Test
    void getCurrentUserMismatchTest() {
        Authentication auth = mock(Authentication.class);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("u1");
        when(auth.getPrincipal()).thenReturn("u1");

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertFalse(service.getCurrentUser("u2"));
    }

    @Test
    void getCurrentUserAnonymousTest() {
        Authentication auth = mock(Authentication.class);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("anonymousUser");

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertFalse(service.getCurrentUser("u1"));
    }

    @Test
    void getCurrentUserNullAuthTest() {
        SecurityContextHolder.clearContext();

        assertFalse(service.getCurrentUser("u1"));
    }
}