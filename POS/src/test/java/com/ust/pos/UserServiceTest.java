package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.Arrays;
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
    void constructorTest() {
        UserServiceImpl service = new UserServiceImpl(userRepository, passwordEncoder, modelMapper);
        Assertions.assertNotNull(service);
    }

    @Test
    void findByUserNameSuccessTest() {
        User user = new User();
        user.setUsername("john");
        UserDto dto = new UserDto();
        dto.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto result = userService.findByUserName("john");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("john", result.getUsername());
    }

    @Test
    void findByUserNameFailureTest() {
        when(userRepository.findByUsername("john")).thenReturn(null);
        Assertions.assertNull(userService.findByUserName("john"));
    }

    @Test
    void saveSuccessTest() {
        UserDto dto = new UserDto();
        dto.setUsername("john");
        dto.setPassword("password");
        User user = new User();
        user.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(null);
        when(modelMapper.map(dto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        UserDto result = userService.save(dto);

        Assertions.assertEquals("john", result.getUsername());

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        User existingUser = new User();
        existingUser.setUsername("john");
        UserDto dto = new UserDto();
        dto.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(existingUser);

        UserDto result = userService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("User with username - john already exists", result.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void saveFailureDeletedUserTest() {
        User existingUser = new User();
        existingUser.setUsername("john");
        existingUser.setDeleted(true);
        UserDto dto = new UserDto();
        dto.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(existingUser);

        UserDto result = userService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("User with userEmail - john not available", result.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateFailureUserNotFoundTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("john");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("User with username - john not found", result.getMessage());
    }

    @Test
    void updateFailureUsernameAlreadyExistsTest() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("olduser");

        User duplicateUser = new User();
        duplicateUser.setUsername("newuser");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("newuser")).thenReturn(duplicateUser);

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("User with username - newuser already exists", result.getMessage());
    }

    @Test
    void updateSuccessTest() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("john");
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("john");
        dto.setName("John Doe");
        dto.setPhoneNo("9999999999");
        dto.setPassword("newPassword");
        dto.setRoles(List.of("ADMIN"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        userService.update(dto);

        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateSuccessWithoutPasswordTest() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("john");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("john");
        dto.setName("John");
        dto.setPhoneNo("9999999999");
        dto.setRoles(List.of("ADMIN"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.update(dto);

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateSuccessBlankPasswordTest() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("john");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("john");
        dto.setPassword("   ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        userService.update(dto);

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateSuccessChangedUsernameTest() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("olduser");
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("newuser")).thenReturn(null);

        userService.update(dto);

        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteTest() {
        User user = new User();
        user.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(user);

        userService.delete(" john ");

        Assertions.assertTrue(user.isDeleted());
    }

    @Test
    void findAllTest() {
        List<User> users = Arrays.asList(new User(), new User());
        List<UserDto> dtoList = Arrays.asList(new UserDto(), new UserDto());

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findByIsDeletedFalse(pageable)).thenReturn(userPage);
        when(modelMapper.map(Mockito.eq(users), Mockito.any(Type.class))).thenReturn(dtoList);

        WsDto<UserDto> result = userService.findAll(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }

    @Test
    void findByIdentifierSuccessTest() {
        User user = new User();
        user.setUsername("john");
        UserDto dto = new UserDto();
        dto.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto result = userService.findByIdentifier("john");
        Assertions.assertEquals("john", result.getUsername());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(userRepository.findByUsername("john")).thenReturn(null);
        Assertions.assertNull(userService.findByIdentifier("john"));
    }

    @Test
    void getUserDetailsSuccessTest() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setUsername("john");
        user.setPhoneNo("9999999999");
        user.setRoles(List.of("ADMIN"));

        when(userRepository.findByUsername("john")).thenReturn(user);

        UserDto result = userService.getUserDetails("john");

        Assertions.assertEquals("john", result.getUsername());
        Assertions.assertEquals("John Doe", result.getName());
    }

    @Test
    void getUserDetailsFailureTest() {
        when(userRepository.findByUsername("john")).thenReturn(null);
        Assertions.assertNull(userService.getUserDetails("john"));
    }
}