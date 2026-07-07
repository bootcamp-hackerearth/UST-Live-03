package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;

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
    void findByUserNameSuccessTest() {
        User user = new User();
        user.setUsername("john");

        UserDto dto = new UserDto();
        dto.setUsername("john");

        Mockito.when(userRepository.findByUsernameAndIsDeletedFalse("john")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto result = userService.findByUserName("john");

        Assertions.assertEquals("john", result.getUsername());
    }

    @Test
    void findByUserNameFailureTest() {
        Mockito.when(userRepository.findByUsernameAndIsDeletedFalse("john")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            userService.findByUserName("john");
        });
    }

    @Test
    void saveSuccessTest() {
        UserDto dto = new UserDto();
        dto.setUsername("john");
        dto.setPassword("pwd");
        dto.setSuccess(true);

        User user = new User();
        user.setUsername("john");

        Mockito.when(userRepository.findByUsername("john")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, User.class)).thenReturn(user);
        Mockito.when(passwordEncoder.encode("pwd")).thenReturn("ENCODED");

        UserDto result = userService.save(dto);

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertNull(result.getMessage());
        verify(userRepository).save(user);
    }

    @Test
    void saveFailureUserAlreadyExistsTest() {
        UserDto dto = new UserDto();
        dto.setUsername("john");

        Mockito.when(userRepository.findByUsername("john")).thenReturn(new User());

        UserDto result = userService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void updateFailureUserNotFoundTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("john");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void updateSuccessTest_usernameUnchanged() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("john");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("john");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        Mockito.doNothing().when(modelMapper).map(Mockito.any(UserDto.class), Mockito.any(User.class));

        UserDto result = userService.update(dto);

        Assertions.assertTrue(result.isSuccess());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateFailureUsernameAlreadyExistsTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newUser");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldUser");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByUsername("newUser")).thenReturn(new User());

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void updateSuccessTest_usernameChangedButNotExists() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newUser");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldUser");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByUsername("newUser")).thenReturn(null);

        Mockito.doNothing().when(modelMapper).map(Mockito.any(UserDto.class), Mockito.any(User.class));

        UserDto result = userService.update(dto);

        Assertions.assertTrue(result.isSuccess());
        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteSuccessTest() {
        User user = new User();
        Mockito.when(userRepository.findByUsername("john")).thenReturn(user);

        userService.delete("john");

        verify(userRepository).findByUsername("john");
    }

    @Test
    void findAllSuccessTest() {
        User u1 = new User();
        u1.setUsername("john");

        User u2 = new User();
        u2.setUsername("alice");

        List<User> users = List.of(u1, u2);

        UserDto d1 = new UserDto();
        d1.setUsername("john");

        UserDto d2 = new UserDto();
        d2.setUsername("alice");

        List<UserDto> userDtos = List.of(d1, d2);

        Page<User> page = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 20);

        Mockito.when(userRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(users), Mockito.any(Type.class))).thenReturn(userDtos);

        WsDto<UserDto> result = userService.findAll(pageable);

        Assertions.assertEquals(2, result.getDtoList().size());
    }

    @Test
    void findAllSpecificationSuccessTest() {
        User user = new User();
        List<User> userList = List.of(user);

        UserDto dto = new UserDto();
        List<UserDto> userDtos = List.of(dto);

        Page<User> page = new PageImpl<>(userList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Specification<User> specification = Mockito.mock(Specification.class);

        Mockito.when(userRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(userList), Mockito.any(Type.class))).thenReturn(userDtos);

        WsDto<UserDto> result = userService.findAll(specification, pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }
}