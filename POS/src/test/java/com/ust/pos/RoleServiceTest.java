package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setIdentifier("ROLE1");
        role.setStatus(true);
        role.setDeleted(false);

        roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE1");
    }

    // ✅ FIND BY IDENTIFIER
    @Test
    void testFindByIdentifier() {
        when(roleRepository.findByIdentifier("ROLE1")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto result = roleService.findByIdentifier("ROLE1");

        assertNotNull(result);
    }

    // ✅ CHANGE TOGGLE STATUS
    @Test
    void testChangeToggleStatus() {
        when(roleRepository.findByIdentifier("ROLE1")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto result = roleService.changeToggleStatus("ROLE1", false);

        assertNotNull(result);
        assertFalse(role.isStatus());
        verify(roleRepository).save(role);
    }

    // ✅ FIND ACTIVE STATUS
    @Test
    void testFindActiveStatus() {
        role.setStatus(true);

        Role inactive = new Role();
        inactive.setStatus(false);

        List<Role> roles = List.of(role, inactive);

        when(roleRepository.findAll()).thenReturn(roles);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(roleDto));

        List<RoleDto> result = roleService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ✅ SAVE - NEW ROLE
    @Test
    void testSave_NewRole() {
        when(roleRepository.findByIdentifier("ROLE1")).thenReturn(null);
        when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        doNothing().when(roleService).setAuditFields(role, true);

        RoleDto result = roleService.save(roleDto);

        assertNotNull(result);
        verify(roleRepository).save(role);
    }

    // ✅ SAVE - ALREADY EXISTS
    @Test
    void testSave_AlreadyExists() {
        when(roleRepository.findByIdentifier("ROLE1")).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    // ✅ SAVE - SOFT DELETED
    @Test
    void testSave_SoftDeleted() {
        role.setDeleted(true);

        when(roleRepository.findByIdentifier("ROLE1")).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    // ✅ UPDATE - SUCCESS
    @Test
    void testUpdate_Success() {
        when(roleRepository.findByIdentifier("ROLE1")).thenReturn(role);

        doNothing().when(modelMapper).map(roleDto, role);
        doNothing().when(roleService).setAuditFields(role, false);

        RoleDto result = roleService.update(roleDto);

        assertNotNull(result);
        verify(roleRepository).save(role);
    }

    // ✅ UPDATE - NOT FOUND
    @Test
    void testUpdate_NotFound() {
        when(roleRepository.findByIdentifier("ROLE1")).thenReturn(null);

        RoleDto result = roleService.update(roleDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    // ✅ DELETE
    @Test
    void testDelete() {
        roleService.delete("ROLE1");

        verify(roleRepository).deleteByIdentifier("ROLE1");
    }

    // ✅ FIND ALL (Pagination)
    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> page = new PageImpl<>(Collections.singletonList(role));

        when(roleRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(roleDto));

        WsDto<RoleDto> result = roleService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }
}