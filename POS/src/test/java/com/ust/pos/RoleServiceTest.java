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

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void save_success() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        Role role = new Role();
        role.setIdentifier("Admin");
        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(modelMapper.map(any(RoleDto.class), eq(Role.class))).thenReturn(role);
        RoleDto response = roleService.save(dto);
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void update_success() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        Role existing = new Role();
        existing.setIdentifier("Admin");
        when(roleRepository.findByIdentifier("Admin")).thenReturn(existing);
        when(roleRepository.save(any(Role.class))).thenReturn(existing);
        RoleDto response = roleService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void find_success() {
        Role role = new Role();
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(dto);
        RoleDto response = roleService.findByIdentifier("Admin");
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void delete_test() {
        doNothing().when(roleRepository).deleteByIdentifier("Admin");
        roleService.delete("Admin");
        verify(roleRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAll_with_pageable() {
        Role role = new Role();
        role.setIdentifier("Admin");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        Page<Role> page = new PageImpl<>(List.of(role));
        when(roleRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).
                thenReturn(List.of(dto));
        List<RoleDto> response = roleService.findAll(PageRequest.of(0, 5));
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
    }

    @Test
    void findAll_without_pageable() {
        Role role = new Role();
        role.setIdentifier("Admin");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        Page<Role> page = new PageImpl<>(List.of(role));
        when(roleRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class))).
                thenReturn(List.of(dto));
        List<RoleDto> response = roleService.
                findAll(PageRequest.of(0, 5));
        Assertions.assertEquals(1, response.size());
    }
}