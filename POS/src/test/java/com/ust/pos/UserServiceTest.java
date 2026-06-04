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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findAll_success() {

        User user = new User();
        UserDto dto = new UserDto();

        Page<User> page = new PageImpl<>(List.of(user));

        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(user)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<UserDto> result =
                userService.findAll(Mockito.mock(Pageable.class));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void findByUserName_success() {

        User user = new User();
        user.setUsername("john");

        UserDto dto = new UserDto();
        dto.setUsername("john");

        Mockito.when(userRepository.findByUsername("john"))
                .thenReturn(user);

        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);

        UserDto result = userService.findByUserName("john");

        Assertions.assertEquals("john", result.getUsername());
    }

    @Test
    void save_success() {

        UserDto input = new UserDto();
        input.setUsername("john");
        input.setPassword("123");

        Mockito.when(userRepository.findByUsername("john"))
                .thenReturn(null);

        User entity = new User();

        Mockito.when(modelMapper.map(input, User.class))
                .thenReturn(entity);

        Mockito.when(passwordEncoder.encode("123"))
                .thenReturn("encodedPassword");

        Mockito.when(userRepository.save(entity))
                .thenReturn(entity);

        UserDto result = userService.save(input);

        Assertions.assertEquals("john", result.getUsername());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        UserDto input = new UserDto();
        input.setUsername("john");

        Mockito.when(userRepository.findByUsername("john"))
                .thenReturn(new User());

        UserDto result = userService.save(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void update_success() {

        UserDto input = new UserDto();
        input.setId(1L);
        input.setUsername("john");

        User existing = new User();
        existing.setId(1L);
        existing.setUsername("john");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(userRepository.save(existing))
                .thenReturn(existing);

        UserDto result = userService.update(input);

        Assertions.assertEquals("john", result.getUsername());
    }

    @Test
    void update_failure_notFound() {

        UserDto input = new UserDto();
        input.setId(1L);
        input.setUsername("john");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserDto result = userService.update(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void update_failure_duplicate_username() {

        UserDto input = new UserDto();
        input.setId(1L);
        input.setUsername("john");

        User existing = new User();
        existing.setId(1L);
        existing.setUsername("oldUser");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(userRepository.findByUsername("john"))
                .thenReturn(new User());

        UserDto result = userService.update(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }


    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(userRepository).deleteByUsername("john");

        userService.delete("john");

        Mockito.verify(userRepository)
                .deleteByUsername("john");
    }


    @Test
    void changeToggleStatus_success() {

        User user = new User();
        user.setId(1L);
        user.setStatus(false);

        UserDto dto = new UserDto();

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Mockito.when(userRepository.save(user))
                .thenReturn(user);

        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);

        UserDto result =
                userService.changeToggleStatus(1L, true);

        Assertions.assertTrue(user.isStatus());
        Assertions.assertNotNull(result);
    }
}