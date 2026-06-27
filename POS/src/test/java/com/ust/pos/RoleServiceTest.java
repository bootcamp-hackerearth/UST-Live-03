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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
    void saveSuccessTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        when(roleRepository.findByIdentifier("ADMIN")).thenReturn(null);
        Role role = new Role();
        when(modelMapper.map(dto, Role.class)).thenReturn(role);
        roleService.save(dto);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void saveDuplicateTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        when(roleRepository.findByIdentifier("ADMIN")).thenReturn(new Role());
        RoleDto result = roleService.save(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Role with identifier - ADMIN already exists", result.getMessage());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(dto);
        RoleDto result = roleService.findByIdentifier("ADMIN");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("ADMIN", result.getIdentifier());
    }

    @Test
    void updateSuccessTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        Role role = new Role();
        role.setIdentifier("ADMIN");
        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN")).thenReturn(role);
        roleService.update(dto);
        verify(roleRepository).save(role);
    }

    @Test
    void updateNotFoundTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN")).thenReturn(null);
        RoleDto result = roleService.update(dto);
        Assertions.assertFalse(result.isSuccess());
        Assertions.assertEquals("Role with identifier - ADMIN not found", result.getMessage());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN")).thenReturn(role);
        roleService.delete("ADMIN");
        verify(roleRepository).save(role);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        List<Role> roles = List.of(role);
        List<RoleDto> dtos = List.of(dto);
        Page<Role> page = new PageImpl<>(roles, pageable, roles.size());
        when(roleRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(roles), any(Type.class))).thenReturn(dtos);
        WsDto<RoleDto> result = roleService.findAll(pageable);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals("ADMIN", result.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPage());
        Assertions.assertEquals(50, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());
        verify(roleRepository).findAllByDeletedFalse(pageable);
    }

    @Test
    void findByIdentifierReturnsNullEntityTest() {
        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN")).thenReturn(null);
        when(modelMapper.map(null, RoleDto.class)).thenReturn(null);
        RoleDto result = roleService.findByIdentifier("ADMIN");
        Assertions.assertNull(result);
    }

    @Test
    void findAllEmptyPageTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Page<Role> page = new PageImpl<>(List.of(), pageable, 0);
        when(roleRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(List.of()), any(Type.class))).thenReturn(List.of());
        WsDto<RoleDto> result = roleService.findAll(pageable);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getDtoList().isEmpty());
        Assertions.assertEquals(0, result.getTotalRecords());
    }
}