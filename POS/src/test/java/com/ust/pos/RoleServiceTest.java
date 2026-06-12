package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
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
    void findByIdentifier() {
        Role role = new Role();
        role.setIdentifier("ROLE001");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE001");

        when(roleRepository.findByIdentifier("ROLE001")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(dto);

        RoleDto result = roleService.findByIdentifier("ROLE001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ROLE001", result.getIdentifier());
    }

    @Test
    void saveSuccess() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE001");

        Role role = new Role();
        role.setIdentifier("ROLE001");

        when(roleRepository.findByIdentifier("ROLE001")).thenReturn(null);
        when(modelMapper.map(dto, Role.class)).thenReturn(role);

        RoleDto result = roleService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ROLE001", result.getIdentifier());

        verify(roleRepository).save(role);
    }

    @Test
    void saveFailure() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE001");

        Role existing = new Role();

        when(roleRepository.findByIdentifier("ROLE001")).thenReturn(existing);

        RoleDto result = roleService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(roleRepository, never()).save(any());
    }

    @Test
    void updateSuccess() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE001");

        Role role = new Role();
        role.setIdentifier("ROLE001");

        when(roleRepository.findByIdentifier("ROLE001")).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);

        RoleDto result = roleService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ROLE001", result.getIdentifier());

        verify(modelMapper).map(dto, role);
        verify(roleRepository).save(role);
    }

    @Test
    void updateFailure() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE001");

        when(roleRepository.findByIdentifier("ROLE001")).thenReturn(null);

        RoleDto result = roleService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        verify(roleRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        doNothing().when(roleRepository).deleteByIdentifier("ROLE001");

        roleService.delete("ROLE001");

        verify(roleRepository).deleteByIdentifier("ROLE001");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Role> roles = List.of(new Role(), new Role());
        Page<Role> page = new PageImpl<>(roles, pageable, 2);

        List<RoleDto> dtoList = List.of(new RoleDto(), new RoleDto());

        when(roleRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(roles), any(Type.class))).thenReturn(dtoList);

        WsDto<RoleDto> result = roleService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(2, result.getTotalRecords());
    }

    @Test
    void findAllEmptyTest() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Role> roles = List.of();
        Page<Role> page = new PageImpl<>(roles, pageable, 0);

        when(roleRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(eq(roles), any(Type.class))).thenReturn(List.of());

        WsDto<RoleDto> result = roleService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalRecords());

        verify(roleRepository).findAll(pageable);
    }
}