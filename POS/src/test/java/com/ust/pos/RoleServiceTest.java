package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    @DisplayName("Save Role - Success Case")
    void saveTest_Success() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        Role roleEntity = new Role();

        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Role.class)).thenReturn(roleEntity);

        RoleDto result = roleService.save(dto);

        Assertions.assertTrue(result.getMessage().contains("successfully Created"));
        Mockito.verify(roleRepository).save(roleEntity);
    }

    @Test
    @DisplayName("Save Role - Failure: Already Exists")
    void saveTest_AlreadyExists() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(new Role());

        RoleDto result = roleService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        Mockito.verify(roleRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Update Role - Success")
    void updateTest_Success() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("USER");
        Role existingRole = new Role();

        Mockito.when(roleRepository.findByIdentifier("USER")).thenReturn(existingRole);

        RoleDto result = roleService.update(dto);

        Assertions.assertNotNull(result);
        Mockito.verify(modelMapper).map(dto, existingRole);
        Mockito.verify(roleRepository).save(existingRole);
    }

    @Test
    @DisplayName("Update Role - Failure: Not Found")
    void updateTest_NotFound() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("GHOST");

        Mockito.when(roleRepository.findByIdentifier("GHOST")).thenReturn(null);

        RoleDto result = roleService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        Mockito.verify(roleRepository, Mockito.never()).save(any());
    }

    @Test
    @DisplayName("Find All - Paginated Success")
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Role> roles = List.of(new Role());
        Page<Role> rolePage = new PageImpl<>(roles);
        List<RoleDto> dtos = List.of(new RoleDto());

        Mockito.when(roleRepository.findAll(pageable)).thenReturn(rolePage);
        Mockito.when(modelMapper.map(eq(roles), any(Type.class))).thenReturn(dtos);

        List<RoleDto> result = roleService.findAll(pageable);

        Assertions.assertEquals(1, result.size());
        Mockito.verify(roleRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Find All Active - Success")
    void findAllActiveTest() {
        List<Role> activeRoles = List.of(new Role());
        List<RoleDto> dtos = List.of(new RoleDto());

        Mockito.when(roleRepository.findAllByStatus(true)).thenReturn(activeRoles);
        Mockito.when(modelMapper.map(eq(activeRoles), any(Type.class))).thenReturn(dtos);

        List<RoleDto> result = roleService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifierTest() {
        Role role = new Role();
        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(new RoleDto());

        RoleDto result = roleService.findByIdentifier("ADMIN");
        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Toggle Status - Logic Flip")
    void toggleStatusTest() {
        Role role = new Role();
        role.setStatus(false);

        Mockito.when(roleRepository.findByIdentifier("ADMIN")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(new RoleDto());

        roleService.toggleStatus("ADMIN");

        Assertions.assertTrue(role.isStatus());
        Mockito.verify(roleRepository).save(role);
    }

    @Test
    @DisplayName("Delete Role - Success")
    void deleteTest() {
        boolean result = roleService.delete("ADMIN");
        Assertions.assertTrue(result);
        Mockito.verify(roleRepository).deleteByIdentifier("ADMIN");
    }
}