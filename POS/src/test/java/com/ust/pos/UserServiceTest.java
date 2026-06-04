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

        Mockito.when(userRepository.findByUsername("admin")).thenReturn(null);

        User user = new User();

        Mockito.when(modelMapper.map(userDto, User.class)).thenReturn(user);
        Mockito.when(passwordEncoder.encode("123")).thenReturn("encoded123");

        userService.save(userDto);
        Mockito.verify(userRepository).save(user);
        assertEquals("admin", userDto.getUsername());

    }

    @Test
    void saveTestFailure() {

        UserDto userDto = new UserDto();
        userDto.setUsername("admin");

        User existingUser = new User();

        Mockito.when(userRepository.findByUsername("admin")).thenReturn(existingUser);

        UserDto response = userService.save(userDto);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("already exists"));

    }

    @Test
    void findByUserNameTest() {

        User user = new User();
        user.setUsername("admin");

        UserDto userDto = new UserDto();
        userDto.setUsername("admin");

        Mockito.when(userRepository.findByUsername("admin")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.findByUserName("admin");
        assertEquals("admin", response.getUsername());

    }

    @Test
    void updateTest() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("admin");

        User existingUser = new User();
        existingUser.setUsername("admin");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.doNothing().when(modelMapper).map(userDto, existingUser);

        userService.update(userDto);
        Mockito.verify(userRepository).save(existingUser);

    }

    @Test
    void updateTestFailure_UserNotFound() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("admin");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto response = userService.update(userDto);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("not found"));

    }

    @Test
    void updateTestFailure_UsernameExists() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("newUser");

        User existingUser = new User();
        existingUser.setUsername("oldUser");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByUsername("newUser")).thenReturn(new User());

        UserDto response = userService.update(userDto);
        assertFalse(response.isSuccess());
        assertTrue(response.getMessage().contains("already exists"));

    }


    @Test
    void deleteTest() {

        Mockito.doNothing().when(userRepository).deleteByUsername("admin");

        userService.delete("admin");
        Mockito.verify(userRepository).deleteByUsername("admin");

    }


    @Test
    void findAllWithPaginationTest() {

        Pageable pageable = PageRequest.of(0, 5);
        User user = new User();
        user.setUsername("admin");

        UserDto userDto = new UserDto();
        userDto.setUsername("admin");

        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(Mockito.eq(userPage.getContent()), Mockito.any(java.lang.reflect.Type.class))).thenReturn(userDtos);

        List<UserDto> response = userService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("admin", response.get(0).getUsername());

    }

}