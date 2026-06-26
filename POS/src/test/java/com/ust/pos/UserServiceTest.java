package com.ust.pos;

import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.UserDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void saveTestSuccess() {

        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");
        userDto.setPassword("password");
        userDto.setSuccess(true);

        User user = new User();

        Mockito.when(userRepository.findByUsername("admin@test.com"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(userDto, User.class))
                .thenReturn(user);

        Mockito.when(passwordEncoder.encode("password"))
                .thenReturn("encodedPwd");

        Mockito.when(userRepository.save(user))
                .thenReturn(user);

        UserDto response = userService.save(userDto);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("encodedPwd", user.getPassword());
    }

    @Test
    void saveTestFailure_UserAlreadyExists() {

        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");

        User existing = new User();
        existing.setDeleted(false);

        Mockito.when(userRepository.findByUsername("admin@test.com"))
                .thenReturn(existing);

        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestFailure_SoftDeletedUser() {

        UserDto userDto = new UserDto();
        userDto.setUsername("admin@test.com");

        User deletedUser = new User();
        deletedUser.setDeleted(true);

        Mockito.when(userRepository.findByUsername("admin@test.com"))
                .thenReturn(deletedUser);

        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "User with identifier - admin@test.com has been soft deleted. Restore it by changing status.",
                response.getMessage()
        );
    }

    @Test
    void findByUsernameTest() {

        User user = new User();
        user.setUsername("admin@test.com");

        UserDto dto = new UserDto();
        dto.setUsername("admin@test.com");

        Mockito.when(userRepository.findByUsername("admin@test.com"))
                .thenReturn(user);

        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);

        UserDto response =
                userService.findByUserName("admin@test.com");

        Assertions.assertEquals(
                "admin@test.com",
                response.getUsername()
        );
    }

    @Test
    void updateTestSuccess() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin@test.com");
        dto.setSuccess(true);

        User existing = new User();
        existing.setId(1L);
        existing.setUsername("admin@test.com");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(userRepository.save(existing))
                .thenReturn(existing);

        UserDto response = userService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(userRepository)
                .save(existing);
    }

    @Test
    void updateTestFailure_UserNotFound() {

        UserDto dto = new UserDto();
        dto.setId(100L);
        dto.setUsername("admin@test.com");

        Mockito.when(userRepository.findById(100L))
                .thenReturn(Optional.empty());

        UserDto response = userService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                UserServiceImpl.USER_WITH_USERNAME_EMAIL
                        + "admin@test.com not found",
                response.getMessage()
        );
    }

    @Test
    void updateTestFailure_DuplicateUsername() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("new@test.com");

        User existing = new User();
        existing.setId(1L);
        existing.setUsername("old@test.com");

        User duplicate = new User();
        duplicate.setUsername("new@test.com");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(userRepository.findByUsername("new@test.com"))
                .thenReturn(duplicate);

        UserDto response = userService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteTest() {

        User user = new User();
        user.setUsername("admin@test.com");
        user.setDeleted(false);
        user.setStatus(true);

        Mockito.when(userRepository.findByIdentifier("admin@test.com"))
                .thenReturn(user);

        Mockito.when(userRepository.save(user))
                .thenReturn(user);

        boolean result =
                userService.delete("admin@test.com");

        Assertions.assertTrue(result);
        Assertions.assertTrue(user.getDeleted());
        Assertions.assertFalse(user.getStatus());

        Mockito.verify(userRepository)
                .save(user);
    }

    @Test
    void deleteFailureTest() {

        Mockito.when(userRepository.findByIdentifier("admin@test.com"))
                .thenReturn(null);

        boolean result =
                userService.delete("admin@test.com");

        Assertions.assertFalse(result);

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllPaginationTest() {

        User user = new User();
        user.setUsername("admin@test.com");

        UserDto dto = new UserDto();
        dto.setUsername("admin@test.com");

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page =
                new PageImpl<>(List.of(user), pageable, 1);

        Mockito.when(userRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        Type listType =
                new TypeToken<List<UserDto>>() {
                }.getType();

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(page.getContent()),
                        Mockito.eq(listType)
                )
        ).thenReturn(List.of(dto));

        PageDto<UserDto> response =
                userService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(
                "admin@test.com",
                response.getDtoList().get(0).getUsername()
        );
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}