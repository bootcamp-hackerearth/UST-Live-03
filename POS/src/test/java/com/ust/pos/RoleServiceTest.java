package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.RoleDto;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

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
    void saveSuccessTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");
        Role role = new Role();
        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(null);
        Mockito.when(modelMapper.map(roleDto, Role.class)).thenReturn(role);
        RoleDto response = roleService.save(roleDto);
        Assertions.assertNotNull(response);
        Assertions.assertEquals("ADMIN", response.getIdentifier());
        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void saveDuplicateTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");
        Role existingRole = new Role();
        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(existingRole);
        RoleDto response = roleService.save(roleDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveSoftDeletedRoleTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");
        Role existingRole = new Role();
        existingRole.setDeleted(true);
        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(existingRole);
        RoleDto response = roleService.save(roleDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");
        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);
        RoleDto response = roleService.findByIdentifier("ADMIN");
        Assertions.assertNotNull(response);
        Assertions.assertEquals("ADMIN", response.getIdentifier());
    }

    @Test
    void updateSuccessTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");
        Role role = new Role();
        role.setIdentifier("ADMIN");
        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(role);
        RoleDto response = roleService.update(roleDto);
        Assertions.assertNotNull(response);
        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void updateRoleNotFoundTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");
        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(null);
        RoleDto response = roleService.update(roleDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("not found"));
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(role);
        roleService.delete("ADMIN");
        Assertions.assertTrue(role.isDeleted());
        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void deleteRoleNotFoundTest() {
        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(null);
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> roleService.delete("ADMIN"));
        Assertions.assertEquals("Role not found", exception.getMessage());
    }

    @Test
    void toggleStatusSuccessTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setStatus(false);
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");
        roleDto.setStatus(true);
        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);
        RoleDto response = roleService.toggleStatus("ADMIN", true);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(role.isStatus());
        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void toggleStatusRoleNotFoundTest() {
        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(null);
        Mockito.when(modelMapper.map(null, RoleDto.class)).thenReturn(null);
        RoleDto response = roleService.toggleStatus("ADMIN", true);
        Assertions.assertNull(response);
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllWithPageableTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        List<Role> roles = List.of(role);
        List<RoleDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Role> rolePage = new PageImpl<>(roles);
        Mockito.when(roleRepository.findByDeletedFalse(pageable)).thenReturn(rolePage);
        Mockito.when(modelMapper.map(Mockito.eq(roles), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<RoleDto> response = roleService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("ADMIN", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        List<Role> roles = List.of(role);
        List<RoleDto> dtos = List.of(dto);
        Mockito.when(roleRepository.findAll()).thenReturn(roles);
        Mockito.when(modelMapper.map(Mockito.eq(roles),Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<RoleDto> response = roleService.findAll(null);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("ADMIN", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithSpecificationTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        List<Role> roles = List.of(role);
        List<RoleDto> dtos = List.of(dto);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Role> page = new PageImpl<>(roles);
        Specification<Role> specification = Mockito.mock(Specification.class);
        Mockito.when(roleRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(roles), Mockito.any(Type.class))).thenReturn(dtos);
        PaginationResponseDto<RoleDto> response = roleService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("ADMIN", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(roleRepository).findAll(specification, pageable);
    }

    @Test
    void findAllWithSpecificationNoDataTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Specification<Role> specification = Mockito.mock(Specification.class);
        Page<Role> emptyPage = Page.empty(pageable);
        Mockito.when(roleRepository.findAll(specification, pageable)).thenReturn(emptyPage);
        Mockito.when(modelMapper.map(Mockito.eq(List.of()), Mockito.any(Type.class))).thenReturn(List.of());
        PaginationResponseDto<RoleDto> response = roleService.findAll(specification, pageable);
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getDtoList().isEmpty());
        Assertions.assertEquals(0, response.getTotalRecords());
        Assertions.assertEquals(0, response.getTotalPages());
        Assertions.assertEquals(5, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(roleRepository).findAll(specification, pageable);
    }
}