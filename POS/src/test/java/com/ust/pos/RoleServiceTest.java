package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Role;
import com.ust.pos.modell.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
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

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    public static final String ADMIN = "ADMIN";
    @InjectMocks
    private RoleServiceImpl service;

    @Mock
    private RoleRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierTest() {

        Role role = new Role();
        role.setIdentifier(ADMIN);

        RoleDto dto = new RoleDto();
        dto.setIdentifier(ADMIN);

        when(repository.findByIdentifierAndDeletedFalse(ADMIN))
                .thenReturn(role);

        when(mapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto result =
                service.findByIdentifier(ADMIN);

        assertNotNull(result);
        assertEquals(ADMIN, result.getIdentifier());
    }

    @Test
    void saveSuccessTest() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier(ADMIN);

        Role role = new Role();

        when(repository.findByIdentifier(ADMIN))
                .thenReturn(null);

        when(mapper.map(dto, Role.class))
                .thenReturn(role);

        RoleDto result = service.save(dto);

        verify(repository).save(role);

        assertEquals(ADMIN, result.getIdentifier());
    }

    @Test
    void saveWhenRoleExistsAndSoftDeleted() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier(ADMIN);

        Role existingRole = new Role();
        existingRole.setDeleted(true);

        when(repository.findByIdentifier(ADMIN)).thenReturn(existingRole);

        RoleDto result = service.save(roleDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Role with Identifier ADMIN already exists (Soft-Deleted)",
                result.getMessage()
        );

        verify(repository, never()).save(any(Role.class));
        verify(mapper, never()).map(any(), eq(Role.class));
    }

    @Test
    void saveDuplicateTest() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier(ADMIN);

        Role role = new Role();
        role.setDeleted(false);

        when(repository.findByIdentifier(ADMIN))
                .thenReturn(role);

        RoleDto result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Role with identifier - ADMIN already exists",
                result.getMessage()
        );
    }

    @Test
    void updateSuccessTest() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier(ADMIN);

        Role role = new Role();
        role.setIdentifier(ADMIN);
        role.setCreatedBy("admin");
        role.setCreatedOn(LocalDateTime.now());

        when(repository.findByIdentifierAndDeletedFalse(ADMIN))
                .thenReturn(role);

        RoleDto result = service.update(dto);

        verify(mapper).map(dto, role);
        verify(repository).save(role);

        assertNotNull(result);
    }

    @Test
    void updateNotFoundTest() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier(ADMIN);

        when(repository.findByIdentifierAndDeletedFalse(ADMIN))
                .thenReturn(null);

        RoleDto result = service.update(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Role with identifier - ADMIN not found",
                result.getMessage()
        );
    }

    @Test
    void deleteSuccessTest() {

        Role role = new Role();

        when(repository.findByIdentifierAndDeletedFalse(ADMIN))
                .thenReturn(role);

        service.delete(ADMIN);

        verify(repository).save(role);
    }

    @Test
    void findAllTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Role role = new Role();
        RoleDto dto = new RoleDto();

        Page<Role> page =
                new PageImpl<>(
                        List.of(role),
                        pageable,
                        1
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<RoleDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Page<Role> page =
                new PageImpl<>(
                        Collections.emptyList(),
                        pageable,
                        0
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(Collections.emptyList());

        WsDto<RoleDto> result =
                service.findAll(pageable);

        assertTrue(result.getDtoList().isEmpty());
    }
}