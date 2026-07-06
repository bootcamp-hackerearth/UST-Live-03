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
import org.springframework.data.jpa.domain.Specification;
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
        userDto.setUsername("chaila@gmail.com");
        userDto.setPassword("12345");
        Mockito.when(userRepository.findByUsername("chaila@gmail.com")).thenReturn(null);
        User user = new User();
        Mockito.when(modelMapper.map(userDto, User.class)).thenReturn(user);
        Mockito.when(passwordEncoder.encode("12345")).thenReturn("encodedPwd");
        Mockito.when(userRepository.save(user)).thenReturn(user);
        UserDto response = userService.save(userDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("User added successfully", response.getMessage());
    }

    @Test
    void saveTestFailure() {
        UserDto userDto = new UserDto();
        userDto.setUsername("chaila@gmail.com");
        User user = new User();
        Mockito.when(userRepository.findByUsername("chaila@gmail.com")).thenReturn(user);
        UserDto response = userService.save(userDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestFailureForSoftDeletedUser() {
        UserDto userDto = new UserDto();
        userDto.setUsername("chaila@gmail.com");
        User deletedUser = new User();
        deletedUser.setDeleted(true);
        Mockito.when(userRepository.findByUsername("chaila@gmail.com")).thenReturn(deletedUser);
        UserDto response = userService.save(userDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByUsernameTest() {
        User user = new User();
        user.setUsername("chaila@gmail.com");
        UserDto userDto = new UserDto();
        userDto.setUsername("chaila@gmail.com");
        Mockito.when(userRepository.findByUsername("chaila@gmail.com")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);
        UserDto response = userService.findByUserName("chaila@gmail.com");
        Assertions.assertEquals("chaila@gmail.com", response.getUsername());
    }

    @Test
    void updateTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("chailashree@gmail.com");
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("chailashree@gmail.com");
        existingUser.setPassword("encodedPassword");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(existingUser)).thenReturn(existingUser);
        UserDto response = userService.update(userDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("User updated successfully", response.getMessage());
    }

    @Test
    void updateTestFailure() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("chailashree@gmail.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserDto response = userService.update(userDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestFailureUsernameAlreadyExists() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("new@gmail.com");
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("old@gmail.com");
        User duplicateUser = new User();
        duplicateUser.setUsername("new@gmail.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByUsername("new@gmail.com")).thenReturn(duplicateUser);
        UserDto response = userService.update(userDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
    }

    @Test
    void updateTestPasswordShouldRemainUnchanged() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("chaila@gmail.com");
        userDto.setPassword("newPassword");
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("chaila@gmail.com");
        existingUser.setPassword("encodedOldPassword");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(existingUser)).thenReturn(existingUser);
        UserDto response = userService.update(userDto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("encodedOldPassword", existingUser.getPassword());
    }

    @Test
    void deleteTest() {
        User user = new User();
        user.setUsername("chaila@gmail.com");
        user.setDeleted(false);
        Mockito.when(userRepository.findByUsername("chaila@gmail.com")).thenReturn(user);
        userService.delete("chaila@gmail.com");
        Assertions.assertTrue(user.isDeleted());
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(userRepository.findByUsername("chaila@gmail.com")).thenReturn(null);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> userService.delete("chaila@gmail.com"));
        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findAllWithPageableTest() {
        User user = new User();
        user.setIdentifier("chaila@gmail.com");
        UserDto dto = new UserDto();
        dto.setIdentifier("chaila@gmail.com");
        List<User> users = List.of(user);
        List<UserDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<User> userPage = new PageImpl<>(users);
        Mockito.when(userRepository.findByDeletedFalse(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(Mockito.eq(users), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<UserDto> response = userService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("chaila@gmail.com", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        User user = new User();
        user.setIdentifier("chaila@gmail.com");
        UserDto dto = new UserDto();
        dto.setIdentifier("chaila@gmail.com");
        List<User> users = List.of(user);
        List<UserDto> dtos = List.of(dto);
        Mockito.when(userRepository.findAll()).thenReturn(users);
        Mockito.when(modelMapper.map(Mockito.eq(users), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<UserDto> response = userService.findAll(null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("chaila@gmail.com", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findAllWithSpecificationTest() {
        User user = new User();
        user.setUsername("john");
        UserDto dto = new UserDto();
        dto.setUsername("john");
        List<User> users = List.of(user);
        List<UserDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Specification<User> specification = Mockito.mock(Specification.class);
        Page<User> page = new PageImpl<>(users);
        Mockito.when(userRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(users), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<UserDto> response = userService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("john", response.getDtoList().get(0).getUsername());
        Assertions.assertEquals(1, response.getTotalRecords());
        Mockito.verify(userRepository).findAll(specification, pageable);
    }

    @Test
    void findAllWithSpecificationNoDataTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Specification<User> specification = Mockito.mock(Specification.class);
        Page<User> page = new PageImpl<>(List.of());
        Mockito.when(userRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());
        PaginationResponseDto<UserDto> response = userService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Mockito.verify(userRepository).findAll(specification, pageable);
    }
}