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

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void findByUserNameTest() {
        User user = new User();
        user.setUsername("admin@test.com");

        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");

        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.findByUserName("admin@test.com");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("admin@test.com", response.getUsername());
    }

    @Test
    void findByUserNameNotFoundTest() {
        Mockito.when(userRepository.findByUsername("unknown@test.com")).thenReturn(null);

        UserDto response = userService.findByUserName("unknown@test.com");

        Assertions.assertNull(response);
    }


    @Test
    void saveTest() {

        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");
        userDto.setPassword("123456");

        Mockito.when(userRepository.findByUsername("admin@test.com"))
                .thenReturn(null);

        User user = new User();
        Mockito.when(modelMapper.map(userDto, User.class))
                .thenReturn(user);

        Mockito.when(passwordEncoder.encode("123456"))
                .thenReturn("hashedpassword");

        Mockito.when(userRepository.save(user))
                .thenReturn(user);


        UserDto response = userService.save(userDto);


        Mockito.verify(userRepository).save(user);
        Assertions.assertEquals("hashedpassword", user.getPassword());
        Assertions.assertNotNull(response);
    }

    @Test
    void saveTestFailure() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");

        User existingUser = new User();
        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(existingUser);

        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertTrue(response.getMessage()
                .contains(UserServiceImpl.USER_WITH_USERNAME_EMAIL));
        Assertions.assertTrue(response.getMessage().contains("admin@test.com"));
    }

    /* ===================== UPDATE ===================== */

    @Test
    void updateTest() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");
        userDto.setRoles(new ArrayList<>(List.of("ADMIN")));   // non-empty roles

        User existingUser = new User();
        existingUser.setUsername("admin@test.com");
        existingUser.setRoles(new ArrayList<>(List.of("USER")));

        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(existingUser);

        UserDto response = userService.update(userDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("User updated successfully", response.getMessage());
        Mockito.verify(userRepository).save(existingUser);
    }

    @Test
    void updateTestNotFound() {
        UserDto userDto = new UserDto();
        userDto.setUsername("unknown@test.com");

        Mockito.when(userRepository.findByUsername("unknown@test.com")).thenReturn(null);

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("User not found", response.getMessage());
    }

    @Test
    void updateTestNullRoles() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");
        userDto.setRoles(null);

        User existingUser = new User();
        existingUser.setUsername("admin@test.com");
        existingUser.setRoles(new ArrayList<>(List.of("USER")));

        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(existingUser);

        UserDto response = userService.update(userDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertTrue(existingUser.getRoles().contains("USER"));
    }

    @Test
    void updateTestEmptyRoles() {
        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");
        userDto.setRoles(new ArrayList<>());

        User existingUser = new User();
        existingUser.setUsername("admin@test.com");
        existingUser.setRoles(new ArrayList<>(List.of("USER")));

        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(existingUser);
        UserDto response = userService.update(userDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertTrue(existingUser.getRoles().contains("USER"));
    }


    @Test
    void updateTestDuplicateUsernameOnDifferentCase() {

        UserDto userDto = new UserDto();
        userDto.setUsername("newname@test.com");
        userDto.setRoles(new ArrayList<>(List.of("ADMIN")));
        User existingUser = new User();
        existingUser.setUsername("oldname@test.com");
        User conflictUser = new User();
        conflictUser.setUsername("newname@test.com");

        Mockito.when(userRepository.findByUsername("newname@test.com"))
                .thenReturn(existingUser)
                .thenReturn(conflictUser);

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("newname@test.com"));
    }


    @Test
    void deleteTest() {
        userService.delete("admin@test.com");

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteByUsername("admin@test.com");
    }


    @Test
    void findAllTest() {
        User user = new User();
        UserDto userDto = new UserDto();
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(users);

        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(users),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(userDtos);

        List<UserDto> response = userService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }


    @Test
    void toggleStatusFalseToTrueTest() {
        User user = new User();
        user.setStatus(false);

        UserDto userDto = new UserDto();
        userDto.setStatus(true);

        Mockito.when(userRepository.findByIdentifier("admin-identifier")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.toggleStatus("admin-identifier");

        Assertions.assertTrue(user.isStatus());
        Mockito.verify(userRepository).save(user);
        Assertions.assertTrue(response.isStatus());
    }


    @Test
    void toggleStatusTrueToFalseTest() {
        User user = new User();
        user.setStatus(true);

        UserDto userDto = new UserDto();
        userDto.setStatus(false);

        Mockito.when(userRepository.findByIdentifier("admin-identifier")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.toggleStatus("admin-identifier");

        Assertions.assertFalse(user.isStatus());
        Mockito.verify(userRepository).save(user);
        Assertions.assertFalse(response.isStatus());
    }


    @Test
    void findIfTrueTest() {
        User user = new User();
        UserDto userDto = new UserDto();
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        Mockito.when(userRepository.findByStatusIsTrue()).thenReturn(users);
        Mockito.when(modelMapper.map(
                Mockito.eq(users),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(userDtos);

        List<UserDto> response = userService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }
}
