package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setIdentifier("ROLE_ADMIN");
        role.setStatus(true);
        role.setDeleted(false);

        roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");
    }

    @Test
    void testFindByIdentifier_Success() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);

        RoleDto result = roleService.findByIdentifier("ROLE_ADMIN");

        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> roleService.findByIdentifier("ROLE_ADMIN"));
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> roleService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull() {
        roleDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> roleService.save(roleDto));
    }

    @Test
    void testSave_WhenRoleAlreadyExistsAndNotDeleted() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenRoleAlreadyExistsButDeleted() {
        role.setDeleted(true);
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Role created successfully", result.getMessage());
    }

    @Test
    void testUpdate_WhenRoleNotFound() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);

        RoleDto result = roleService.update(roleDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenRoleDeleted() {
        role.setDeleted(true);
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);

        RoleDto result = roleService.update(roleDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDto result = roleService.update(roleDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Role updated successfully", result.getMessage());
    }

    @Test
    void testDelete_WhenRoleNotFound() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);

        roleService.delete("ROLE_ADMIN");

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testDelete_Success() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        roleService.delete("ROLE_ADMIN");

        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> page = new PageImpl<>(Collections.singletonList(role), pageable, 1);
        when(roleRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<RoleDto> result = roleService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertFalse(result.getDtoList().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> page = new PageImpl<>(Collections.singletonList(role), pageable, 1);
        Specification<Role> spec = mock(Specification.class);
        when(roleRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        WsDto<RoleDto> result = roleService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testToggleStatus_WhenRoleNotFound() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);

        RoleDto result = roleService.toggleStatus("ROLE_ADMIN");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDto result = roleService.toggleStatus("ROLE_ADMIN");

        assertNotNull(result);
        assertFalse(result.isStatus());
    }

    @Test
    void testFindIfTrue() {
        when(roleRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(Collections.singletonList(role));

        List<RoleDto> result = roleService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}