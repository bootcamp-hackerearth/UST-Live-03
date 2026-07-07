package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
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
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    private static final String ROLE = "ADMIN";
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void findByIdentifierSuccess() {

        Role entity = new Role();
        entity.setIdentifier(ROLE);

        RoleDto dto = new RoleDto();

        when(roleRepository.findByIdentifier(ROLE)).thenReturn(entity);

        when(modelMapper.map(entity, RoleDto.class)).thenReturn(dto);

        RoleDto result = roleService.findByIdentifier(ROLE);

        assertNotNull(result);

    }

    @Test
    void findByIdentifierNotFound() {

        when(roleRepository.findByIdentifier(ROLE)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> roleService.findByIdentifier(ROLE));

    }

    @Test
    void saveSuccess() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier(ROLE);

        Role role = new Role();

        when(roleRepository.findByIdentifier(ROLE)).thenReturn(null);

        when(modelMapper.map(dto, Role.class)).thenReturn(role);

        RoleDto result = roleService.save(dto);

        assertTrue(result.isSuccess());

        assertEquals("Role added successfully", result.getMessage());

        verify(roleRepository).save(role);

    }

    @Test
    void saveAlreadyExists() {

        Role existing = new Role();
        existing.setIdentifier(ROLE);

        RoleDto dto = new RoleDto();
        dto.setIdentifier(ROLE);

        when(roleRepository.findByIdentifier(ROLE)).thenReturn(existing);

        RoleDto result = roleService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Role 'ADMIN' already exists", result.getMessage());

        verify(roleRepository, never()).save(any());

    }

    @Test
    void saveSoftDeleted() {

        Role existing = new Role();
        existing.setDeleted(true);

        RoleDto dto = new RoleDto();
        dto.setIdentifier(ROLE);

        when(roleRepository.findByIdentifier(ROLE)).thenReturn(existing);

        RoleDto result = roleService.save(dto);

        assertFalse(result.isSuccess());

        assertTrue(result.getMessage().contains("soft deleted"));

    }

    @Test
    void updateSuccess() {

        Role role = new Role();
        role.setIdentifier(ROLE);

        RoleDto dto = new RoleDto();
        dto.setIdentifier(ROLE);

        when(roleRepository.findByIdentifier(ROLE)).thenReturn(role);

        RoleDto result = roleService.update(dto);

        verify(modelMapper).map(dto, role);

        verify(roleRepository).save(role);

        assertEquals(dto, result);

    }

    @Test
    void updateNotFound() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier(ROLE);

        when(roleRepository.findByIdentifier(ROLE)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> roleService.update(dto));

    }

    @Test
    void deleteSuccess() {

        Role role = new Role();

        when(roleRepository.findByIdentifier(ROLE)).thenReturn(role);

        boolean result = roleService.delete(ROLE);

        assertTrue(result);

        verify(roleRepository).save(role);

    }

    @Test
    void deleteNotFound() {

        when(roleRepository.findByIdentifier(ROLE)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> roleService.delete(ROLE));

    }

    @Test
    void findAllWithoutPageable() {

        List<Role> entities = List.of(new Role());

        List<RoleDto> dtos = List.of(new RoleDto());

        when(roleRepository.findAll()).thenReturn(entities);

        when(modelMapper.map(eq(entities), any(Type.class))).thenReturn(dtos);

        WsDto<RoleDto> result = roleService.findAll(null);

        assertEquals(1, result.getDtoList().size());

        assertEquals(1, result.getTotalRecords());

        assertEquals(1, result.getTotalPages());

    }

    @Test
    void findAllPageableSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Role> entities = List.of(new Role());

        List<RoleDto> dtos = List.of(new RoleDto());

        Page<Role> page = new PageImpl<>(entities, pageable, 1);

        when(roleRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtos);

        WsDto<RoleDto> result = roleService.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

        assertEquals(1, result.getTotalRecords());

        assertEquals(10, result.getSizePerPage());

    }

    @Test
    void findAllPageableEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Role> page = new PageImpl<>(Collections.emptyList());

        when(roleRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<RoleDto> result = roleService.findAll(pageable);

        assertEquals(0, result.getDtoList().size());

    }

    @Test
    void findAllSpecificationSuccess() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Role> spec = mock(Specification.class);

        List<Role> entities = List.of(new Role());

        List<RoleDto> dtos = List.of(new RoleDto());

        Page<Role> page = new PageImpl<>(entities);

        when(roleRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(dtos);

        WsDto<RoleDto> result = roleService.findAll(spec, pageable, "admin");

        assertEquals("admin", result.getKeyword());

        assertEquals(1, result.getDtoList().size());

    }

    @Test
    void findAllSpecificationEmpty() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Role> spec = mock(Specification.class);

        Page<Role> page = new PageImpl<>(Collections.emptyList());

        when(roleRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(Collections.emptyList()), any(Type.class))).thenReturn(Collections.emptyList());

        WsDto<RoleDto> result = roleService.findAll(spec, pageable, "abc");

        assertEquals(0, result.getDtoList().size());

        assertEquals("abc", result.getKeyword());

    }

}