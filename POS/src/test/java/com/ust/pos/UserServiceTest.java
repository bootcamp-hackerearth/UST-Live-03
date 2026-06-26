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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
    void testSave_Success() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin");
        userDto.setPassword("12345");

        User user = new User();
        user.setUsername("admin");

        when(userRepository.findByIdentifier("admin")).thenReturn(null);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("12345")).thenReturn("encodedPassword");

        UserDto result = userService.save(userDto);

        assertNotNull(result);
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("admin", user.getIdentifier());

        verify(userRepository).save(user);
    }

    @Test
    void testSave_AlreadyExists() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin");

        User existingUser = new User();
        existingUser.setDeleted(false);

        when(userRepository.findByIdentifier("admin")).thenReturn(existingUser);

        UserDto result = userService.save(userDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "User with username/email - admin already exists",
                result.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testSave_DeletedUserExists() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin");

        User existingUser = new User();
        existingUser.setDeleted(Boolean.TRUE);

        when(userRepository.findByIdentifier("admin"))
                .thenReturn(existingUser);

        UserDto result = userService.save(userDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "User with username/email - admin was deleted and cannot be created again.",
                result.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdate_Success() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("admin");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("admin");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        UserDto result = userService.update(userDto);

        assertNotNull(result);

        verify(modelMapper).map(userDto, existingUser);
        verify(userRepository).save(existingUser);
    }

    @Test
    void testUpdate_NotFound() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("admin");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "User with username/email - admin not found",
                result.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdate_UsernameAlreadyExists() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("newadmin");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("admin");

        User duplicateUser = new User();
        duplicateUser.setUsername("newadmin");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByIdentifierAndDeletedFalse("newadmin")).thenReturn(duplicateUser);

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "User with username/email - newadmin already exists",
                result.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "admin";

        User user = new User();
        user.setIdentifier(identifier);
        user.setDeleted(false);

        when(userRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(user);

        userService.delete(identifier);

        assertTrue(user.getDeleted());
        verify(userRepository).save(user);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setIdentifier("admin");

        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        UserDto userDto = new UserDto();
        userDto.setUsername("admin");

        when(userRepository.findAllByDeletedFalse(pageable))
                .thenReturn(userPage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(userDto));

        WsDto<UserDto> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

}