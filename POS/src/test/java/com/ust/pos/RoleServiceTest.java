package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    public static final String INVALID = "INVALID";
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
        RoleDto dto = new RoleDto();

        when(repository.findByIdentifierAndDeletedFalse(ADMIN))
                .thenReturn(role);

        when(mapper.map(role, RoleDto.class))
                .thenReturn(dto);

        assertNotNull(service.findByIdentifier(ADMIN));

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.findByIdentifier(INVALID)
                );

        assertEquals(
                "role with identifier 'INVALID' not found",
                exception.getMessage()
        );
    }

    @Test
    void saveTest() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier(ADMIN);

        Role role = new Role();
        role.setStatus(null);

        when(repository.findByIdentifier(ADMIN))
                .thenReturn(null);

        when(mapper.map(dto, Role.class))
                .thenReturn(role);

        RoleDto result = service.save(dto);

        verify(repository).save(role);

        assertEquals(ADMIN, result.getIdentifier());
        assertTrue(role.getStatus());

        Role existingRole = new Role();
        existingRole.setDeleted(false);

        when(repository.findByIdentifier(ADMIN))
                .thenReturn(existingRole);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Role with identifier - ADMIN already exists",
                result.getMessage()
        );

        existingRole.setDeleted(true);

        when(repository.findByIdentifier(ADMIN))
                .thenReturn(existingRole);

        result = service.save(dto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Role with Identifier ADMIN already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void updateTest() {

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

        RoleDto invalidDto = new RoleDto();
        invalidDto.setIdentifier(INVALID);

        when(repository.findByIdentifierAndDeletedFalse(INVALID))
                .thenReturn(null);

        result = service.update(invalidDto);

        assertFalse(result.isSuccess());

        assertEquals(
                "Role with identifier - INVALID not found",
                result.getMessage()
        );
    }

    @Test
    void deleteTest() {

        Role role = new Role();

        when(repository.findByIdentifierAndDeletedFalse(ADMIN))
                .thenReturn(role);

        service.delete(ADMIN);

        verify(repository).save(role);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Role> page =
                new PageImpl<>(
                        List.of(new Role()),
                        pageable,
                        1
                );

        when(repository.findAllByDeletedFalse(pageable))
                .thenReturn(page);

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(mapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new RoleDto()));

        WsDto<RoleDto> result =
                service.findAll(pageable);

        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        Specification<Role> specification =
                (root, query, cb) -> cb.conjunction();

        WsDto<RoleDto> specResult =
                service.findAll(specification, pageable);

        assertEquals(1, specResult.getDtoList().size());
        assertEquals(1, specResult.getTotalRecords());
        assertEquals(1, specResult.getTotalPage());
        assertEquals(10, specResult.getSizePerPage());
        assertEquals(0, specResult.getPage());

        verify(repository).findAllByDeletedFalse(pageable);
        verify(repository).findAll(any(Specification.class), eq(pageable));
    }
}