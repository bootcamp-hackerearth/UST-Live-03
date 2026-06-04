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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock(lenient = true)
    private ModelMapper modelMapper;

    @Test
    void saveTestSuccess() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();
        role.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        assertEquals("Admin", response.getIdentifier());
        verify(roleRepository).save(role);
    }

    @Test
    void saveTestFailure_WithSpaces() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Ad   min");

        Role existingRole = new Role();
        existingRole.setIdentifier("Admin");

        when(roleRepository.findByIdentifier(anyString())).thenReturn(existingRole);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();
        role.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(role);

        RoleDto response = roleService.update(roleDto);

        assertEquals("Admin", response.getIdentifier());
        verify(modelMapper).map(roleDto, role);
        verify(roleRepository).save(role);
    }

    @Test
    void updateTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertFalse(response.isSuccess());
        assertNotNull(response.getMessage());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void deleteTestSuccess() {
        roleService.delete("Admin");
        verify(roleRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(dto);

        RoleDto result = roleService.findByIdentifier("Admin");

        assertNotNull(result);
        assertEquals("Admin", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        RoleDto result = roleService.findByIdentifier("Admin");

        Assertions.assertNull(result);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Role> roles = List.of(new Role(), new Role());
        Page<Role> page = new PageImpl<>(roles);

        List<RoleDto> dtoList = List.of(new RoleDto(), new RoleDto());

        when(roleRepository.findAll(pageable)).thenReturn(page);

        when(modelMapper.map(
                eq(roles),
                any(java.lang.reflect.Type.class)
        )).thenReturn(dtoList);

        List<RoleDto> result = roleService.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(roleRepository).findAll(pageable);
    }
}