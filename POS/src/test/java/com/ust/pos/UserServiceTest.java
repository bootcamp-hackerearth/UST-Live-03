package com.ust.pos;

import com.ust.pos.dto.UserDto;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
    void saveTest() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin");
        userDto.setPassword("123");
        userDto.setSuccess(true);
        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(null);
        User user = new User();
        Mockito.when(modelMapper.map(userDto, User.class))
                .thenReturn(user);
        Mockito.when(passwordEncoder.encode("123"))
                .thenReturn("encoded123");
        Mockito.when(userRepository.save(user))
                .thenReturn(user);
        UserDto response = userService.save(userDto);
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals("encoded123", user.getPassword());
        Mockito.verify(passwordEncoder).encode("123");
        Mockito.verify(userRepository).save(user);
    }
    
    @Test
    void saveTestFailure() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin");
        User existingUser = new User();
        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(existingUser);
        UserDto response = userService.save(userDto);
        assertFalse(response.isSuccess());
        assertEquals(
                "User with username/email - admin already exists",
                response.getMessage()
        );
        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any(User.class));
    }
    
    @Test
    void findByUserNameTest() {
        User user = new User();
        user.setUsername("admin");
        UserDto userDto = new UserDto();
        userDto.setUsername("admin");
        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(userDto);
        UserDto response = userService.findByUserName("admin");
        assertNotNull(response);
        assertEquals("admin", response.getUsername());
    }

    @Test
    void findByUserNameNotFoundTest() {
        Mockito.when(userRepository.findByUsername("unknown"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(null, UserDto.class))
                .thenReturn(null);
        UserDto response = userService.findByUserName("unknown");
        assertNull(response);
    }

    @Test
    void updateTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("admin");
        userDto.setSuccess(true);
        User existingUser = new User();
        existingUser.setUsername("admin");
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));
        Mockito.doNothing()
                .when(modelMapper)
                .map(userDto, existingUser);
        Mockito.when(userRepository.save(existingUser))
                .thenReturn(existingUser);
        UserDto response = userService.update(userDto);
        assertNotNull(response);
        assertTrue(response.isSuccess());
        Mockito.verify(modelMapper)
                .map(userDto, existingUser);
        Mockito.verify(userRepository)
                .save(existingUser);
    }

    @Test
    void updateTestSameUsername() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("admin");
        User existingUser = new User();
        existingUser.setUsername("admin");
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));
        Mockito.doNothing()
                .when(modelMapper)
                .map(userDto, existingUser);
        userService.update(userDto);
        Mockito.verify(userRepository)
                .save(existingUser);
        Mockito.verify(userRepository, Mockito.never())
                .findByUsername(Mockito.anyString());
    }

    @Test
    void updateTestUsernameDifferentButNotExists() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("newUser");
        User existingUser = new User();
        existingUser.setUsername("oldUser");
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByUsername("newUser"))
                .thenReturn(null);
        Mockito.doNothing()
                .when(modelMapper)
                .map(userDto, existingUser);
        userService.update(userDto);
        Mockito.verify(userRepository)
                .findByUsername("newUser");
        Mockito.verify(userRepository)
                .save(existingUser);
        Mockito.verify(modelMapper)
                .map(userDto, existingUser);
    }

    @Test
    void updateTestFailure_UserNotFound() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("admin");
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        UserDto response = userService.update(userDto);
        assertFalse(response.isSuccess());
        assertEquals(
                "User with username/email - admin not found",
                response.getMessage()
        );
        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any(User.class));
    }

    @Test
    void updateTestFailure_UsernameExists() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("newUser");
        User existingUser = new User();
        existingUser.setUsername("oldUser");
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByUsername("newUser"))
                .thenReturn(new User());
        UserDto response = userService.update(userDto);
        assertFalse(response.isSuccess());
        assertEquals(
                "User with username/email - newUser already exists",
                response.getMessage()
        );
        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any(User.class));
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(userRepository)
                .deleteByUsername("admin");
        boolean result = userService.delete("admin");
        assertTrue(result);
        Mockito.verify(userRepository)
                .deleteByUsername("admin");
    }

    @Test
    void deleteReturnTrueTest() {
        Mockito.doNothing()
                .when(userRepository)
                .deleteByUsername("admin");
        boolean result = userService.delete("admin");
        assertTrue(result);
    }

    @Test
    void findAllTest() {
        User user = new User();
        user.setUsername("user1");
        UserDto userDto = new UserDto();
        userDto.setUsername("user1");
        List<User> userList = List.of(user);
        List<UserDto> userDtoList = List.of(userDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(userList);
        Mockito.when(userRepository.findAll(pageable))
                .thenReturn(userPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(userList),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(userDtoList);
        List<UserDto> response = userService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("user1", response.get(0).getUsername());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(List.of());
        Mockito.when(userRepository.findAll(pageable))
                .thenReturn(emptyPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(List.of()),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of());
        List<UserDto> response = userService.findAll(pageable);
        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}