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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    void findByUserNameTest() {
        User user = new User();
        user.setUsername("testUser");
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");

        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.findByUserName("testUser");

        Assertions.assertEquals("testUser", response.getUsername());
    }

    @Test
    void saveTest() {
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setPassword("plainPassword");

        User user = new User();

        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(null);
        Mockito.when(modelMapper.map(userDto, User.class)).thenReturn(user);
        Mockito.when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        Mockito.when(userRepository.save(user)).thenReturn(user);

        UserDto response = userService.save(userDto);

        Assertions.assertEquals("testUser", response.getUsername());
    }

    @Test
    void saveTestFailure() {
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");

        User existingUser = new User();
        existingUser.setUsername("testUser");
        existingUser.setDeleted(false);

        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(existingUser);

        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");

        User existingUser = new User();
        existingUser.setUsername("testUser");
        existingUser.setDeleted(true);

        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(existingUser);

        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTest() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testUser");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("testUser");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto response = userService.update(userDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestUsernameConflict() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("newUsername");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldUsername");

        User conflictingUser = new User();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByUsername("newUsername")).thenReturn(conflictingUser);

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTestFailureNotFound() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testUser");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTestSuccess() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("loggedInUser");

        User userEntity = new User();
        userEntity.setUsername("targetUser");
        UserDto userDto = new UserDto();

        Mockito.when(userRepository.findByUsername("targetUser")).thenReturn(userEntity);
        Mockito.when(modelMapper.map(userEntity, UserDto.class)).thenReturn(userDto);
        Mockito.when(userRepository.save(userEntity)).thenReturn(userEntity);

        UserDto response = userService.delete("targetUser");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("User deleted successfully", response.getMessage());
        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteTestSelfDeletionFailure() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("sameUser");

        User userEntity = new User();
        userEntity.setUsername("sameUser");
        UserDto userDto = new UserDto();

        Mockito.when(userRepository.findByUsername("sameUser")).thenReturn(userEntity);
        Mockito.when(modelMapper.map(userEntity, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.delete("sameUser");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cannot delete the logged in User", response.getMessage());
        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteTestUserNotFound() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        Mockito.when(userRepository.findByUsername("invalidUser")).thenReturn(null);

        UserDto response = userService.delete("invalidUser");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("User not found", response.getMessage());
        SecurityContextHolder.clearContext();
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        User user = new User();
        List<User> userList = List.of(user);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        UserDto userDto = new UserDto();
        List<UserDto> userDtos = List.of(userDto);

        Mockito.when(userRepository.findByDeletedFalse(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(Mockito.eq(userList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(userDtos);

        WsDto<UserDto> response = userService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void toggleTestActive() {
        User user = new User();
        user.setStatus(false);
        UserDto userDto = new UserDto();
        userDto.setStatus(true);

        Mockito.when(userRepository.findByUsername("testUser")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.toggleStatus("testUser");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void findByStatusTest() {
        User user = new User();
        List<User> userList = List.of(user);
        UserDto userDto = new UserDto();
        List<UserDto> userDtos = List.of(userDto);

        Mockito.when(userRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(userList);
        Mockito.when(modelMapper.map(Mockito.eq(userList), Mockito.any(java.lang.reflect.Type.class))).thenReturn(userDtos);

        List<UserDto> response = userService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void getUserDetailsTestSuccess() {
        User user = new User();
        user.setId(1L);
        user.setIdentifier("ID-01");
        user.setName("John Doe");
        user.setUsername("john");
        user.setPhoneNo("12345");
        user.setRoles(List.of("ROLE_USER"));

        Mockito.when(userRepository.findByUsername("john")).thenReturn(user);

        UserDto response = userService.getUserDetails("john");

        Assertions.assertEquals(1L, response.getId());
        Assertions.assertEquals("john", response.getUsername());
        Assertions.assertEquals("John Doe", response.getName());
    }

    @Test
    void getUserDetailsTestFailureNotFound() {
        Mockito.when(userRepository.findByUsername("invalid")).thenReturn(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserDetails("invalid");
        });
    }
}