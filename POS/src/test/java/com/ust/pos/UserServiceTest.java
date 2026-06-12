package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
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
    void findByUserNameTest() {
        User user = new User();
        user.setUsername("admin");
        UserDto dto = new UserDto();
        dto.setUsername("admin");

        when(repository.findByUsername("admin")).thenReturn(user);
        when(mapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto result = service.findByUserName("admin");
        assertEquals("admin", result.getUsername());

        when(repository.findByUsername("X")).thenReturn(null);

        UserDto notFound = service.findByUserName("X");
        assertFalse(notFound.isSuccess());
        assertEquals("User not found", notFound.getMessage());
    }

    @Test
    void saveTest() {
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
        assertEquals("User created successfully", result.getMessage());

        when(repository.findByUsername("admin")).thenReturn(user);

        UserDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void updateTest() {
        User existing = new User();
        existing.setUsername("oldadmin");

        UserDto dto = new UserDto();
        dto.setUsername("newadmin");

        when(repository.findByUsername("oldadmin")).thenReturn(null);

        UserDto notFound = service.update("oldadmin", dto);
        assertFalse(notFound.isSuccess());
        assertEquals("User not found", notFound.getMessage());

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
        assertEquals("User updated successfully", success.getMessage());

        verify(repository, atLeastOnce()).save(existing);
    }

    @Test
    void deleteTest() {
        service.delete("admin");
        verify(repository).deleteByUsername("admin");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<UserDto>>(){}.getType();

        User user = new User();
        user.setUsername("admin");
        UserDto dto = new UserDto();
        dto.setUsername("admin");

        Page<User> page = new PageImpl<>(List.of(user), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        WsDto<UserDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());

        Page<User> empty = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(empty);
        when(mapper.map(empty.getContent(), type)).thenReturn(List.of());

        WsDto<UserDto> emptyResult = service.findAll(pageable);
        assertTrue(emptyResult.getDtoList().isEmpty());
    }
}
