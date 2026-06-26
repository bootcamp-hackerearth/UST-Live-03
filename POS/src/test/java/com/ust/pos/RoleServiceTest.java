package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

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
    void findByIdentifierSuccessTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto result = roleService.findByIdentifier("ADMIN");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ADMIN", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);

        RoleDto result = roleService.findByIdentifier("ADMIN");

        Assertions.assertNull(result);
    }

    @Test
    void saveSuccessTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Role role = new Role();

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);

        when(modelMapper.map(dto, Role.class))
                .thenReturn(role);

        RoleDto result = roleService.save(dto);

        Assertions.assertEquals("ADMIN", result.getIdentifier());

        verify(roleRepository).save(role);
    }

    @Test
    void saveAlreadyExistsTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Role existingRole = new Role();
        existingRole.setDeleted(false);

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(existingRole);

        RoleDto result = roleService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Role with identifier - ADMIN already exists",
                result.getMessage()
        );

        verify(roleRepository, never()).save(any());
    }

    @Test
    void saveDeletedRoleTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Role existingRole = new Role();
        existingRole.setDeleted(true);

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(existingRole);

        RoleDto result = roleService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Role with identifier - ADMIN was deleted , Please Contact the Administrator to add.",
                result.getMessage()
        );

        verify(roleRepository, never()).save(any());
    }

    @Test
    void updateSuccessTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Role existingRole = new Role();
        existingRole.setIdentifier("ADMIN");

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(existingRole);

        RoleDto result = roleService.update(dto);

        Assertions.assertEquals("ADMIN", result.getIdentifier());

        verify(modelMapper).map(dto, existingRole);
        verify(roleRepository).save(existingRole);
    }

    @Test
    void updateFailureTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);

        RoleDto result = roleService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals(
                "Role with identifier - ADMIN not found",
                result.getMessage()
        );

        verify(roleRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Role role = new Role();

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        roleService.delete("ADMIN");

        verify(roleRepository).findByIdentifier("ADMIN");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Role role1 = new Role();
        Role role2 = new Role();

        List<Role> roles = List.of(
                role1,
                role2
        );

        Page<Role> page = new PageImpl<>(
                roles,
                pageable,
                2
        );

        List<RoleDto> dtoList = List.of(
                new RoleDto(),
                new RoleDto()
        );

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();

        when(roleRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(roles, listType))
                .thenReturn(dtoList);

        WsDto<RoleDto> result = roleService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getDtoList().size());
        Assertions.assertEquals(2, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
    }
}