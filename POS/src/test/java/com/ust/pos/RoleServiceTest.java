package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Role;
import com.ust.pos.modell.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    void saveTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        Role role = new Role();
        when(modelMapper.map(roleDto, Role.class)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);
        RoleDto response = roleService.save(roleDto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        Role role = new Role();
        when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        RoleDto response = roleService.save(roleDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("already exists"));
    }

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        role.setIdentifier("Admin");
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);
        RoleDto response = roleService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        Role existingRole = new Role();
        existingRole.setIdentifier("Admin");
        when(roleRepository.findByIdentifier("Admin")).thenReturn(existingRole);
        when(roleRepository.save(existingRole)).thenReturn(existingRole);
        RoleDto response = roleService.update(roleDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        RoleDto response = roleService.update(roleDto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        doNothing().when(roleRepository).deleteByIdentifier("Admin");
        roleService.delete("Admin");
        verify(roleRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {
        Role localRole = new Role();
        localRole.setIdentifier("Admin");
        RoleDto localRoleDto = new RoleDto();
        localRoleDto.setIdentifier("Admin");
        List<Role> roles = List.of(localRole);
        List<RoleDto> roleDtos = List.of(localRoleDto);
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Page<Role> rolePage = new PageImpl<>(roles, pageable, roles.size());
        when(roleRepository.findAll(pageable)).thenReturn(rolePage);
        when(modelMapper.map(eq(roles), any(Type.class))).thenReturn(roleDtos);
        WsDto<RoleDto> response = roleService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPage());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        verify(roleRepository, times(1)).findAll(pageable);
        verify(modelMapper, times(1)).map(eq(roles), any(Type.class));
    }

    @Test
    void findAllActiveTest() {
        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(true);
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        when(roleRepository.findByStatusTrue()).thenReturn(List.of(role));
        when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);
        List<RoleDto> result = roleService.findAllActive();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Admin", result.get(0).getIdentifier());
    }

    @Test
    void toggleStatusTest() {
        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(true);
        RoleDto roleDto = new RoleDto();
        when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);
        roleService.toggleStatus("Admin");
        Assertions.assertFalse(role.getStatus());
        verify(roleRepository).save(role);
    }

    @Test
    void toggleStatus_NullStatusTest() {
        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(null);
        when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(new RoleDto());
        roleService.toggleStatus("Admin");
        Assertions.assertTrue(role.getStatus());
    }

    @Test
    void toggleStatus_NotFoundTest() {
        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                roleService.toggleStatus("Admin")
        );
    }
}