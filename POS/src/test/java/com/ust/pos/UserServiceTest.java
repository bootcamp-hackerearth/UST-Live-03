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

    // SAVE

    @Test
    void saveTest_Success() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setPassword("plain");

        User user = new User();
        user.setUsername("admin");

        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, User.class))
                .thenReturn(user);

        Mockito.when(passwordEncoder.encode("plain"))
                .thenReturn("encoded");

        Mockito.when(userRepository.save(user))
                .thenReturn(user);

        UserDto response = userService.save(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("admin", response.getUsername());

        Mockito.verify(userRepository).save(user);
    }

    @Test
    void saveTest_Failure_WhenUserExists() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");

        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(new User());

        UserDto response = userService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    // FIND BY USERNAME

    @Test
    void findByUserNameTest() {
        User user = new User();
        user.setUsername("admin");

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(user);

        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);

        UserDto response = userService.findByUserName("admin");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("admin", response.getUsername());
    }

    @Test
    void findByUserName_WhenUserNotFound() {
        Mockito.when(userRepository.findByUsername("admin"))
                .thenReturn(null);

        UserDto response = userService.findByUserName("admin");

        Assertions.assertNull(response);
    }

    // UPDATE

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

        Mockito.when(userRepository.save(existing))
                .thenReturn(existing);

        UserDto response = userService.update(dto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("admin", response.getUsername());

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
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void updateTest_Failure_WhenUsernameAlreadyExists() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newname");

        User existing = new User();
        existing.setId(1L);
        existing.setUsername("oldname");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(userRepository.findByUsername("newname"))
                .thenReturn(new User());

        UserDto response = userService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    // FIND ALL

    @Test
    void findAllTest() {
        List<User> users = List.of(new User());
        List<UserDto> dtos = List.of(new UserDto());

        Type listType = new TypeToken<List<UserDto>>() {
        }.getType();

        Mockito.when(userRepository.findAll())
                .thenReturn(users);

        Mockito.when(modelMapper.map(users, listType))
                .thenReturn(dtos);

        List<UserDto> response = userService.findAll();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
    }

    // DELETE

    @Test
    void deleteTest() {
        Mockito.doNothing()
                .when(userRepository)
                .deleteByUsername("admin");

        userService.delete("admin");

        Mockito.verify(userRepository)
                .deleteByUsername("admin");
    }

    // PAGINATION

    @Test
    void findAll_WithPagination_ShouldReturnUserDtos() {

        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setId(1L);
        user.setUsername("admin");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        Page<User> userPage = new PageImpl<>(List.of(user));

        Mockito.when(userRepository.findAll(pageable))
                .thenReturn(userPage);

        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);

        Page<UserDto> response = userService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals("admin",
                response.getContent().get(0).getUsername());

        Mockito.verify(userRepository)
                .findAll(pageable);

        Mockito.verify(modelMapper)
                .map(user, UserDto.class);
    }
}