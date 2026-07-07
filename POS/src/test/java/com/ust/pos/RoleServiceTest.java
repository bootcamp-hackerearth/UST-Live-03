package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
    void saveTest() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(roleDto, Role.class)).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNull(response.getMessage());
        Assertions.assertFalse(role.getIsDeleted());
    }

    @Test
    void saveTestFailureAlreadyExists() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role existingRole = new Role();
        existingRole.setIsDeleted(false);

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existingRole);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role with identifier - Admin already exists", response.getMessage());
    }

    @Test
    void saveTestFailureDeletedRole() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role existingRole = new Role();
        existingRole.setIsDeleted(true);

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existingRole);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("was deleted"));
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
    void updateTest() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role existingRole = new Role();
        existingRole.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existingRole);
        Mockito.when(roleRepository.save(existingRole)).thenReturn(existingRole);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());

        Mockito.verify(modelMapper).map(roleDto, existingRole);
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
    }

    @Test
    void deleteTest() {

        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(true);
        role.setIsDeleted(false);

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);

        RoleDto response = roleService.delete("Admin");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Role deleted successfully", response.getMessage());
        Assertions.assertTrue(role.getIsDeleted());
        Assertions.assertFalse(role.getStatus());
    }

    @Test
    void deleteTestFailure() {

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        RoleDto response = roleService.delete("Admin");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role with identifier - Admin not found", response.getMessage());
    }

    @Test
    void findAllTest() {

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);

        Page<Role> page = new PageImpl<>(roles);

        Mockito.when(roleRepository.findByIsDeleted(
                Mockito.eq(false),
                Mockito.any(Pageable.class)
        )).thenReturn(page);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(roleDtos);

        PaginatedResponseDto<RoleDto> response = roleService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findAllActiveTest() {

        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(true);

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);

        Mockito.when(roleRepository.findByStatusAndIsDeleted(true, false)).thenReturn(roles);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(roleDtos);

        List<RoleDto> response = roleService.findAllActive();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
    }

    @Test
    void changeStatusTrueTest() {

        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(false);

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);

        roleService.changeStatus("Admin", true);

        Assertions.assertTrue(role.getStatus());
        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void changeStatusFalseTest() {

        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(true);

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);

        roleService.changeStatus("Admin", false);

        Assertions.assertFalse(role.getStatus());
        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void findByIdentifierNotFoundTest() {

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.findByIdentifier("Admin")
        );

        Assertions.assertEquals(
                "Role with identifier - Admin not found",
                exception.getMessage()
        );
    }

    @Test
    void findAllSpecificationTest() {

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);

        Page<Role> page = new PageImpl<>(roles);

        @SuppressWarnings("unchecked")
        Specification<Role> specification = Mockito.mock(Specification.class);

        Mockito.when(roleRepository.findAll(
                Mockito.eq(specification),
                Mockito.any(Pageable.class)
        )).thenReturn(page);

        Mockito.when(modelMapper.map(
                Mockito.anyList(),
                Mockito.any(Type.class)
        )).thenReturn(roleDtos);

        PaginatedResponseDto<RoleDto> response =
                roleService.findAll(specification, PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(10, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}