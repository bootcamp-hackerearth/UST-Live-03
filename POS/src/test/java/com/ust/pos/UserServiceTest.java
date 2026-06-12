package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("john");
        user.setIdentifier("U001");
        user.setPassword("encodedPwd");
        user.setStatus(true);
        user.setRoles(new ArrayList<>(List.of("ADMIN")));

        userDto = new UserDto();
        userDto.setUsername("john");
        userDto.setIdentifier("U001");
        userDto.setPassword("plainPwd");
        userDto.setRoles(new ArrayList<>(List.of("ADMIN")));
    }




    @Test
    void findByUserName_found() {
        when(userRepository.findByUsername("john")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.findByUserName("john");

        assertNotNull(result);
        assertEquals("john", result.getUsername());
    }

    @Test
    void findByUserName_notFound() {
        when(userRepository.findByUsername("john")).thenReturn(null);

        UserDto result = userService.findByUserName("john");

        assertNull(result);
    }

    @Test
    void findByIdentifier_found() {
        when(userRepository.findByIdentifier("U001")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.findByIdentifier("U001");

        assertNotNull(result);
        assertEquals("U001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_notFound() {
        when(userRepository.findByIdentifier("U001")).thenReturn(null);

        UserDto result = userService.findByIdentifier("U001");

        assertNull(result);
    }

    @Test
    void save_success() {
        when(userRepository.findByUsername("john")).thenReturn(null);
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("plainPwd")).thenReturn("encodedPwd");

        UserDto result = userService.save(userDto);

        verify(passwordEncoder).encode("plainPwd");
        verify(userRepository).save(user);
        assertEquals("john", result.getUsername());
    }

    @Test
    void save_duplicate() {
        when(userRepository.findByUsername("john")).thenReturn(user);

        UserDto result = userService.save(userDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void update_success_withRoles() {
        when(userRepository.findByUsername("john")).thenReturn(user);

        UserDto result = userService.update(userDto);

        verify(modelMapper).map(userDto, user);
        verify(userRepository).save(user);
        assertTrue(result.isSuccess());
        assertEquals("User updated successfully", result.getMessage());
    }

    @Test
    void update_userNotFound() {
        when(userRepository.findByUsername("john")).thenReturn(null);

        UserDto result = userService.update(userDto);

        assertFalse(result.isSuccess());
        assertEquals("User not found", result.getMessage());
    }


    @Test
    void update_preserveRoles_whenRolesNull() {
        when(userRepository.findByUsername("john")).thenReturn(user);

        userDto.setRoles(null);

        UserDto result = userService.update(userDto);

        assertTrue(result.isSuccess());
        assertTrue(user.getRoles().contains("ADMIN"));
    }

    @Test
    void update_preserveRoles_whenRolesEmpty() {
        when(userRepository.findByUsername("john")).thenReturn(user);

        userDto.setRoles(new ArrayList<>());

        UserDto result = userService.update(userDto);

        assertTrue(result.isSuccess());
        assertTrue(user.getRoles().contains("ADMIN"));
    }

    @Test
    void delete_user() {
        doNothing().when(userRepository).deleteByUsername("john");

        userService.delete("john");

        verify(userRepository).deleteByUsername("john");
    }

    @Test
    void findAll_success() {
        User user1 = new User();
        user1.setIdentifier("U001");

        UserDto userDto1 = new UserDto();
        userDto1.setIdentifier("U001");

        List<User> users = List.of(user1);
        List<UserDto> userDtos = List.of(userDto1);

        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        Mockito.when(userRepository.findAll(pageable)).thenReturn(userPage);
        Mockito.when(modelMapper.map(Mockito.eq(users), Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(userDtos);

        WsDto<UserDto> response = userService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1L, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void toggleStatus_trueToFalse() {
        when(userRepository.findByIdentifier("U001")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        userService.toggleStatus("U001");

        assertFalse(user.isStatus());
    }

    @Test
    void toggleStatus_falseToTrue() {
        user.setStatus(false);

        when(userRepository.findByIdentifier("U001")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        userService.toggleStatus("U001");

        assertTrue(user.isStatus());
    }

    @Test
    void findIfTrue_activeUsers() {
        when(userRepository.findByStatusIsTrue()).thenReturn(List.of(user));
        when(modelMapper.map(any(), any(Type.class))).thenReturn(List.of(userDto));

        List<UserDto> result = userService.findIfTrue();

        assertEquals(1, result.size());
    }

    @Test
    void update_failure_duplicateUsername() {
        User existingUser = new User();
        existingUser.setUsername("john");
        existingUser.setIdentifier("U001");
        existingUser.setRoles(new ArrayList<>(List.of("ADMIN")));

        UserDto dto = new UserDto();
        dto.setIdentifier("U001");
        dto.setUsername("jane");
        when(userRepository.findByUsername("jane")).thenReturn(existingUser);

        when(userRepository.findByUsername("jane"))
                .thenReturn(existingUser)
                .thenReturn(new User());

        UserDto result = userService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals("User with email jane already exists", result.getMessage());
    }
}