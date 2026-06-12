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
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
class UserServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void saveTest() {
        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");
        userDto.setPassword("navya");
        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(null);
        User user = new User();
        Mockito.when(modelMapper.map(userDto, User.class)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        UserDto response = userService.save(userDto);
        Assertions.assertEquals("Admin", response.getUsername());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");
        User existingUser = new User();
        existingUser.setUsername("Admin");
        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(existingUser);
        UserDto response = userService.save(userDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByUserNameTest() {
        User user = new User();
        user.setIdentifier("Admin");
        UserDto userDto = new UserDto();
        userDto.setIdentifier("Admin");
        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        UserDto response = userService.findByUserName("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("Admin");
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("Admin");
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(existingUser));
        Mockito.doNothing().when(modelMapper).map(userDto, existingUser);
        Mockito.when(userRepository.save(existingUser)).thenReturn(existingUser);
        UserDto response = userService.update(userDto);
        Assertions.assertEquals("Admin", response.getUsername());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        UserDto response = userService.update(userDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        User user = new User();
        user.setUsername("Admin");
        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("OtherUser");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        lenient().when(userRepository.findByUsername("Admin")).thenReturn(user);
        lenient().when(modelMapper.map(
                Mockito.any(User.class),
                Mockito.eq(UserDto.class)
        )).thenReturn(userDto);
        UserDto response = userService.delete("Admin");
        Assertions.assertEquals("Admin", response.getUsername());
    }

    @Test
    void findAllTest() {
        User user = new User();
        user.setIdentifier("Admin");
        UserDto userDto = new UserDto();
        userDto.setIdentifier("Admin");
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 2), users.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(
                Mockito.eq(users),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(userDtos);
        WsDto<UserDto> response = userService.findAll(pageable);
        Assertions.assertEquals(userDtos, response.getDtoList());
        Assertions.assertEquals(1L, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findByStatusTest() {
        User user = new User();
        user.setIdentifier("Admin");
        UserDto userDto = new UserDto();
        userDto.setIdentifier("Admin");
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

    @Test
    void toggleTestActive() {
        User user = new User();
        user.setStatus(false);
        UserDto userDto = new UserDto();
        userDto.setStatus(true);
        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        UserDto response = userService.toggleStatus("Admin");
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        User user = new User();
        user.setStatus(true);
        UserDto userDto = new UserDto();
        userDto.setStatus(false);
        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        UserDto response = userService.toggleStatus("Admin");
        Assertions.assertFalse(response.isStatus());
    }
}