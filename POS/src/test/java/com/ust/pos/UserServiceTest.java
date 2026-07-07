package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.User;
import com.ust.pos.models.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByUsernameTest() {
        User user = new User();
        UserDto dto = new UserDto();
        when(userRepository.findByUsernameAndDeletedFalse("test")).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);
        UserDto result = userService.findByUserName("test");
        assertNotNull(result);
        when(userRepository.findByUsernameAndDeletedFalse("missing")).thenReturn(null);
        result = userService.findByUserName("missing");
        assertFalse(result.isSuccess());
        assertEquals("User not found", result.getMessage());
    }

    @Test
    void saveTest() {
        UserDto dto = new UserDto();
        dto.setUsername("existing");
        when(userRepository.findByUsername("existing")).thenReturn(new User());
        UserDto result = userService.save(dto);
        assertFalse(result.isSuccess());
        verify(userRepository, never()).save(any());
        User deleted = new User();
        deleted.setDeleted(true);
        when(userRepository.findByUsername("deleted")).thenReturn(deleted);
        UserDto deletedDto = new UserDto();
        deletedDto.setUsername("deleted");
        result = userService.save(deletedDto);
        assertFalse(result.isSuccess());
        UserDto successDto = new UserDto();
        successDto.setUsername("newuser");
        successDto.setPassword("plain");
        User user = new User();
        when(userRepository.findByUsername("newuser")).thenReturn(null);
        when(modelMapper.map(successDto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        result = userService.save(successDto);
        assertTrue(result.isSuccess());
        assertEquals("User created successfully", result.getMessage());
        verify(userRepository).save(user);
        assertEquals("encoded", user.getPassword());
        assertEquals("newuser", user.getIdentifier());
    }

    @Test
    void updateTest() {
        when(userRepository.findByUsernameAndDeletedFalse("old")).thenReturn(null);
        UserDto result = userService.update("old", new UserDto());
        assertFalse(result.isSuccess());
        User existing = new User();
        when(userRepository.findByUsernameAndDeletedFalse("user1")).thenReturn(existing);
        when(userRepository.findByUsername("new")).thenReturn(new User());
        UserDto duplicateDto = new UserDto();
        duplicateDto.setUsername("new");
        result = userService.update("user1", duplicateDto);
        assertFalse(result.isSuccess());
        verify(userRepository, never()).save(argThat(user -> "new".equals(user.getUsername())));
        User sameUser = new User();
        sameUser.setUsername("same");
        when(userRepository.findByUsernameAndDeletedFalse("same")).thenReturn(sameUser);
        UserDto sameDto = new UserDto();
        sameDto.setUsername("same");
        sameDto.setName("Updated");
        sameDto.setPhoneNo("123");
        result = userService.update("same", sameDto);
        assertTrue(result.isSuccess());
        verify(userRepository).save(sameUser);
        User renameUser = new User();
        when(userRepository.findByUsernameAndDeletedFalse("oldName")).thenReturn(renameUser);
        when(userRepository.findByUsername("newName")).thenReturn(null);
        UserDto renameDto = new UserDto();
        renameDto.setUsername("newName");
        result = userService.update("oldName", renameDto);
        assertTrue(result.isSuccess());
        assertEquals("newName", renameUser.getUsername());
    }

    @Test
    void deleteTest() {
        User user = new User();
        user.setDeleted(false);
        when(userRepository.findByUsernameAndDeletedFalse("test")).thenReturn(user);
        userService.delete("test");
        assertTrue(user.getDeleted());
        verify(userRepository).save(user);
        when(userRepository.findByUsernameAndDeletedFalse("missing")).thenReturn(null);
        userService.delete("missing");
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);
        List<User> users = List.of(new User());
        Page<User> page = new PageImpl<>(users, pageable, 1);
        when(userRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(users), any(Type.class))).thenReturn(List.of(new UserDto()));
        WsDto<UserDto> result = userService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<User> specification = mock(Specification.class);
        List<User> users = List.of(new User());
        Page<User> page = new PageImpl<>(users, pageable, 1);
        when(userRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(users), any(Type.class))).thenReturn(List.of(new UserDto()));
        WsDto<UserDto> result = userService.findAll(specification, pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        verify(userRepository).findAll(specification, pageable);
    }
}