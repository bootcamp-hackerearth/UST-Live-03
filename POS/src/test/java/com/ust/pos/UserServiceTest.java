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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void findByUserNameTest() {

        User user = new User();
        user.setUsername("john");

        UserDto userDto = new UserDto();
        userDto.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.findByUserName("john");

        Assertions.assertEquals("john", response.getUsername());
    }

    @Test
    void saveSuccessTest() {

        UserDto userDto = new UserDto();
        userDto.setUsername("john");
        userDto.setPassword("password");

        User user = new User();

        when(userRepository.findByUsername("john")).thenReturn(null);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        UserDto response = userService.save(userDto);

        Assertions.assertEquals("john", response.getUsername());
        Assertions.assertEquals("encodedPassword", user.getPassword());

        verify(userRepository).save(user);
    }

    @Test
    void saveFailureUserExistsTest() {

        UserDto userDto = new UserDto();
        userDto.setUsername("john");

        User existingUser = new User();

        when(userRepository.findByUsername("john")).thenReturn(existingUser);

        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                UserServiceImpl.USER_WITH_USERNAME_EMAIL + "john already exists",
                response.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void saveSoftDeletedUserTest() {

        UserDto userDto = new UserDto();
        userDto.setUsername("john");

        User existingUser = new User();
        existingUser.setDeleted(true);

        when(userRepository.findByUsername("john")).thenReturn(existingUser);

        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                UserServiceImpl.USER_WITH_USERNAME_EMAIL +
                        "john has been soft deleted.(Rollback by changing status",
                response.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateSuccessSameUsernameTest() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("john");
        userDto.setName("John");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("john");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        doAnswer(invocation -> {
            UserDto source = invocation.getArgument(0);
            User target = invocation.getArgument(1);
            target.setUsername(source.getUsername());
            target.setName(source.getName());
            return null;
        }).when(modelMapper).map(any(UserDto.class), any(User.class));

        UserDto response = userService.update(userDto);

        Assertions.assertEquals("john", response.getUsername());

        verify(userRepository).save(existingUser);
    }

    @Test
    void updateSuccessDifferentUsernameAvailableTest() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("newuser");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("olduser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("newuser")).thenReturn(null);

        doAnswer(invocation -> {
            UserDto source = invocation.getArgument(0);
            User target = invocation.getArgument(1);
            target.setUsername(source.getUsername());
            return null;
        }).when(modelMapper).map(any(UserDto.class), any(User.class));

        UserDto response = userService.update(userDto);

        Assertions.assertEquals("newuser", response.getUsername());

        verify(userRepository).save(existingUser);
    }

    @Test
    void updateFailureUserNotFoundTest() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("john");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                UserServiceImpl.USER_WITH_USERNAME_EMAIL + "john not found",
                response.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateFailureUsernameAlreadyExistsTest() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("john");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("admin");

        User duplicateUser = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("john")).thenReturn(duplicateUser);

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                UserServiceImpl.USER_WITH_USERNAME_EMAIL + "john already exists",
                response.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteTest() {

        User user = new User();
        user.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(user);

        boolean result = userService.delete("john");

        Assertions.assertTrue(result);

        verify(userRepository).save(user);
    }

    @Test
    void deleteUserNotFoundTest() {

        when(userRepository.findByUsername("john")).thenReturn(null);

        boolean result = userService.delete("john");

        Assertions.assertFalse(result);

        verify(userRepository, never()).save(any(User.class));
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

        when(userRepository.findByDeletedFalse(any(PageRequest.class))).thenReturn(userPage);
        when(modelMapper.map(eq(users), any(Type.class))).thenReturn(userDtos);

        WsDto<UserDto> response = userService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("john", response.getDtoList().get(0).getUsername());
        Assertions.assertEquals(1L, response.getTotalRecords());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllEmptyTest() {

        List<User> users = List.of();
        List<UserDto> userDtos = List.of();

        Page<User> userPage = new PageImpl<>(users);

        when(userRepository.findByDeletedFalse(any(PageRequest.class))).thenReturn(userPage);
        when(modelMapper.map(eq(users), any(Type.class))).thenReturn(userDtos);

        WsDto<UserDto> response = userService.findAll(PageRequest.of(0, 10));

        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0L, response.getTotalRecords());
    }

    @Test
    void userDtoNoArgsConstructorGetterSetterTest() {

        UserDto dto = new UserDto();
        dto.setName("John");
        dto.setUsername("john123");
        dto.setPhoneNo("9999999999");
        dto.setRoles(List.of("ADMIN"));
        dto.setPassword("password");
        dto.setToken("token");

        Assertions.assertEquals("John", dto.getName());
        Assertions.assertEquals("john123", dto.getUsername());
        Assertions.assertEquals("9999999999", dto.getPhoneNo());
        Assertions.assertEquals(List.of("ADMIN"), dto.getRoles());
        Assertions.assertEquals("password", dto.getPassword());
        Assertions.assertEquals("token", dto.getToken());
    }

    @Test
    void userDtoTokenConstructorTest() {

        UserDto dto = new UserDto("jwt-token");

        Assertions.assertEquals("jwt-token", dto.getToken());
    }

}
