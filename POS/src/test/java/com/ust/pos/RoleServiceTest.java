package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private RoleDto roleDto;
    private Role role;

    @BeforeEach
    void setUp() {
        roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_MANAGER");

        role = new Role();
        role.setIdentifier("ROLE_MANAGER");
        role.setStatus(true);
        role.setDeleted(false);
    }

    @Test
    @DisplayName("Save Role - Success")
    void save_Success() {
        when(roleRepository.findByIdentifier("ROLE_MANAGER")).thenReturn(null);
        when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getMessage().contains("successfully Created"));
        verify(roleRepository).save(role);
    }

    @Test
    @DisplayName("Save Role - Failure: Already Exists")
    void save_Failure_AlreadyExists() {
        when(roleRepository.findByIdentifier("ROLE_MANAGER")).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("already exists"));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("Find By Identifier - Success")
    void findByIdentifier_Success() {
        when(roleRepository.findByIdentifier("ROLE_MANAGER")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto result = roleService.findByIdentifier("ROLE_MANAGER");

        Assertions.assertNotNull(result);
    }

    @Test
    @DisplayName("Find All Roles - Paginated Success")
    void findAll_PaginatedSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> rolePage = new PageImpl<>(List.of(role));

        when(roleRepository.findByDeletedFalse(pageable)).thenReturn(rolePage);
        when(modelMapper.map(eq(rolePage.getContent()), any(Type.class))).thenReturn(List.of(roleDto));

        WsDto<RoleDto> result = roleService.findAll(pageable);

        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertFalse(result.getDtoList().isEmpty());
    }

    @Test
    @DisplayName("Find All Active Roles - Success")
    void findAllActive_Success() {
        List<Role> activeRoles = List.of(role);
        when(roleRepository.findAllByStatusAndDeletedFalse(true)).thenReturn(activeRoles);
        when(modelMapper.map(eq(activeRoles), any(Type.class))).thenReturn(List.of(roleDto));

        List<RoleDto> result = roleService.findAllActive();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Update Role - Success")
    void update_Success() {
        when(roleRepository.findByIdentifier("ROLE_MANAGER")).thenReturn(role);

        RoleDto result = roleService.update(roleDto);

        Assertions.assertNotNull(result);
        verify(roleRepository).save(role);
    }

    @Test
    @DisplayName("Update Role - Failure: Not Found")
    void update_Failure_NotFound() {
        when(roleRepository.findByIdentifier("ROLE_MANAGER")).thenReturn(null);

        RoleDto result = roleService.update(roleDto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertTrue(result.getMessage().contains("not found"));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("Toggle Status - Success")
    void toggleStatus_Success() {
        when(roleRepository.findByIdentifier("ROLE_MANAGER")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto result = roleService.toggleStatus("ROLE_MANAGER");

        Assertions.assertFalse(role.isStatus());
        verify(roleRepository).save(role);
    }

    @Test
    @DisplayName("Delete Role - Success")
    void delete_Success() {
        when(roleRepository.findByIdentifier("ROLE_MANAGER")).thenReturn(role);

        boolean result = roleService.delete("ROLE_MANAGER");

        Assertions.assertTrue(result);
        verify(roleRepository).save(role);
    }

    @Test
    @DisplayName("Delete Role - Failure: Not Found")
    void delete_Failure_NotFound() {
        when(roleRepository.findByIdentifier("ROLE_MANAGER")).thenReturn(null);

        boolean result = roleService.delete("ROLE_MANAGER");

        Assertions.assertFalse(result);
        verify(roleRepository, never()).save(any(Role.class));
    }
}