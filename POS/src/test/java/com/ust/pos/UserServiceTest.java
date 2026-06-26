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
    void saveTest() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        dto.setPassword("123");
        Mockito.when(userRepository.findByUsername("admin")).thenReturn(null);
        User user = new User();
        Mockito.when(modelMapper.map(dto, User.class)).thenReturn(user);
        Mockito.when(passwordEncoder.encode("123")).thenReturn("encoded123");
        Mockito.when(userRepository.save(user)).thenReturn(user);
        UserDto response = userService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestAlreadyExists() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        User existing = new User();
        existing.setDeleted(false);
        Mockito.when(userRepository.findByUsername("admin")).thenReturn(existing);
        UserDto response = userService.save(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestDeletedUser() {
        UserDto dto = new UserDto();
        dto.setUsername("admin");
        User existing = new User();
        existing.setDeleted(true);
        Mockito.when(userRepository.findByUsername("admin")).thenReturn(existing);
        UserDto response = userService.save(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByUserNameTest() {
        User user = new User();
        UserDto dto = new UserDto();
        Mockito.when(userRepository.findByUsernameAndDeletedFalse("admin")).thenReturn(user);
        Mockito.when(modelMapper.map(user, UserDto.class)).thenReturn(dto);
        UserDto response = userService.findByUserName("admin");
        Assertions.assertNotNull(response);
    }

    @Test
    void updateUserNotFoundTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserDto response = userService.update(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateDeletedUserTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");
        User existing = new User();
        existing.setDeleted(true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        UserDto response = userService.update(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateUsernameExistsTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newUser");
        User existing = new User();
        existing.setUsername("oldUser");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.findByUsername("newUser")).thenReturn(new User());
        UserDto response = userService.update(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateUsernameDifferentButNotExistsTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("newUser");
        User existing = new User();
        existing.setUsername("oldUser");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(userRepository.findByUsername("newUser")).thenReturn(null);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(userRepository.save(existing)).thenReturn(existing);
        UserDto response = userService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updatePasswordNullBranchTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");
        dto.setPassword(null);
        User existing = new User();
        existing.setUsername("admin");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(userRepository.save(existing)).thenReturn(existing);
        UserDto response = userService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(passwordEncoder, Mockito.never())
                .encode(Mockito.any());
    }

    @Test
    void updatePasswordPresentTest() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("admin");
        dto.setPassword("123");
        User existing = new User();
        existing.setUsername("admin");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(passwordEncoder.encode("123")).thenReturn("encoded123");
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(userRepository.save(existing)).thenReturn(existing);
        UserDto response = userService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void deleteTest() {
        User user = new User();
        Mockito.when(userRepository.findByUsernameAndDeletedFalse("admin")).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        boolean result = userService.delete("admin");
        Assertions.assertTrue(result);
    }

    @Test
    void deleteNullTest() {
        Mockito.when(userRepository.findByUsernameAndDeletedFalse("admin")).thenReturn(null);
        boolean result = userService.delete("admin");
        Assertions.assertTrue(result);
        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllTest() {
        User user = new User();
        UserDto dto = new UserDto();
        List<User> list = List.of(user);
        List<UserDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<User> page = new PageImpl<>(list);
        Mockito.when(userRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class)))
                .thenReturn(dtoList);
        WsDto<UserDto> response = userService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<User> page = new PageImpl<>(List.of());
        Mockito.when(userRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class)))
                .thenReturn(List.of());
        WsDto<UserDto> response = userService.findAll(pageable);
        Assertions.assertTrue(response.getDtoList().isEmpty());
    }
}