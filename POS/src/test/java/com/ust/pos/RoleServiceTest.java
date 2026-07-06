package com.ust.pos;

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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void saveTestSuccess() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());
    }

    @Test
    void saveTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();
        role.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestFailure_softDeletedRecord() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role existingRole = new Role();
        existingRole.setIdentifier("Admin");
        existingRole.setDeleted(true);

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(existingRole);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertFalse(response.isSuccess());
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
    void findByIdentifierNotFoundTest() {
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.findByIdentifier("Admin")
        );

        Assertions.assertEquals("Role with identifier 'Admin'not found", exception.getMessage());
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

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest_softDeletesAndSaves() {
        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(true);
        role.setDeleted(false);

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);

        roleService.delete("Admin");

        Assertions.assertTrue(role.isDeleted());
        Assertions.assertFalse(role.isStatus());
        Mockito.verify(roleRepository, times(1)).save(role);
    }

    @Test
    void findAllTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Page<Role> page = new PageImpl<>(List.of(role));

        Mockito.when(roleRepository.findByDeletedFalse(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(roleDto));

        Pageable pageable = PageRequest.of(0, 50, Sort.unsorted());
        List<RoleDto> response = roleService.findAll(pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
    }

    @Test
    void findAllWithSpecificationTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Page<Role> page = new PageImpl<>(List.of(role));
        Specification<Role> spec = Mockito.mock(Specification.class);

        Mockito.when(roleRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class)))
                .thenReturn(page);

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType))
                .thenReturn(List.of(roleDto));

        Pageable pageable = PageRequest.of(0, 50, Sort.unsorted());
        List<RoleDto> response = roleService.findAll(spec, pageable).getDtoList();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
    }

    @Test
    void changeRoleStatusSuccessTest() {
        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(false);

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        roleDto.setStatus(true);

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto response = roleService.changeRoleStatus("Admin", true);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void changeRoleStatusFailureTest() {
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        RoleDto response = roleService.changeRoleStatus("Admin", true);

        Assertions.assertNull(response);
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveRoleTest() {
        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(true);

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();

        Mockito.when(roleRepository.findByStatusTrue(true))
                .thenReturn(List.of(role));

        Mockito.when(modelMapper.map(List.of(role), listType))
                .thenReturn(List.of(roleDto));

        List<RoleDto> response = roleService.findActiveRole();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
    }
}