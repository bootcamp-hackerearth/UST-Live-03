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

import static org.mockito.Mockito.verify;

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
        role.setIdentifier("ROLE_USER");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_USER");

        Mockito.when(roleRepository.findByIdentifierAndIsDeletedFalse("ROLE_USER")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto response = roleService.findByIdentifier("ROLE_USER");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ROLE_USER", response.getIdentifier());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(roleRepository.findByIdentifierAndIsDeletedFalse("ROLE_USER")).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            roleService.findByIdentifier("ROLE_USER");
        });
    }

    @Test
    void saveSuccessTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_USER");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(null);
        Mockito.when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("ROLE_USER", response.getIdentifier());
        verify(roleRepository).save(role);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_USER");

        Role existingRole = new Role();
        existingRole.setDeleted(false);

        Mockito.when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(existingRole);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("ROLE_USER", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role with identifier - ROLE_USER already exists", response.getMessage());
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_USER");

        Role existingRole = new Role();
        existingRole.setDeleted(true);

        Mockito.when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(existingRole);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("ROLE_USER", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role with identifier - ROLE_USER was deleted , Please Contact the Administrator to add.", response.getMessage());
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_USER");

        Role existingRole = new Role();
        existingRole.setIdentifier("ROLE_USER");

        Mockito.when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(existingRole);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertEquals("ROLE_USER", response.getIdentifier());
        verify(modelMapper).map(roleDto, existingRole);
        verify(roleRepository).save(existingRole);
    }

    @Test
    void updateFailureTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_USER");

        Mockito.when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(null);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertEquals("ROLE_USER", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role with identifier - ROLE_USER not found", response.getMessage());
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Role role = new Role();
        Mockito.when(roleRepository.findByIdentifier("ROLE_USER")).thenReturn(role);

        roleService.delete("ROLE_USER");

        verify(roleRepository).findByIdentifier("ROLE_USER");
    }

    @Test
    void findAllSuccessTest() {
        Role role = new Role();
        List<Role> roleList = List.of(role);

        RoleDto dto = new RoleDto();
        List<RoleDto> roleDtos = List.of(dto);

        Page<Role> page = new PageImpl<>(roleList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(roleRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(roleList), Mockito.any(Type.class))).thenReturn(roleDtos);

        WsDto<RoleDto> result = roleService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }

    @Test
    void findAllSpecificationSuccessTest() {
        Role role = new Role();
        List<Role> roleList = List.of(role);

        RoleDto dto = new RoleDto();
        List<RoleDto> roleDtos = List.of(dto);

        Page<Role> page = new PageImpl<>(roleList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Role> specification = Mockito.mock(Specification.class);

        Mockito.when(roleRepository.findAll(specification, pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(roleList), Mockito.any(Type.class))).thenReturn(roleDtos);

        WsDto<RoleDto> result = roleService.findAll(specification, pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
    }
}