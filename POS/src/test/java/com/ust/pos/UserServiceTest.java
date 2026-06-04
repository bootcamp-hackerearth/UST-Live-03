package com.ust.pos;

import com.ust.pos.dto.UserDto;
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

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findByUserNameTest() {
        User user = new User();
        UserDto dto = new UserDto();

        when(userRepository.findByUsername("admin")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        assertNotNull(userService.findByUserName("admin"));
    }

    @Test
    void saveSuccessTest() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setPassword("123");

        User user = new User();

        when(userRepository.findByUsername("admin")).thenReturn(null);
        when(modelMapper.map(dto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("123")).thenReturn("encoded");

        UserDto result = userService.save(dto);

        assertTrue(result.isSuccess());
        verify(userRepository).save(user);
    }

    @Test
    void saveDuplicateTest() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");

        when(userRepository.findByUsername("admin")).thenReturn(new User());

        UserDto result = userService.save(dto);

        assertFalse(result.isSuccess());
    }

    @Test
    void updateUserNotFoundTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = userService.update(dto);

        assertFalse(result.isSuccess());
    }

    @Test
    void updateDuplicateUsernameTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("new");

        User existing = new User();
        existing.setUsername("old");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("new")).thenReturn(new User());

        UserDto result = userService.update(dto);

        assertFalse(result.isSuccess());
    }

    @Test
    void updateSuccessTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        User existing = new User();
        existing.setUsername("admin");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        UserDto result = userService.update(dto);

        assertTrue(result.isSuccess());
        verify(modelMapper).map(dto, existing);
        verify(userRepository).save(existing);
    }

    @Test
    void testDelete() {
        userService.delete("testUser");
        verify(userRepository).deleteByUsername("testUser");
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);

        List<User> users = List.of(new User());
        Page<User> page = new PageImpl<>(users);

        when(userRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(users), any(Type.class)))
                .thenReturn(List.of(new UserDto()));

        List<UserDto> result = userService.findAll(pageable);

        assertEquals(1, result.size());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void testGetCurrentUser_NullAuth() {
        SecurityContextHolder.clearContext();

        assertFalse(userService.getCurrentUser("user"));
    }

    @Test
    void testGetCurrentUser_Anonymous() {
        Authentication auth = mock(Authentication.class);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("anonymousUser");

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertFalse(userService.getCurrentUser("user"));
    }

    @Test
    void testGetCurrentUser_Match() {
        Authentication auth = mock(Authentication.class);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("user");
        when(auth.getName()).thenReturn("user");

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertTrue(userService.getCurrentUser("user"));
    }

    @Test
    void testGetCurrentUser_NotMatch() {
        Authentication auth = mock(Authentication.class);

        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("user");
        when(auth.getName()).thenReturn("anotherUser");

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertFalse(userService.getCurrentUser("user"));
    }
}