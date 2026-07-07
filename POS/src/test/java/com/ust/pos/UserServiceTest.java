package com.ust.pos;

import com.ust.pos.dto.UserDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.User;
import com.ust.pos.modell.UserRepository;
import com.ust.pos.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    public static final String ADMIN = "admin";
    public static final String OLDADMIN = "oldadmin";
    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByUserNameTest() {

        User user = new User();
        UserDto dto = new UserDto();

        when(repository.findByUsernameAndDeletedFalse(ADMIN))
                .thenReturn(user);

        when(mapper.map(user, UserDto.class))
                .thenReturn(dto);

        assertNotNull(service.findByUserName(ADMIN));

        when(repository.findByUsernameAndDeletedFalse("invalid"))
                .thenReturn(null);

        ResourceNotFoundException ex =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByUserName("invalid")
                );

        assertEquals(
                "user with identifier 'invalid' not found",
                ex.getMessage()
        );
    }

    @Test
    void saveTest() {

        UserDto dto = new UserDto();
        dto.setUsername(ADMIN);
        dto.setPassword("raw");

        User user = new User();
        user.setStatus(null);

        when(repository.findByUsername(ADMIN))
                .thenReturn(null);

        when(mapper.map(dto, User.class))
                .thenReturn(user);

        when(encoder.encode("raw"))
                .thenReturn("encoded");

        UserDto result = service.save(dto);

        verify(repository).save(user);

        assertEquals("encoded", user.getPassword());
        assertTrue(user.getStatus());
        assertTrue(result.isSuccess());

        User duplicate = new User();
        duplicate.setDeleted(false);

        when(repository.findByUsername(ADMIN))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "User with username/email - admin already exists",
                result.getMessage()
        );

        duplicate.setDeleted(true);

        when(repository.findByUsername(ADMIN))
                .thenReturn(duplicate);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "User with username/email - admin already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateTest() {

        User existing = new User();
        existing.setUsername(OLDADMIN);
        existing.setCreatedBy("system");
        existing.setCreatedOn(LocalDateTime.now());

        UserDto dto = new UserDto();
        dto.setUsername("newadmin");
        dto.setName("Test");
        dto.setPhoneNo("9999999999");
        dto.setRoles(List.of("ADMIN"));

        when(repository.findByUsernameAndDeletedFalse(OLDADMIN))
                .thenReturn(existing);

        when(repository.findByUsername("newadmin"))
                .thenReturn(null);

        UserDto result =
                service.update(OLDADMIN, dto);

        assertTrue(result.isSuccess());

        verify(repository).save(existing);

        UserDto duplicateDto = new UserDto();
        duplicateDto.setUsername("duplicate");

        User existingUser = new User();
        existingUser.setUsername(OLDADMIN);

        when(repository.findByUsernameAndDeletedFalse("oldadmin2"))
                .thenReturn(existingUser);

        when(repository.findByUsername("duplicate"))
                .thenReturn(new User());

        result =
                service.update("oldadmin2", duplicateDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "User with username/email - duplicate already exists",
                result.getMessage()
        );

        when(repository.findByUsernameAndDeletedFalse("missing"))
                .thenReturn(null);

        result =
                service.update("missing", new UserDto());

        assertFalse(result.isSuccess());

        assertEquals(
                "User not found",
                result.getMessage()
        );

        User sameUser = new User();
        sameUser.setUsername("same");

        UserDto sameDto = new UserDto();
        sameDto.setUsername("same");

        when(repository.findByUsernameAndDeletedFalse("same"))
                .thenReturn(sameUser);

        result = service.update("same", sameDto);

        assertTrue(result.isSuccess());
    }

    @Test
    void deleteTest() {

        User user = new User();

        when(repository.findByUsernameAndDeletedFalse(ADMIN))
                .thenReturn(user)
                .thenReturn(null);

        service.delete(ADMIN);

        verify(repository).save(user);

        service.delete(ADMIN);

        verify(repository, times(1)).save(user);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> page =
                new PageImpl<>(
                        List.of(new User()),
                        pageable,
                        1
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new UserDto()));

        WsDto<UserDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        Specification<User> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<UserDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());
        assertEquals(1, specResult.getTotalPage());

        verify(repository).findAllByDeletedFalse(pageable);
        verify(repository).findAll(any(Specification.class), eq(pageable));
    }
}