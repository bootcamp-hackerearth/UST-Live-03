package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
    void findByUserNameTestSuccess() {
        User user = new User();
        UserDto userDto = new UserDto();
        userDto.setUsername("john_doe");

        Mockito.when(userRepository.findByUsername("john_doe")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.findByUserName("john_doe");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("john_doe", response.getUsername());
    }

    @Test
    void findByUserNameTestNotFoundException() {
        Mockito.when(userRepository.findByUsername("john_doe")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            userService.findByUserName("john_doe");
        });
    }

    @Test
    void saveTestSuccess() {
        UserDto userDto = new UserDto();
        userDto.setUsername("john_doe");
        userDto.setPassword("plainPassword");

        Mockito.when(userRepository.findByUsername("john_doe")).thenReturn(null);

        User user = new User();
        Mockito.when(modelMapper.map(userDto, User.class)).thenReturn(user);
        Mockito.when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        Mockito.when(userRepository.save(user)).thenReturn(user);

        UserDto response = userService.save(userDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("john_doe", response.getUsername());
    }

    @Test
    void saveTestFailureAlreadyExists() {
        UserDto userDto = new UserDto();
        userDto.setUsername("john_doe");

        User existingUser = new User();
        existingUser.setUsername("john_doe");
        existingUser.setDeleted(false);

        Mockito.when(userRepository.findByUsername("john_doe")).thenReturn(existingUser);

        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "john_doe already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        UserDto userDto = new UserDto();
        userDto.setUsername("john_doe");

        User existingUser = new User();
        existingUser.setUsername("john_doe");
        existingUser.setDeleted(true);

        Mockito.when(userRepository.findByUsername("john_doe")).thenReturn(existingUser);

        UserDto response = userService.save(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "john_doe was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void updateTestSuccessSameUsername() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("john_doe");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("john_doe");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto response = userService.update(userDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("john_doe", response.getUsername());
    }

    @Test
    void updateTestSuccessNewUniqueUsername() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("new_username");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("old_username");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByUsername("new_username")).thenReturn(null);
        Mockito.when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserDto response = userService.update(userDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("new_username", response.getUsername());
    }

    @Test
    void updateTestFailureUserNotFound() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("john_doe");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "john_doe not found", response.getMessage());
    }

    @Test
    void updateTestFailureUsernameAlreadyExists() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("clashing_username");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("old_username");

        User clashingUser = new User();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        Mockito.when(userRepository.findByUsername("clashing_username")).thenReturn(clashingUser);

        UserDto response = userService.update(userDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(UserServiceImpl.USER_WITH_USERNAME_EMAIL + "clashing_username already exists", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(authentication.getName()).thenReturn("logged_in_user");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User userEntity = new User();
        UserDto userDto = new UserDto();

        Mockito.when(userRepository.findByUsername("target_user")).thenReturn(userEntity);
        Mockito.when(modelMapper.map(userEntity, UserDto.class)).thenReturn(userDto);
        Mockito.when(userRepository.save(userEntity)).thenReturn(userEntity);

        UserDto response = userService.delete("target_user");

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("User deleted successfully", response.getMessage());
    }

    @Test
    void deleteTestFailureUserNotFound() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(userRepository.findByUsername("john_doe")).thenReturn(null);

        UserDto response = userService.delete("john_doe");

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("User not found", response.getMessage());
    }

    @Test
    void deleteTestFailureCannotDeleteSelf() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(authentication.getName()).thenReturn("john_doe");
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User userEntity = new User();
        UserDto userDto = new UserDto();

        Mockito.when(userRepository.findByUsername("john_doe")).thenReturn(userEntity);
        Mockito.when(modelMapper.map(userEntity, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.delete("john_doe");

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Cannot delete the logged in User", response.getMessage());
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        User user = new User();
        List<User> userList = List.of(user);
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());

        UserDto userDto = new UserDto();
        List<UserDto> userDtos = List.of(userDto);

        Mockito.when(userRepository.findByDeletedFalse(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(Mockito.eq(userList), Mockito.any(Type.class))).thenReturn(userDtos);

        WsDto<UserDto> response = userService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void toggleStatusTest() {
        User user = new User();
        user.setStatus(false);
        UserDto userDto = new UserDto();
        userDto.setStatus(true);

        Mockito.when(userRepository.findByUsername("john_doe")).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto response = userService.toggleStatus("john_doe");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void findIfTrueTest() {
        User user = new User();
        List<User> userList = List.of(user);
        UserDto userDto = new UserDto();
        List<UserDto> userDtos = List.of(userDto);

        Mockito.when(userRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(userList);
        Mockito.when(modelMapper.map(Mockito.eq(userList), Mockito.any(Type.class))).thenReturn(userDtos);

        List<UserDto> response = userService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void getUserDetailsTestSuccess() {
        User user = new User();
        user.setId(1L);
        user.setIdentifier("USR01");
        user.setName("John Doe");
        user.setUsername("john_doe");
        user.setPhoneNo("1234567890");

        Mockito.when(userRepository.findByUsername("john_doe")).thenReturn(user);

        UserDto response = userService.getUserDetails("john_doe");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1L, response.getId());
        Assertions.assertEquals("USR01", response.getIdentifier());
        Assertions.assertEquals("John Doe", response.getName());
        Assertions.assertEquals("john_doe", response.getUsername());
        Assertions.assertEquals("1234567890", response.getPhoneNo());
    }

    @Test
    void getUserDetailsTestFailureNotFound() {
        Mockito.when(userRepository.findByUsername("john_doe")).thenReturn(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserDetails("john_doe");
        });
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<User> specification = Mockito.mock(Specification.class);
        User user = new User();
        List<User> userList = List.of(user);
        Page<User> page = new PageImpl<>(userList, pageable, userList.size());

        UserDto userDto = new UserDto();
        List<UserDto> userDtos = List.of(userDto);

        Mockito.when(userRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(userList), Mockito.any(Type.class))).thenReturn(userDtos);

        WsDto<UserDto> response = userService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}