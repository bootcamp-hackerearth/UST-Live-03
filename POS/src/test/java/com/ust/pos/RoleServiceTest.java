package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

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
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Role.class)).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);
        RoleDto response = roleService.save(dto);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveFailure_existingActive() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role existing = new Role();
        existing.setDeleted(false);
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existing);
        RoleDto response = roleService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveFailure_softDeleted() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role existing = new Role();
        existing.setDeleted(true);

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existing);
        RoleDto response = roleService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertTrue(response.getMessage().contains("soft deleted"));
    }

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(dto);
        RoleDto response = roleService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role existing = new Role();
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(roleRepository.save(existing)).thenReturn(existing);
        RoleDto response = roleService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(roleRepository).save(existing);
    }

    @Test
    void updateFailure() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        RoleDto response = roleService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Role role = new Role();
        role.setDeleted(false);

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);

        roleService.delete("Admin");
        Mockito.verify(roleRepository).findByIdentifier("Admin");
        Mockito.verify(roleRepository).save(role);
        Assertions.assertTrue(role.isDeleted());
    }

    @Test
    void findAllTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        List<Role> list = List.of(role);
        Page<Role> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(roleRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));

        WsDto<RoleDto> response = roleService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void toggleStatusSuccessTest() {
        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(false);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        dto.setStatus(true);

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(dto);
        RoleDto response = roleService.toggleStatus("Admin", true);
        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleStatusFailureTest() {
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        RoleDto response = roleService.toggleStatus("Admin", true);
        Assertions.assertNull(response);
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findActiveRoleTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setStatus(true);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        dto.setStatus(true);

        List<Role> list = List.of(role);

        Mockito.when(roleRepository.findByStatusTrue()).thenReturn(list);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(List.of(dto));
        List<RoleDto> response = roleService.findActiveRole();
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("ADMIN", response.get(0).getIdentifier());
        Assertions.assertTrue(response.get(0).isStatus());
    }
}