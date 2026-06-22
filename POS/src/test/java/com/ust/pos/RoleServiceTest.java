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

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();
        role.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Role added successfully", response.getMessage());

        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void saveTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role existingRole = new Role();

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existingRole);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role 'Admin' already exists", response.getMessage());

        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveSoftDeletedTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role existingRole = new Role();
        existingRole.setDeleted(true);

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existingRole);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role 'Admin' has been soft deleted.(Rollback by changing status", response.getMessage());

        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto response = roleService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findByIdentifierNullTest() {
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        RoleDto response = roleService.findByIdentifier("Admin");

        Assertions.assertNull(response);
    }

    @Test
    void updateTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role existingRole = new Role();
        existingRole.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existingRole);

        Mockito.doAnswer(invocation -> {
            RoleDto source = invocation.getArgument(0);
            Role target = invocation.getArgument(1);
            target.setIdentifier(source.getIdentifier());
            return null;
        }).when(modelMapper).map(Mockito.any(RoleDto.class), Mockito.any(Role.class));

        RoleDto response = roleService.update(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());

        Mockito.verify(roleRepository).save(existingRole);
    }

    @Test
    void updateTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role with identifier - Admin not found", response.getMessage());

        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);

        boolean response = roleService.delete("Admin");

        Assertions.assertTrue(response);

        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void deleteNotFoundTest() {
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        boolean response = roleService.delete("Admin");

        Assertions.assertFalse(response);

        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllWithPageableTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);

        Page<Role> rolePage = new PageImpl<>(roles);

        Mockito.when(roleRepository.findByDeletedFalse(pageable)).thenReturn(rolePage);
        Mockito.when(modelMapper.map(Mockito.eq(roles), Mockito.any(Type.class))).thenReturn(roleDtos);

        WsDto<RoleDto> response = roleService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1L, response.getTotalRecords());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllWithNullPageableTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);

        Mockito.when(roleRepository.findAll()).thenReturn(roles);
        Mockito.when(modelMapper.map(Mockito.eq(roles), Mockito.any(Type.class))).thenReturn(roleDtos);

        WsDto<RoleDto> response = roleService.findAll(null);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1L, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(1, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllWithNullPageableEmptyTest() {
        Mockito.when(roleRepository.findAll()).thenReturn(List.of());
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());

        WsDto<RoleDto> response = roleService.findAll(null);

        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0L, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(0, response.getSizePerPage());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(1, 5);

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);

        Page<Role> rolePage = new PageImpl<>(roles, pageable, 6);

        Mockito.when(roleRepository.findByDeletedFalse(pageable)).thenReturn(rolePage);
        Mockito.when(modelMapper.map(Mockito.eq(roles), Mockito.any(Type.class))).thenReturn(roleDtos);

        WsDto<RoleDto> response = roleService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(6L, response.getTotalRecords());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(1, response.getPage());
    }

}
