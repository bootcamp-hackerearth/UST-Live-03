package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
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

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl service;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        RoleDto dto = new RoleDto();

        when(roleRepository.findByIdentifier("R1")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(dto);

        RoleDto result = service.findByIdentifier("R1");

        assertNotNull(result);
    }

    @Test
    void saveSuccessTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("R1");

        when(roleRepository.findByIdentifier("R1")).thenReturn(null);
        when(modelMapper.map(dto, Role.class)).thenReturn(new Role());

        RoleDto result = service.save(dto);

        assertTrue(result.isSuccess());
        verify(roleRepository).save(any());
    }

    @Test
    void saveDuplicateActiveTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("R1");

        Role existing = new Role();
        existing.setDeleted(false);

        when(roleRepository.findByIdentifier("R1")).thenReturn(existing);

        RoleDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void saveDuplicateDeletedTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("R1");

        Role existing = new Role();
        existing.setDeleted(true);

        when(roleRepository.findByIdentifier("R1")).thenReturn(existing);

        RoleDto result = service.save(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("deleted"));
    }

    @Test
    void updateSuccessTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("R1");

        Role existing = new Role();

        when(roleRepository.findByIdentifier("R1")).thenReturn(existing);

        RoleDto result = service.update(dto);

        assertTrue(result.isSuccess());
        verify(roleRepository).save(existing);
    }

    @Test
    void updateNotFoundTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("R1");

        when(roleRepository.findByIdentifier("R1")).thenReturn(null);

        RoleDto result = service.update(dto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Role role = new Role();
        role.setDeleted(false);

        when(roleRepository.findByIdentifier("R1")).thenReturn(role);

        service.delete("R1");

        assertTrue(role.isDeleted());
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 2);

        Page<Role> page = new PageImpl<>(List.of(new Role()), pageable, 1);

        when(roleRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(any(), any(Type.class)))
                .thenReturn(List.of(new RoleDto()));

        WsDto<RoleDto> result = service.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void toggleStatusTrueToFalseTest() {
        Role role = new Role();
        role.setStatus(true);

        when(roleRepository.findByIdentifier("R1")).thenReturn(role);

        service.toggleStatus("R1");

        assertFalse(role.isStatus());
        verify(roleRepository).save(role);
    }

    @Test
    void toggleStatusFalseToTrueTest() {
        Role role = new Role();
        role.setStatus(false);

        when(roleRepository.findByIdentifier("R1")).thenReturn(role);

        service.toggleStatus("R1");

        assertTrue(role.isStatus());
        verify(roleRepository).save(role);
    }

    @Test
    void toggleStatusNotFoundTest() {
        when(roleRepository.findByIdentifier("R1")).thenReturn(null);

        service.toggleStatus("R1");

        verify(roleRepository, never()).save(any());
    }
}