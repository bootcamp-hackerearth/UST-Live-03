package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.modell.Role;
import com.ust.pos.modell.RoleRepository;
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
import java.util.ArrayList;
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
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        Role role = new Role();
        Mockito.when(modelMapper.map(roleDto, Role.class)).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);
        RoleDto response = roleService.save(roleDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());

    }

    @Test
    void saveTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        Role role = new Role();
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        RoleDto response = roleService.save(roleDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");
        Assertions.assertEquals(false, response.isSuccess());

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
    void deleteTest() {
        Mockito.doNothing().when(roleRepository).deleteByIdentifier("Admin");
        roleService.delete("Admin");
        Mockito.verify(roleRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {
        Role role = new Role();
        role.setIdentifier("Admin");
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);
        Page<Role> rolePage = new PageImpl<>(roles, PageRequest.of(0, 2), roles.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(roleRepository.findAll(pageable)).thenReturn(rolePage);
        Mockito.when(modelMapper.map(Mockito.eq(roles), Mockito.any(java.lang.reflect.Type.class))).thenReturn(roleDtos);
        List<RoleDto> response = roleService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }
}