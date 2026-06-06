package com.ust.pos;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void saveTest_Success() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("NewRole");

        Role role = new Role();
        role.setIdentifier("NewRole");

        when(roleRepository.findByIdentifier("NewRole")).thenReturn(null);
        when(modelMapper.map(roleDto, Role.class)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("NewRole", response.getIdentifier());

        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void saveTest_Failure_RoleAlreadyExists() {
        Role existingRole = new Role();
        existingRole.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(existingRole);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role with identifier - Admin already exists", response.getMessage());

        verify(roleRepository, never()).save(any());
    }

    @Test
    void updateTest_Success() {
        Role existingRole = new Role();
        existingRole.setIdentifier("User");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("User");

        when(roleRepository.findByIdentifier("User")).thenReturn(existingRole);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertEquals("User", response.getIdentifier());

        verify(roleRepository).save(existingRole);
    }

    @Test
    void updateTest_Failure_NotFound() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Unknown");

        when(roleRepository.findByIdentifier("Unknown")).thenReturn(null);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role with identifier - Unknown not found", response.getMessage());
    }

    @Test
    void deleteTest() {
        roleService.delete("Admin");
        verify(roleRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllWithPaginationTest() {
        Role r1 = new Role();
        r1.setIdentifier("ADMIN");

        Role r2 = new Role();
        r2.setIdentifier("USER");

        List<Role> roles = List.of(r1, r2);

        RoleDto d1 = new RoleDto();
        d1.setIdentifier("ADMIN");

        RoleDto d2 = new RoleDto();
        d2.setIdentifier("USER");

        List<RoleDto> roleDtos = List.of(d1, d2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Role> rolePage = new PageImpl<>(roles, pageable, roles.size());

        Mockito.when(roleRepository.findAll(pageable)).thenReturn(rolePage);
        Mockito.when(modelMapper.map(Mockito.eq(roles), Mockito.any(Type.class))).thenReturn(roleDtos);

        List<RoleDto> result = roleService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("ADMIN", result.get(0).getIdentifier());
        Assertions.assertEquals("USER", result.get(1).getIdentifier());
        Assertions.assertEquals(0, pageable.getPageNumber());
        Assertions.assertEquals(10, pageable.getPageSize());

        Mockito.verify(roleRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(Mockito.eq(roles), Mockito.any(Type.class));
    }

    @Test
    void findByIdentifierSuccessTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(role);

        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto result = roleService.findByIdentifier("ADMIN");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ADMIN", result.getIdentifier());
    }
}