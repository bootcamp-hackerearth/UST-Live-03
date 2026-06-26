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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void constructorTest() {
        RoleServiceImpl service = new RoleServiceImpl(roleRepository, modelMapper);
        Assertions.assertNotNull(service);
    }

    @Test
    void saveSuccessTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("NewRole");
        Role role = new Role();
        role.setIdentifier("NewRole");

        Mockito.when(roleRepository.findByIdentifier("NewRole")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Role.class)).thenReturn(role);

        RoleDto result = roleService.save(dto);

        Assertions.assertEquals("NewRole", result.getIdentifier());

        Mockito.verify(modelMapper).map(dto, Role.class);
        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        Role existingRole = new Role();
        existingRole.setIdentifier("Admin");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existingRole);

        RoleDto result = roleService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Role with identifier - Admin already exists", result.getMessage());

        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureDeletedIdentifierTest() {
        Role existingRole = new Role();
        existingRole.setIdentifier("Admin");
        existingRole.setDeleted(true);
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existingRole);

        RoleDto result = roleService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Role identifier - Admin not available", result.getMessage());

        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        Role existingRole = new Role();
        existingRole.setIdentifier("User");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("User");

        Mockito.when(roleRepository.findByIdentifier("User")).thenReturn(existingRole);

        RoleDto result = roleService.update(dto);

        Assertions.assertEquals("User", result.getIdentifier());

        Mockito.verify(modelMapper).map(dto, existingRole);
        Mockito.verify(roleRepository).save(existingRole);
    }

    @Test
    void updateFailureNotFoundTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Unknown");

        Mockito.when(roleRepository.findByIdentifier("Unknown")).thenReturn(null);

        RoleDto result = roleService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Role with identifier - Unknown not found", result.getMessage());

        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);

        roleService.delete("Admin");

        Assertions.assertTrue(role.isDeleted());

        Mockito.verify(roleRepository).findByIdentifier("Admin");
    }

    @Test
    void deleteTrimmedIdentifierTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(role);

        roleService.delete(" ADMIN ");

        Assertions.assertTrue(role.isDeleted());

        Mockito.verify(roleRepository).findByIdentifier("ADMIN");
    }

    @Test
    void findAllTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> page = new PageImpl<>(roles, pageable, roles.size());

        Mockito.when(roleRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(roles), Mockito.any(Type.class))).thenReturn(roleDtos);

        WsDto<RoleDto> result = roleService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());

        Mockito.verify(roleRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findByIdentifierSuccessTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(dto);

        RoleDto result = roleService.findByIdentifier("ADMIN");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ADMIN", result.getIdentifier());

        Mockito.verify(modelMapper).map(role, RoleDto.class);
    }
}