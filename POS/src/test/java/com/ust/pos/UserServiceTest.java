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
    void saveFailureSoftDeletedUserTest() {

        UserDto dto = new UserDto();
        dto.setUsername("test@mail.com");

        User existingUser = new User();
        existingUser.setUsername("test@mail.com");
        existingUser.setDeleted(true);

        Mockito.when(
                userRepository.findByUsername("test@mail.com")
        ).thenReturn(existingUser);

        UserDto response =
                userService.save(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "User test@mail.com has been deleted. Please contact the administrator.",
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
        existingUser.setPassword("oldEncodedPassword");

        Mockito.when(
                userRepository.findById(1L)
        ).thenReturn(Optional.of(existingUser));

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, existingUser);

        Mockito.when(
                userRepository.save(existingUser)
        ).thenReturn(existingUser);

        UserDto response =
                userService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals(
                "User updated successfully",
                response.getMessage()
        );

        Assertions.assertEquals(
                "oldEncodedPassword",
                existingUser.getPassword()
        );

        Mockito.verify(userRepository)
                .save(existingUser);
    }

    @Test
    void updateFailureSoftDeletedUserTest() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("test@mail.com");

        User user = new User();
        user.setUsername("test@mail.com");
        user.setDeleted(true);

        Mockito.when(
                userRepository.findById(1L)
        ).thenReturn(Optional.of(user));

        UserDto response =
                userService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "User test@mail.com has been deleted. Please contact the administrator.",
                response.getMessage()
        );
    }

    @Test
    void updateFailureDuplicateUserSoftDeletedTest() {

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("new@mail.com");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("old@mail.com");

        User duplicateUser = new User();
        duplicateUser.setUsername("new@mail.com");
        duplicateUser.setDeleted(true);

        Mockito.when(
                userRepository.findById(1L)
        ).thenReturn(Optional.of(existingUser));

        Mockito.when(
                userRepository.findByUsername("new@mail.com")
        ).thenReturn(duplicateUser);

        UserDto response =
                userService.update(dto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "User new@mail.com has been deleted. Please contact the administrator.",
                response.getMessage()
        );
    }

    @Test
    void deleteTest() {

        User user = new User();
        user.setUsername("test@mail.com");
        user.setDeleted(false);

        Mockito.when(
                userRepository.findByUsername("test@mail.com")
        ).thenReturn(user);

        Mockito.when(
                userRepository.save(user)
        ).thenReturn(user);

        userService.delete("test@mail.com");

        Assertions.assertTrue(user.isDeleted());

        Mockito.verify(userRepository)
                .findByUsername("test@mail.com");

        Mockito.verify(userRepository)
                .save(user);
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

        Page<User> userPage =
                new PageImpl<>(users, pageable, users.size());

        Mockito.when(
                userRepository.findByIsDeletedFalse(pageable)
        ).thenReturn(userPage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(users),
                        Mockito.any(Type.class)
                )
        ).thenReturn(userDtos);

        PaginationResponseDto<UserDto> response =
                userService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(
                "test@mail.com",
                response.getDtoList().get(0).getUsername()
        );
    }

    @Test
    void findAllWithSpecificationTest() {

        User user = new User();
        user.setUsername("test@mail.com");

        UserDto dto = new UserDto();
        dto.setUsername("test@mail.com");

        Pageable pageable =
                PageRequest.of(0,5);

        Page<User> page =
                new PageImpl<>(
                        List.of(user),
                        pageable,
                        1
                );

        Specification<User> spec =
                (root, query, cb) -> null;

        Mockito.when(
                userRepository.findAll(
                        Mockito.eq(spec),
                        Mockito.eq(pageable)
                )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(List.of(user)),
                        Mockito.any(Type.class)
                )
        ).thenReturn(List.of(dto));

        PaginationResponseDto<UserDto> response =
                userService.findAll(
                        spec,
                        pageable
                );

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );

        Assertions.assertEquals(
                5,
                response.getSizePerPage()
        );
    }
}