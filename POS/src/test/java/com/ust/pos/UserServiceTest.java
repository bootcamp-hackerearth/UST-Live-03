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

    // FIND BY USERNAME

    @Test
    void findByUserName_Success() {

        User user = new User();
        user.setUsername("admin");

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        Mockito.when(userRepository.findByUsernameAndIsDeleteFalse("admin"))
                .thenReturn(user);

        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);

        UserDto result = userService.findByUserName("admin");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("admin", result.getUsername());
    }

    @Test
    void findByUserName_WhenNotFound_ShouldReturnNull() {

        Mockito.when(userRepository.findByUsernameAndIsDeleteFalse("admin"))
                .thenReturn(null);

        UserDto result = userService.findByUserName("admin");

        Assertions.assertNull(result);
    }

    // SAVE

    @Test
    void save_Success() {

        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setPassword("123");

        User entity = new User();

        Mockito.when(userRepository.findByUsernameAndIsDeleteFalse("admin"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, User.class))
                .thenReturn(entity);

        Mockito.when(passwordEncoder.encode("123"))
                .thenReturn("ENC123");

        Mockito.when(userRepository.save(entity))
                .thenReturn(entity);

        UserDto result = userService.save(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(passwordEncoder).encode("123");
        Mockito.verify(userRepository).save(entity);
    }

    @Test
    void save_WhenExists_ShouldFail() {

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        Mockito.when(userRepository.findByUsernameAndIsDeleteFalse("admin"))
                .thenReturn(new User());

        UserDto result = userService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    // UPDATE

    @Test
    void update_Success() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        User existing = new User();
        existing.setUsername("admin");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.doNothing().when(modelMapper).map(dto, existing);

        Mockito.when(userRepository.save(existing))
                .thenReturn(existing);

        UserDto result = userService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(userRepository).save(existing);
    }

    @Test
    void update_WhenNotFound_ShouldFail() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void update_WhenDuplicateUsername_ShouldFail() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newUser");

        User existing = new User();
        existing.setUsername("oldUser");

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(userRepository.findByUsernameAndIsDeleteFalse("newUser"))
                .thenReturn(new User());

        UserDto result = userService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    // DELETE

    @Test
    void delete_Success() {

        User user = new User();
        user.setUsername("admin");

        Mockito.when(userRepository.findByUsernameAndIsDeleteFalse("admin"))
                .thenReturn(user);

        Mockito.when(userRepository.save(user)).thenReturn(user);

        userService.delete("admin");

        Assertions.assertTrue(user.isDelete());

        Mockito.verify(userRepository).save(user);
    }

    @Test
    void delete_WhenNotFound_ShouldDoNothing() {

        Mockito.when(userRepository.findByUsernameAndIsDeleteFalse("admin"))
                .thenReturn(null);

        userService.delete("admin");

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    // FIND ALL

    @Test
    void findAll_Success() {

        List<User> entities = List.of(new User());
        List<UserDto> dtos = List.of(new UserDto());

        Type type = new TypeToken<List<UserDto>>() {
        }.getType();

        Mockito.when(userRepository.findByIsDeleteFalse())
                .thenReturn(entities);

        Mockito.when(modelMapper.map(entities, type))
                .thenReturn(dtos);

        List<UserDto> result = userService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // PAGINATION

    @Test
    void findAll_Pageable_NoSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setUsername("admin");

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        Page<User> page = new PageImpl<>(List.of(user));

        Mockito.when(userRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);

        Page<UserDto> result = userService.findAll(pageable);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findAllPageableWithSearchTest() {
        Pageable pageable =
                PageRequest.of(0, 10);
        User user = new User();
        Page<User> page =
                new PageImpl<>(List.of(user));
        Mockito.when(userRepository.findAll(
                        Mockito.<Specification<User>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        Page<UserDto> result =
                userService.findAll(pageable, "Admin");
        Assertions.assertEquals(
                1,
                result.getContent().size()
        );
        Mockito.verify(userRepository)
                .findAll(
                        Mockito.<Specification<User>>any(),
                        Mockito.eq(pageable)
                );
    }

    @Test
    void findAll_WithBlankSearch_ShouldFallback() {

        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setUsername("admin");

        UserDto dto = new UserDto();
        dto.setUsername("admin");

        Page<User> page = new PageImpl<>(List.of(user));

        Mockito.when(userRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(user, UserDto.class))
                .thenReturn(dto);

        Page<UserDto> result = userService.findAll(pageable, " ");

        Assertions.assertEquals(1, result.getContent().size());
    }
}