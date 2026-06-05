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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void saveTest() {
        UserDto userDto = new UserDto();
        userDto.setUsername("chaila@gmail.com");
        userDto.setPassword("12345");
        Mockito.when(userRepository.findByUsername("chaila@gmail.com")).thenReturn(null);
        User user = new User();
        Mockito.when(modelMapper.map(userDto, User.class)).thenReturn(user);
        Mockito.when(passwordEncoder.encode("12345")).thenReturn("encodedPwd");
        Mockito.when(userRepository.save(user)).thenReturn(user);
        UserDto response = userService.save(userDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        UserDto userDto = new UserDto();
        userDto.setUsername("chaila@gmail.com");
        User user = new User();
        Mockito.when(userRepository.findByUsername("chaila@gmail.com")).thenReturn(user);
        UserDto response = userService.save(userDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByUsernameTest() {
        User user = new User();
        user.setUsername("chaila@gmail.com");
        UserDto userDto = new UserDto();
        userDto.setUsername("chaila@gmail.com");
        Mockito.when(userRepository.findByUsername("chaila@gmail.com")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        UserDto response = userService.findByUserName("chaila@gmail.com");
        Assertions.assertEquals("chaila@gmail.com", response.getUsername());
    }

    @Test
    void updateTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("chailashree@gmail.com");
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("chailashree@gmail.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(existingUser)).thenReturn(existingUser);
        UserDto response = userService.update(userDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("chailashree@gmail.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserDto response = userService.update(userDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(userRepository).deleteByUsername("chaila@gmail.com");
        userService.delete("chaila@gmail.com");
        Mockito.verify(userRepository).deleteByUsername("chaila@gmail.com");
    }
    
    @Test
    void findAllWithPageableTest() {
        User user = new User();
        user.setIdentifier("chaila@gmail.com");
        UserDto dto = new UserDto();
        dto.setIdentifier("chaila@gmail.com");
        List<User> users = List.of(user);
        List<UserDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<User> userPage = new PageImpl<>(users);
        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(Mockito.eq(users), Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<UserDto> response = userService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("chaila@gmail.com", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        User user = new User();
        user.setIdentifier("chaila@gmail.com");
        UserDto dto = new UserDto();
        dto.setIdentifier("chaila@gmail.com");
        List<User> users = List.of(user);
        List<UserDto> dtos = List.of(dto);
        Mockito.when(userRepository.findAll()).thenReturn(users);
        Mockito.when(modelMapper.map(Mockito.eq(users), Mockito.any(Type.class)
        )).thenReturn(dtos);
        List<UserDto> response = userService.findAll(null);
        Assertions.assertEquals(1, response.size());
    }
}