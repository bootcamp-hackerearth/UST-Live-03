package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
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

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void findByIdentifierTestSuccess() {
        Role role = new Role();
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto response = roleService.findByIdentifier("ROLE_ADMIN");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ROLE_ADMIN", response.getIdentifier());
    }

    @Test
    void findByIdentifierTestNotFoundException() {
        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            roleService.findByIdentifier("ROLE_ADMIN");
        });
    }

    @Test
    void saveTestSuccess() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);
        Role role = new Role();
        Mockito.when(modelMapper.map(roleDto, Role.class)).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ROLE_ADMIN", response.getIdentifier());
    }

    @Test
    void saveTestFailureAlreadyExists() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");

        Role existingRole = new Role();
        existingRole.setIdentifier("ROLE_ADMIN");
        existingRole.setDeleted(false);

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(existingRole);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role with identifier - ROLE_ADMIN already exists", response.getMessage());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");

        Role existingRole = new Role();
        existingRole.setIdentifier("ROLE_ADMIN");
        existingRole.setDeleted(true);

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(existingRole);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node with identifier ROLE_ADMIN was previously deleted. Please contact backend team to restore.", response.getMessage());
    }

    @Test
    void updateTestSuccess() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");

        Role existingRole = new Role();
        existingRole.setIdentifier("ROLE_ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(existingRole);
        Mockito.when(roleRepository.save(existingRole)).thenReturn(existingRole);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ROLE_ADMIN", response.getIdentifier());
    }

    @Test
    void updateTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role with identifier - ROLE_ADMIN not found", response.getMessage());
    }

    @Test
    void deleteTestSuccess() {
        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);

        boolean response = roleService.delete("ROLE_ADMIN");

        Assertions.assertTrue(response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);

        boolean response = roleService.delete("ROLE_ADMIN");

        Assertions.assertFalse(response);
    }

    @Test
    void findAllPageableTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Role role = new Role();
        List<Role> roleList = List.of(role);
        Page<Role> rolePage = new PageImpl<>(roleList, pageable, roleList.size());

        RoleDto roleDto = new RoleDto();
        List<RoleDto> roleDtos = List.of(roleDto);

        Mockito.when(roleRepository.findByDeletedFalse(pageable)).thenReturn(rolePage);
        Mockito.when(modelMapper.map(Mockito.eq(roleList), Mockito.any(Type.class))).thenReturn(roleDtos);

        WsDto<RoleDto> response = roleService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }

    @Test
    void findIfTrueTest() {
        Role role = new Role();
        List<Role> roleList = List.of(role);
        RoleDto roleDto = new RoleDto();
        List<RoleDto> roleDtos = List.of(roleDto);

        Mockito.when(roleRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(roleList);
        Mockito.when(modelMapper.map(Mockito.eq(roleList), Mockito.any(Type.class))).thenReturn(roleDtos);

        List<RoleDto> response = roleService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleStatusTest() {
        Role role = new Role();
        role.setStatus(false);
        RoleDto roleDto = new RoleDto();
        roleDto.setStatus(true);

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto response = roleService.toggleStatus("ROLE_ADMIN");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void findAllSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Specification<Role> specification = Mockito.mock(Specification.class);
        Role role = new Role();
        List<Role> roleList = List.of(role);
        Page<Role> page = new PageImpl<>(roleList, pageable, roleList.size());

        RoleDto roleDto = new RoleDto();
        List<RoleDto> roleDtos = List.of(roleDto);

        Mockito.when(roleRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(roleList), Mockito.any(Type.class))).thenReturn(roleDtos);

        WsDto<RoleDto> response = roleService.findAll(specification, pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
    }
}