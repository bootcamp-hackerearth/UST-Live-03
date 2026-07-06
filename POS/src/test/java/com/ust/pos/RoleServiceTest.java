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
import org.springframework.data.jpa.domain.Specification;

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
        role.setIdentifier("ROLE_ADMIN");
        role.setStatus(true);
        role.setDeleted(false);

        roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");
    }

    @Test
    void testFindByIdentifier() {

        when(roleRepository.findByIdentifier("ROLE_ADMIN"))
                .thenReturn(role);

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(roleDto);

        RoleDto result =
                roleService.findByIdentifier("ROLE_ADMIN");

        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.getIdentifier());
    }

    @Test
    void testChangeToggleStatus() {

        when(roleRepository.findByIdentifier("ROLE_ADMIN"))
                .thenReturn(role);

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(roleDto);

        RoleDto result =
                roleService.changeToggleStatus("ROLE_ADMIN", false);

        assertNotNull(result);
        assertFalse(role.isStatus());

        verify(roleRepository).save(role);
    }

    @Test
    void testChangeToggleStatus_RoleNotFound() {

        when(roleRepository.findByIdentifier("ROLE_ADMIN"))
                .thenReturn(null);

        when(modelMapper.map(null, RoleDto.class))
                .thenReturn(null);

        RoleDto result =
                roleService.changeToggleStatus("ROLE_ADMIN", false);

        assertNull(result);
    }

    @Test
    void testFindActiveStatus() {

        Role inactiveRole = new Role();
        inactiveRole.setStatus(false);

        when(roleRepository.findAll())
                .thenReturn(List.of(role, inactiveRole));

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(roleDto));

        List<RoleDto> result =
                roleService.findActiveStatus();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSave_NewRole() {

        when(roleRepository.findByIdentifier("ROLE_ADMIN"))
                .thenReturn(null);

        when(modelMapper.map(roleDto, Role.class))
                .thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertNotNull(result);

        verify(roleRepository).save(role);
    }

    @Test
    void testSave_AlreadyExists() {

        when(roleRepository.findByIdentifier("ROLE_ADMIN"))
                .thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_SoftDeleted() {

        role.setDeleted(true);

        when(roleRepository.findByIdentifier("ROLE_ADMIN"))
                .thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("soft deleted"));
    }

    @Test
    void testUpdate_Success() {

        when(roleRepository.findByIdentifier("ROLE_ADMIN"))
                .thenReturn(role);

        doNothing().when(modelMapper)
                .map(roleDto, role);

        RoleDto result = roleService.update(roleDto);

        assertNotNull(result);

        verify(roleRepository).save(role);
    }

    @Test
    void testUpdate_NotFound() {

        when(roleRepository.findByIdentifier("ROLE_ADMIN"))
                .thenReturn(null);

        RoleDto result = roleService.update(roleDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testDelete() {

        when(roleRepository.findByIdentifier("ROLE_ADMIN"))
                .thenReturn(role);

        roleService.delete("ROLE_ADMIN");

        assertTrue(role.isDeleted());
        assertFalse(role.isStatus());

        verify(roleRepository).save(role);
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Role> page =
                new PageImpl<>(Collections.singletonList(role));

        when(roleRepository.findByDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(roleDto));

        WsDto<RoleDto> result = roleService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Role> specification =
                mock(Specification.class);

        Page<Role> page =
                new PageImpl<>(Collections.singletonList(role));

        when(roleRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(Collections.singletonList(roleDto));

        WsDto<RoleDto> result =
                roleService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());

        verify(roleRepository)
                .findAll(specification, pageable);
    }
}