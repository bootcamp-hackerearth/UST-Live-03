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
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByUserNameTest() {

        User user = new User();
        user.setUsername("john");

        UserDto userDto = new UserDto();
        userDto.setUsername("john");

        Mockito.when(userRepository.findByUsername("john")).thenReturn(user);

        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.findByUserName("john");

        Assertions.assertEquals("john", response.getUsername());
    }

    @Test
    void saveTest() {

        UserDto userDto = new UserDto();
        userDto.setUsername("john");
        userDto.setPassword("password");

        User user = new User();

        Mockito.when(userRepository.findByUsername("john")).thenReturn(null);

        Mockito.when(modelMapper.map(userDto, User.class)).thenReturn(user);

        Mockito.when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        UserDto response = userService.save(userDto);

        Assertions.assertEquals("john", response.getUsername());

        Assertions.assertEquals("encodedPassword", user.getPassword());

        Mockito.verify(userRepository).save(user);
    }

    @Test
    void saveTestFailure() {

        UserDto userDto = new UserDto();
        userDto.setUsername("john");

        User existingUser = new User();

        Mockito.when(userRepository.findByUsername("john")).thenReturn(existingUser);

        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "john already exists", response.getMessage());
    }

    @Test
    void updateTest() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("john");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("john");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        Mockito.doAnswer(invocation -> {

            UserDto source = invocation.getArgument(0);
            User target = invocation.getArgument(1);

            target.setUsername(source.getUsername());

            return null;

        }).when(modelMapper).map(Mockito.any(UserDto.class), Mockito.any(User.class));

        UserDto response = userService.update(userDto);

        Assertions.assertEquals("john", response.getUsername());

        Mockito.verify(userRepository).save(existingUser);
    }

    @Test
    void updateTestFailureUserNotFound() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("john");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "john not found", response.getMessage());
    }

    @Test
    void updateTestFailureUsernameExists() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("john");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("admin");

        User duplicateUser = new User();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        Mockito.when(userRepository.findByUsername("john")).thenReturn(duplicateUser);

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "john already exists", response.getMessage());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(userRepository).deleteByUsername("john");

        boolean result = userService.delete("john");

        Assertions.assertTrue(result);

        Mockito.verify(userRepository).deleteByUsername("john");
    }

    @Test
    void findAllTest() {

        User user = new User();
        user.setUsername("john");

        UserDto userDto = new UserDto();
        userDto.setUsername("john");

        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        Page<User> userPage = new PageImpl<>(users);

        Mockito.when(userRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(userPage);

        Mockito.when(modelMapper.map(Mockito.eq(users), Mockito.any(Type.class))).thenReturn(userDtos);

        List<UserDto> response = userService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("john", response.get(0).getUsername());
    }
}