package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        UserDto dto = new UserDto();
        dto.setUsername("admin@test.com");
        dto.setPassword("password");

        User user = new User();
        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, User.class)).thenReturn(user);
        Mockito.when(passwordEncoder.encode("password")).thenReturn("encodedPwd");

        UserDto response = userService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("admin@test.com", response.getIdentifier());
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void saveFailure_existingActive() {
        UserDto dto = new UserDto();
        dto.setUsername("admin@test.com");

        User existing = new User();
        existing.setDeleted(false);

        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(existing);
        UserDto response = userService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "admin@test.com already exists",
                response.getMessage()
        );
    }

    @Test
    void saveFailure_softDeleted() {
        UserDto dto = new UserDto();
        dto.setUsername("admin@test.com");

        User existing = new User();
        existing.setDeleted(true);
        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(existing);
        UserDto response = userService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByUsernameTest() {
        User user = new User();
        user.setUsername("admin@test.com");
        UserDto dto = new UserDto();
        dto.setUsername("admin@test.com");

        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(dto);
        UserDto response = userService.findByUserName("admin@test.com");
        Assertions.assertEquals("admin@test.com", response.getUsername());
    }

    @Test
    void updateTestSuccess() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin@test.com");

        User existing = new User();
        existing.setId(1L);
        existing.setUsername("admin@test.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.save(existing)).thenReturn(existing);
        UserDto response = userService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(userRepository).save(existing);
    }

    @Test
    void updateFailure_notFound() {
        UserDto dto = new UserDto();
        dto.setId(99L);
        dto.setUsername("admin@test.com");

        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());
        UserDto response = userService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "admin@test.com not found", response.getMessage());
    }

    @Test
    void updateFailure_duplicateUsername() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("new@test.com");

        User existing = new User();
        existing.setId(1L);
        existing.setUsername("old@test.com");

        User another = new User();
        another.setUsername("new@test.com");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.findByUsername("new@test.com")).thenReturn(another);
        UserDto response = userService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "new@test.com already exists", response.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        User user = new User();
        user.setDeleted(false);

        Mockito.when(userRepository.findByUsername("admin@test.com")).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);

        userService.delete("admin@test.com");
        Mockito.verify(userRepository).findByUsername("admin@test.com");
        Mockito.verify(userRepository).save(user);
        Assertions.assertTrue(user.isDeleted());
    }

    @Test
    void findAllTest() {
        User user = new User();
        user.setUsername("admin@test.com");

        UserDto dto = new UserDto();
        dto.setUsername("admin@test.com");

        List<User> list = List.of(user);
        Page<User> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(userRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        WsDto<UserDto> response = userService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("admin@test.com", response.getDtoList().get(0).getUsername());
        Assertions.assertEquals(1, response.getTotalRecords());
    }
}