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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
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
    void saveTest_Success() {

        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setPassword("plain");

        User user = new User();

        Mockito.when(
                userRepository.findByUsernameAndDeletedFalse("admin")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(dto, User.class)
        ).thenReturn(user);

        Mockito.when(
                passwordEncoder.encode("plain")
        ).thenReturn("encodedPassword");

        UserDto response = userService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(userRepository).save(user);
        Assertions.assertEquals("encodedPassword", user.getPassword());
        Assertions.assertFalse(user.getDeleted());
    }

    @Test
    void saveTest_Failure_WhenUserExists() {

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        Mockito.when(
                userRepository.findByUsernameAndDeletedFalse("admin")
        ).thenReturn(new User());

        UserDto response = userService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "User with username/email - admin already exists",
                response.getMessage()
        );

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByUserNameTest() {

        User user = new User();
        user.setUsername("admin");

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        Mockito.when(
                userRepository.findByUsernameAndDeletedFalse("admin")
        ).thenReturn(user);

        Mockito.when(
                modelMapper.map(user, UserDto.class)
        ).thenReturn(dto);

        UserDto response = userService.findByUserName("admin");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("admin", response.getUsername());
    }

    @Test
    void updateTest_Success() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("admin");

        Mockito.when(
                userRepository.findById(1L)
        ).thenReturn(Optional.of(existingUser));

        UserDto response = userService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper)
                .map(dto, existingUser);

        Mockito.verify(userRepository)
                .save(existingUser);
    }

    @Test
    void updateTest_Failure_WhenUserNotFound() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        Mockito.when(
                userRepository.findById(1L)
        ).thenReturn(Optional.empty());

        UserDto response = userService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "User with username/email - admin not found",
                response.getMessage()
        );

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Failure_WhenUsernameAlreadyExists() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newuser");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("olduser");

        Mockito.when(
                userRepository.findById(1L)
        ).thenReturn(Optional.of(existingUser));

        Mockito.when(
                userRepository.findByUsernameAndDeletedFalse("newuser")
        ).thenReturn(new User());

        UserDto response = userService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "User with username/email - newuser already exists",
                response.getMessage()
        );

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllTest() {

        List<User> users = List.of(
                new User(),
                new User()
        );

        List<UserDto> dtoList = List.of(
                new UserDto(),
                new UserDto()
        );

        Mockito.when(
                userRepository.findByDeletedFalse()
        ).thenReturn(users);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(users),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<UserDto> response = userService.findAll();

        Assertions.assertEquals(2, response.size());
    }

    @Test
    void findAll_WithPagination_ShouldReturnUserDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        List<User> users = List.of(new User());

        Page<User> page =
                new PageImpl<>(users, pageable, 1);

        List<UserDto> userDtos =
                List.of(new UserDto());

        Type listType =
                new TypeToken<List<UserDto>>() {}.getType();

        Mockito.when(
                userRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(users, listType)
        ).thenReturn(userDtos);

        WsDto<UserDto> response =
                userService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );

        Assertions.assertEquals(
                1,
                response.getTotalPage()
        );

        Assertions.assertEquals(
                0,
                response.getPage()
        );
    }

    @Test
    void findAll_WithSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();

        Page<User> page =
                new PageImpl<>(List.of(user));

        Mockito.when(
                userRepository
                        .findByNameContainingIgnoreCaseAndDeletedFalse(
                                "john",
                                pageable
                        )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(user, UserDto.class)
        ).thenReturn(new UserDto());

        Page<UserDto> response =
                userService.findAll("john", pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void findAll_WithoutSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();

        Page<User> page =
                new PageImpl<>(List.of(user));

        Mockito.when(
                userRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(user, UserDto.class)
        ).thenReturn(new UserDto());

        Page<UserDto> response =
                userService.findAll("", pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void deleteTest() {

        User user = new User();
        user.setDeleted(false);

        Mockito.when(
                userRepository.findByUsernameAndDeletedFalse("admin")
        ).thenReturn(user);

        userService.delete("admin");

        Assertions.assertTrue(user.getDeleted());

        Mockito.verify(userRepository)
                .save(user);
    }

    @Test
    void deleteTest_UserNotFound() {

        Mockito.when(
                userRepository.findByUsernameAndDeletedFalse("admin")
        ).thenReturn(null);

        userService.delete("admin");

        Mockito.verify(
                userRepository,
                Mockito.never()
        ).save(Mockito.any());
    }
}