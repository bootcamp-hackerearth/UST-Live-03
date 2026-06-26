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
import org.modelmapper.TypeToken;
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
    void saveTest_Success() {

        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setPassword("plain");
        User user = new User();
        Mockito.when(userRepository.findByUsernameAndDeletedFalse("admin"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, User.class))
                .thenReturn(user);
        Mockito.when(passwordEncoder.encode("plain"))
                .thenReturn("encoded");
        UserDto response = userService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Registration successful",
                response.getMessage()
        );
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void saveTest_Failure_WhenUserExists() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        Mockito.when(userRepository.findByUsernameAndDeletedFalse("admin"))
                .thenReturn(new User());
        UserDto response = userService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByUserNameTest() {
        User user = new User();
        user.setUsername("admin");
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        Mockito.when(userRepository.findByUsernameAndDeletedFalse("admin"))
                .thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);
        UserDto response = userService.findByUserName("admin");
        Assertions.assertEquals("admin",
                response.getUsername());
    }

    @Test
    void findByUserName_NotFound_Test() {
        Mockito.when(userRepository.findByUsernameAndDeletedFalse("admin"))
                .thenReturn(null);
        UserDto response = userService.findByUserName("admin");
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "User not found - admin",
                response.getMessage()
        );
    }

    @Test
    void updateTest_Success() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("admin");
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));
        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, existing);
        UserDto response = userService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "User updated successfully",
                response.getMessage()
        );
        Mockito.verify(userRepository).save(existing);
    }

    @Test
    void updateTest_Failure_WhenIdNotFound() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());
        UserDto response = userService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Failure_WhenUsernameExists() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newuser");
        User existing = new User();
        existing.setUsername("olduser");
        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));
        Mockito.when(
                userRepository.findByUsernameAndDeletedFalse("newuser")
        ).thenReturn(new User());
        UserDto response = userService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllTest() {
        List<User> users = List.of(new User());
        List<UserDto> dtos = List.of(new UserDto());
        Type listType =
                new TypeToken<List<UserDto>>() {
                }.getType();
        Mockito.when(userRepository.findByDeletedFalse())
                .thenReturn(users);
        Mockito.when(modelMapper.map(users, listType))
                .thenReturn(dtos);
        List<UserDto> response = userService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllWithPaginationAndSearchTest() {

        Pageable pageable = PageRequest.of(0, 5);
        User user = new User();
        user.setUsername("admin");
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        Page<User> page =
                new PageImpl<>(List.of(user), pageable, 1);
        Mockito.when(
                userRepository
                        .findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseAndDeletedFalse(
                                "admin",
                                "admin",
                                pageable
                        )
        ).thenReturn(page);
        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);
        Page<UserDto> response =
                userService.findAll(pageable, "admin");
        Assertions.assertEquals(1,
                response.getTotalElements());
    }

    @Test
    void findAllWithPaginationWithoutSearchTest() {

        Pageable pageable = PageRequest.of(0, 5);
        User user = new User();
        user.setUsername("admin");
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        Page<User> page =
                new PageImpl<>(List.of(user), pageable, 1);
        Mockito.when(
                userRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);
        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);
        Page<UserDto> response =
                userService.findAll(pageable, "");
        Assertions.assertEquals(1,
                response.getTotalElements());
    }

    @Test
    void deleteTest() {

        User user = new User();
        user.setDeleted(false);
        Mockito.when(
                userRepository.findByUsernameAndDeletedFalse("admin")
        ).thenReturn(user);
        userService.delete("admin");
        Assertions.assertTrue(user.isDeleted());
        Mockito.verify(userRepository)
                .save(user);
    }
}