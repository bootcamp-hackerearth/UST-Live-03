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
        userDto.setUsername("Admin");
        userDto.setPassword("123"); // ✅ REQUIRED

        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(null);

        User user = new User();
        Mockito.when(modelMapper.map(userDto, User.class)).thenReturn(user);

        Mockito.when(passwordEncoder.encode("123")).thenReturn("encoded123");

        Mockito.when(userRepository.save(user)).thenReturn(user);

        UserDto response = userService.save(userDto);

        Assertions.assertEquals("Admin", response.getUsername());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void saveTestFailure() {

        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");
        User user = new User();

        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(user);
        UserDto response = userService.save(userDto);

        Assertions.assertEquals("Admin", response.getUsername());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertFalse(response.isSuccess());

    }

    @Test
    void findByUsernameTest() {

        User user = new User();
        user.setUsername("Admin");

        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");

        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.findByUserName("Admin");

        Assertions.assertEquals("Admin", response.getUsername());
    }

    @Test
    void updateTest() {

        UserDto userDto = new UserDto();
        userDto.setId(1L); // ✅ REQUIRED
        userDto.setUsername("Admin");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("Admin");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(existingUser)); // ✅ correct method

        Mockito.when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        UserDto response = userService.update(userDto);

        Assertions.assertNull(response.getMessage());
    }

    @Test
    void updateTestFailure() {

        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTestDuplicateUsername() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("NewAdmin");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("OldAdmin");

        User duplicateUser = new User();
        duplicateUser.setUsername("NewAdmin");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(existingUser));

        Mockito.when(userRepository.findByUsername("NewAdmin"))
                .thenReturn(duplicateUser);

        UserDto response = userService.update(userDto);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(userRepository)
                .deleteByUsername("Admin");

        userService.delete("Admin");

        Mockito.verify(userRepository).deleteByUsername("Admin");
    }

    @Test
    void findAllTest() {

        User user = new User();
        user.setUsername("Admin");

        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");

        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        Page<User> userPage = new PageImpl<>(users);

        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(userPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(users),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(userDtos);

        List<UserDto> response = userService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllActiveTest() {

        User user = new User();
        user.setUsername("Admin");
        user.setStatus(true);

        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");

        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        Mockito.when(userRepository.findByStatus(true)).thenReturn(users);
        Mockito.when(modelMapper.map(
                Mockito.eq(users),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(userDtos);

        List<UserDto> response = userService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        User user = new User();
        user.setUsername("Admin");
        user.setStatus(false);

        Mockito.when(userRepository.findByUsername("Admin"))
                .thenReturn(user);

        Mockito.when(userRepository.save(user))
                .thenReturn(user);

        userService.changeStatus("Admin", true);

        Assertions.assertTrue(user.getStatus());

        Mockito.verify(userRepository).save(user);
    }

    @Test
    void updateTestNewUsernameNoDuplicate() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("NewAdmin");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("OldAdmin");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(existingUser));

        Mockito.when(userRepository.findByUsername("NewAdmin"))
                .thenReturn(null);

        Mockito.when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        UserDto response = userService.update(userDto);

        Assertions.assertNull(response.getMessage());

        Mockito.verify(userRepository).save(existingUser);
    }
}