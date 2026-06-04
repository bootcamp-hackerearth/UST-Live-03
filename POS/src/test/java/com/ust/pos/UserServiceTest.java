package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.modell.User;
import com.ust.pos.modell.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByUserName_shouldHandleBothCases() {
        User user = new User();
        user.setUsername("admin");
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        when(repository.findByUsername("admin")).thenReturn(user);
        when(mapper.map(user, UserDto.class)).thenReturn(dto);
        assertEquals("admin", service.findByUserName("admin").getUsername());
        when(repository.findByUsername("X")).thenReturn(null);
        assertNull(service.findByUserName("X"));
    }

    @Test
    void save_shouldHandleAllCases() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setPassword("raw");
        User user = new User();
        when(repository.findByUsername("admin")).thenReturn(null);
        when(mapper.map(dto, User.class)).thenReturn(user);
        when(encoder.encode("raw")).thenReturn("encoded");
        UserDto result = service.save(dto);
        verify(repository).save(user);
        assertEquals("encoded", user.getPassword());
        assertTrue(result.isSuccess());
        when(repository.findByUsername("admin")).thenReturn(user);
        UserDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void update_shouldHandleAllCases() {
        User existing = new User();
        existing.setUsername("oldadmin");
        UserDto dto = new UserDto();
        dto.setUsername("newadmin");
        when(repository.findByUsername("oldadmin")).thenReturn(null);
        UserDto notFound = service.update("oldadmin", dto);
        assertFalse(notFound.isSuccess());
        when(repository.findByUsername("oldadmin")).thenReturn(existing);
        when(repository.findByUsername("newadmin")).thenReturn(new User());
        UserDto duplicate = service.update("oldadmin", dto);
        assertFalse(duplicate.isSuccess());
        dto.setUsername("OLDADMIN");
        when(repository.findByUsername("oldadmin")).thenReturn(existing);
        UserDto same = service.update("oldadmin", dto);
        assertTrue(same.isSuccess());
        dto.setUsername("newadmin");
        when(repository.findByUsername("newadmin")).thenReturn(null);
        UserDto success = service.update("oldadmin", dto);
        assertTrue(success.isSuccess());
        verify(repository, atLeastOnce()).save(existing);
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete("admin");
        verify(repository).deleteByUsername("admin");
    }

    @Test
    void findAll_shouldHandleDataAndEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<UserDto>>() {}.getType();
        User user = new User();
        user.setUsername("admin");
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        Page<User> page =
                new PageImpl<>(List.of(user), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(any(), eq(type))).thenReturn(List.of(dto));
        assertEquals(1, service.findAll(pageable).size());
        Page<User> empty =
                new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(empty);
        when(mapper.map(List.of(), type)).thenReturn(List.of());
        assertTrue(service.findAll(pageable).isEmpty());
    }
}
