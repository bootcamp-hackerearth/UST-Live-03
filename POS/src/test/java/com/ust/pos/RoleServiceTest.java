package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");
        roleDto.setDescription("Administrator Role");
        roleDto.setSuccess(true);

        role = new Role();
        role.setIdentifier("ROLE_ADMIN");
        role.setDescription("Administrator Role");
    }

    @Test
    void testFindByIdentifier() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto result = roleService.findByIdentifier("ROLE_ADMIN");

        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.getIdentifier());
    }

    @Test
    void testSave_RoleAlreadyExists() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void testSave_NewRole() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);
        when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        verify(roleRepository).save(role);
        assertNotNull(result);
    }

    @Test
    void testUpdate_RoleNotFound() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);

        RoleDto result = roleService.update(roleDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void testUpdate_RoleFound() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);

        RoleDto result = roleService.update(roleDto);

        verify(modelMapper).map(roleDto, role);
        verify(roleRepository).save(role);
        assertNotNull(result);
    }

    @Test
    void testDelete() {
        doNothing().when(roleRepository).deleteByIdentifier("ROLE_ADMIN");

        roleService.delete("ROLE_ADMIN");

        verify(roleRepository).deleteByIdentifier("ROLE_ADMIN");
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Role> rolePage = new PageImpl<>(
                Collections.singletonList(role)
        );

        when(roleRepository.findAll(pageable)).thenReturn(rolePage);
        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(roleDto));

        List<RoleDto> result = roleService.findAll(pageable);

        assertEquals(1, result.size());
        verify(roleRepository).findAll(pageable);
        verify(modelMapper).map(anyList(), any(Type.class));
    }
}