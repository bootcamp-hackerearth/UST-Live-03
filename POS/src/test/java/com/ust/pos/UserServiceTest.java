package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
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
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void saveTest() {

        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");
        userDto.setPassword("123");

        Mockito.when(userRepository.findByUsername("Admin"))
                .thenReturn(null);

        User user = new User();

        Mockito.when(modelMapper.map(userDto, User.class))
                .thenReturn(user);

        Mockito.when(passwordEncoder.encode("123"))
                .thenReturn("encoded123");

        Mockito.when(userRepository.save(user))
                .thenReturn(user);

        UserDto response = userService.save(userDto);

        Assertions.assertEquals("Admin", response.getUsername());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(userRepository).save(user);
        Assertions.assertEquals("encoded123", user.getPassword());
        Assertions.assertFalse(user.getIsDeleted());
    }

    @Test
    void saveTestFailure() {

        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");

        User user = new User();
        user.setIsDeleted(false);

        Mockito.when(userRepository.findByUsername("Admin"))
                .thenReturn(user);

        UserDto response = userService.save(userDto);

        Assertions.assertEquals("Admin", response.getUsername());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveDeletedUserFailureTest() {

        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");

        User user = new User();
        user.setIsDeleted(true);

        Mockito.when(userRepository.findByUsername("Admin"))
                .thenReturn(user);

        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(
                response.getMessage().contains("deleted")
        );
    }

    @Test
    void findByUsernameTest() {

        User user = new User();
        user.setUsername("Admin");

        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");

        Mockito.when(userRepository.findByUsername("Admin"))
                .thenReturn(user);

        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(userDto);

        UserDto response = userService.findByUserName("Admin");

        Assertions.assertEquals("Admin", response.getUsername());
    }

    @Test
    void updateTest() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("Admin");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("Admin");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        Mockito.when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        UserDto response = userService.update(userDto);

        Assertions.assertNull(response.getMessage());

        Mockito.verify(modelMapper)
                .map(userDto, existingUser);

        Mockito.verify(userRepository)
                .save(existingUser);
    }

    @Test
    void updateTestFailure() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("Admin");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
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
                .thenReturn(Optional.of(existingUser));

        Mockito.when(userRepository.findByUsername("NewAdmin"))
                .thenReturn(duplicateUser);

        UserDto response = userService.update(userDto);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
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
                .thenReturn(Optional.of(existingUser));

        Mockito.when(userRepository.findByUsername("NewAdmin"))
                .thenReturn(null);

        Mockito.when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        UserDto response = userService.update(userDto);

        Assertions.assertNull(response.getMessage());

        Mockito.verify(userRepository)
                .save(existingUser);
    }

    @Test
    void deleteTest() {

        User user = new User();
        user.setUsername("Admin");
        user.setStatus(true);
        user.setIsDeleted(false);

        Mockito.when(userRepository.findByUsername("Admin"))
                .thenReturn(user);

        Mockito.when(userRepository.save(user))
                .thenReturn(user);

        UserDto response = userService.delete("Admin");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "User deleted successfully",
                response.getMessage()
        );

        Assertions.assertTrue(user.getIsDeleted());
        Assertions.assertFalse(user.getStatus());

        Mockito.verify(userRepository)
                .save(user);
    }

    @Test
    void deleteNotFoundTest() {

        Mockito.when(userRepository.findByUsername("Admin"))
                .thenReturn(null);

        UserDto response = userService.delete("Admin");

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "User with username/email - Admin not found",
                response.getMessage()
        );
    }

    @Test
    void findAllTest() {

        User user = new User();
        user.setUsername("Admin");

        UserDto userDto = new UserDto();
        userDto.setUsername("Admin");

        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> userPage =
                new PageImpl<>(users, pageable, users.size());

        Mockito.when(
                userRepository.findByIsDeleted(
                        false,
                        pageable
                )
        ).thenReturn(userPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(users),
                        Mockito.any(Type.class)
                )
        ).thenReturn(userDtos);

        PaginatedResponseDto<UserDto> response =
                userService.findAll(pageable);

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(
                "Admin",
                response.getItems().get(0).getUsername()
        );

        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
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

        Mockito.when(
                userRepository.findByStatusAndIsDeleted(
                        true,
                        false
                )
        ).thenReturn(users);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(users),
                        Mockito.any(Type.class)
                )
        ).thenReturn(userDtos);

        List<UserDto> response =
                userService.findAllActive();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(
                "Admin",
                response.get(0).getUsername()
        );
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

        Mockito.verify(userRepository)
                .save(user);
    }
}