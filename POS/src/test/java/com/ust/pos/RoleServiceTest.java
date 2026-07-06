package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl roleService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void findAll_ShouldReturnWsDtoWithRoleDtos() {
        Specification<Role> specification = (root, query, criteriaBuilder) -> null;
        Pageable pageable = PageRequest.of(0, 10);

        Role role = new Role();
        role.setId(1L);
        role.setIdentifier("ROLE001");

        List<Role> roleList = List.of(role);
        Page<Role> rolePage =
                new PageImpl<>(roleList, pageable, roleList.size());

        RoleDto roleDto = new RoleDto();
        roleDto.setId(1L);
        roleDto.setIdentifier("ROLE001");

        List<RoleDto> roleDtoList = List.of(roleDto);

        when(roleRepository.findAll(specification, pageable))
                .thenReturn(rolePage);

        when(modelMapper.map(eq(roleList), any(Type.class)))
                .thenReturn(roleDtoList);

        WsDto<RoleDto> result = roleService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(roleDtoList, result.getDtoList());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(roleRepository, times(1)).findAll(specification, pageable);
        verify(modelMapper, times(1)).map(eq(roleList), any(Type.class));
    }

    @Test
    void testFindByIdentifier_Success() {
        String identifier = "ADMIN";

        Role role = new Role();
        role.setIdentifier(identifier);

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier(identifier);

        when(roleRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(role);
        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(roleDto);

        RoleDto result = roleService.findByIdentifier(identifier);

        assertEquals(identifier, result.getIdentifier());
    }

    @Test
    void testSave_Success() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");

        Role role = new Role();
        role.setIdentifier("ADMIN");

        when(roleRepository.findByIdentifier("ADMIN")).thenReturn(null);
        when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertTrue(result.isSuccess());
        assertEquals("Role created successfully", result.getMessage());
        verify(roleRepository).save(role);
    }

    @Test
    void testSave_AlreadyExists() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");

        Role existingRole = new Role();
        existingRole.setDeleted(false);

        when(roleRepository.findByIdentifier("ADMIN")).thenReturn(existingRole);

        RoleDto result = roleService.save(roleDto);

        assertFalse(result.isSuccess());
        assertEquals("Role with identifier - ADMIN already exists", result.getMessage());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testSave_DeletedRoleExists() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");

        Role existingRole = new Role();
        existingRole.setDeleted(true);

        when(roleRepository.findByIdentifier("ADMIN")).thenReturn(existingRole);

        RoleDto result = roleService.save(roleDto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Role with identifier - ADMIN was deleted and cannot be created again.",
                result.getMessage()
        );
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testUpdate_Success() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");

        Role existingRole = new Role();
        existingRole.setIdentifier("ADMIN");

        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN"))
                .thenReturn(existingRole);

        RoleDto result = roleService.update(roleDto);

        assertTrue(result.isSuccess());
        assertEquals("Role updated successfully", result.getMessage());

        verify(modelMapper).map(roleDto, existingRole);
        verify(roleRepository).save(existingRole);
    }

    @Test
    void testUpdate_NotFound() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");

        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN"))
                .thenReturn(null);

        RoleDto result = roleService.update(roleDto);

        assertFalse(result.isSuccess());
        assertEquals("Role with identifier - ADMIN not found", result.getMessage());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testDelete_Success() {
        String identifier = "ADMIN";

        Role role = new Role();
        role.setIdentifier(identifier);
        role.setDeleted(false);

        when(roleRepository.findByIdentifierAndDeletedFalse(identifier))
                .thenReturn(role);

        roleService.delete(identifier);

        assertTrue(role.getDeleted());
        verify(roleRepository).save(role);
    }

    @Test
    void testFindAll_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Role role = new Role();
        role.setIdentifier("ADMIN");

        Page<Role> rolePage = new PageImpl<>(List.of(role), pageable, 1);

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");

        when(roleRepository.findAllByDeletedFalse(pageable))
                .thenReturn(rolePage);

        when(modelMapper.map(anyList(), any(Type.class)))
                .thenReturn(List.of(roleDto));

        WsDto<RoleDto> result = roleService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

}