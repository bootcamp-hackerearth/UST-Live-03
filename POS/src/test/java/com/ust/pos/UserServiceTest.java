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
public class UserServiceTest {

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
        userDto.setUsername("lekhya@gmail.com");
        userDto.setPassword("12345");

        Mockito.when(userRepository.findByUsername("lekhya@gmail.com")).thenReturn(null);
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
        userDto.setUsername("lekhya@gmail.com");
        User user = new User();

        Mockito.when(userRepository.findByUsername("lekhya@gmail.com")).thenReturn(user);
        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByUsernameTest() {
        User user = new User();
        user.setUsername("lekhya@gmail.com");

        UserDto userDto = new UserDto();
        userDto.setUsername("lekhya@gmail.com");

        Mockito.when(userRepository.findByUsername("lekhya@gmail.com")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.findByUserName("lekhya@gmail.com");

        Assertions.assertEquals("lekhya@gmail.com", response.getUsername());
    }

    @Test
    void updateTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("logeshust@gmail.com");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("logeshust@gmail.com");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        UserDto response = userService.update(userDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("logeshust@gmail.com");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(userRepository)
                .deleteByUsername("lekhya@gmail.com");

        userService.delete("lekhya@gmail.com");

        Mockito.verify(userRepository).deleteByUsername("lekhya@gmail.com");
    }

    @Test
    void findAllWithPageableTest() {
        User user = new User();
        user.setIdentifier("lekhya@gmail.com");

        UserDto dto = new UserDto();
        dto.setIdentifier("lekhya@gmail.com");

        List<User> users = List.of(user);
        List<UserDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<User> userPage = new PageImpl<>(users);

        Mockito.when(userRepository.findAll(pageable))
                .thenReturn(userPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(users),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

        List<UserDto> response = userService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("lekhya@gmail.com", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        User user = new User();
        user.setIdentifier("lekhya@gmail.com");

        UserDto dto = new UserDto();
        dto.setIdentifier("lekhya@gmail.com");

        List<User> users = List.of(user);
        List<UserDto> dtos = List.of(dto);

        Mockito.when(userRepository.findAll())
                .thenReturn(users);
        Mockito.when(modelMapper.map(
                Mockito.eq(users),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

        List<UserDto> response = userService.findAll(null);

        Assertions.assertEquals(1, response.size());
    }

}