package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
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
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByUserNameTest() {
        User user = new User();
        user.setUsername("test@mail.com");

        UserDto dto = new UserDto();
        dto.setUsername("test@mail.com");

        Mockito.when(userRepository.findByUsername("test@mail.com"))
                .thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);

        UserDto response = userService.findByUserName("test@mail.com");

        Assertions.assertEquals("test@mail.com", response.getUsername());
    }

    @Test
    void saveSuccessTest() {
        UserDto dto = new UserDto();
        dto.setUsername("test@mail.com");
        dto.setPassword("rawPwd");

        User user = new User();
        user.setUsername("test@mail.com");

        Mockito.when(userRepository.findByUsername("test@mail.com"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, User.class))
                .thenReturn(user);

        Mockito.when(passwordEncoder.encode("rawPwd"))
                .thenReturn("encodedPwd");

        Mockito.when(userRepository.save(user))
                .thenReturn(user);

        UserDto response = userService.save(dto);

        Assertions.assertEquals("test@mail.com", response.getUsername());
    }

    @Test
    void saveFailureDuplicateUserTest() {
        UserDto dto = new UserDto();
        dto.setUsername("test@mail.com");

        Mockito.when(userRepository.findByUsername("test@mail.com"))
                .thenReturn(new User());

        UserDto response = userService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Email test@mail.com already exists",
                response.getMessage()
        );
    }

    @Test
    void updateUserNotFoundTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("test@mail.com");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserDto response = userService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Email - test@mail.com not found",
                response.getMessage()
        );
    }

    @Test
    void updateUsernameConflictTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("new@mail.com");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("old@mail.com");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByUsername("new@mail.com"))
                .thenReturn(new User());

        UserDto response = userService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Email - new@mail.com already exists",
                response.getMessage()
        );
    }

    @Test
    void updateSuccessTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("same@mail.com");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("same@mail.com");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        Mockito.when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        UserDto response = userService.update(dto);

        Assertions.assertEquals("same@mail.com", response.getUsername());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(userRepository)
                .deleteByUsername("test@mail.com");

        userService.delete("test@mail.com");

        Mockito.verify(userRepository)
                .deleteByUsername("test@mail.com");
    }

    @Test
    void findAllWithPageableTest() {

        User user = new User();
        user.setUsername("test@mail.com");

        UserDto userDto = new UserDto();
        userDto.setUsername("test@mail.com");

        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<User> userPage = new PageImpl<>(users);

        Mockito.when(userRepository.findAll(pageable))
                .thenReturn(userPage);

        Mockito.when(modelMapper.map(
                Mockito.eq(users),
                Mockito.any(Type.class)
        )).thenReturn(userDtos);

        PaginationResponseDto<UserDto> response =
                userService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());

        Assertions.assertEquals(
                "test@mail.com",
                response.getDtoList().get(0).getUsername()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        User user = new User();
        user.setUsername("test@mail.com");

        UserDto userDto = new UserDto();
        userDto.setUsername("test@mail.com");

        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        Mockito.when(userRepository.findAll())
                .thenReturn(users);

        Mockito.when(modelMapper.map(
                Mockito.eq(users),
                Mockito.any(Type.class)
        )).thenReturn(userDtos);

        PaginationResponseDto<UserDto> response =
                userService.findAll(null);
    }
}