package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
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

    private Role roleEntity;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        roleEntity = new Role();
        roleEntity.setId(1L);
        roleEntity.setIdentifier("ROLE_USER");
        roleEntity.setStatus(true);
        roleEntity.setDeleted(false);

        roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_USER");
    }

    @Test
    void testFindByIdentifier() {
        when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(roleEntity);

        RoleDto result = roleService.findByIdentifier("ROLE_USER");

        assertNotNull(result);
        assertEquals("ROLE_USER", result.getIdentifier());
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
    void testSave_WhenRoleExistsAndNotDeleted() {
        when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(roleEntity);

        RoleDto result = roleService.save(roleDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenRoleWasPreviouslyDeleted() {
        roleEntity.setDeleted(true);
        when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(roleEntity);

        RoleDto result = roleService.save(roleDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(null);
        when(roleRepository.save(any(Role.class))).thenReturn(roleEntity);

        RoleDto result = roleService.save(roleDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Role created successfully", result.getMessage());
    }

    @Test
    void testUpdate_WhenRoleNotFound() {
        when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(null);

        RoleDto result = roleService.update(roleDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenRolegetDeleted() {
        roleEntity.setDeleted(true);
        when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(roleEntity);

        RoleDto result = roleService.update(roleDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(roleEntity);
        when(roleRepository.save(any(Role.class))).thenReturn(roleEntity);

        RoleDto result = roleService.update(roleDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Role updated successfully", result.getMessage());
    }

    @Test
    void testDelete_WhenRoleNotFound() {
        when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(null);

        roleService.delete("ROLE_USER");

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testDelete_Success() {
        when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(roleEntity);
        when(roleRepository.save(any(Role.class))).thenReturn(roleEntity);

        roleService.delete("ROLE_USER");

        verify(roleRepository, times(1)).save(roleEntity);
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Role> entityList = Collections.singletonList(roleEntity);
        Page<Role> page = new PageImpl<>(entityList, pageable, 1);

        when(roleRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<RoleDto> result = roleService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertEquals(0, result.getPage());
    }

    @Test
    void testToggleStatus_WhenRoleNotFound() {
        when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(null);

        RoleDto result = roleService.toggleStatus("ROLE_USER");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testToggleStatus_Success() {
        roleEntity.setStatus(true);
        when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(roleEntity);
        when(roleRepository.save(any(Role.class))).thenReturn(roleEntity);

        RoleDto result = roleService.toggleStatus("ROLE_USER");

        assertNotNull(result);
        assertFalse(result.getStatus());
    }

    @Test
    void testFindIfTrue() {
        List<Role> activeRoles = Collections.singletonList(roleEntity);
        when(roleRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(activeRoles);

        List<RoleDto> result = roleService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}