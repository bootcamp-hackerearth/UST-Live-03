package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Role;
import com.ust.pos.modell.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl service;

    @Mock
    private RoleRepository repository;

    @Mock
    private ModelMapper mapper;

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        when(repository.findByIdentifier("ADMIN")).thenReturn(role);
        when(mapper.map(role, RoleDto.class)).thenReturn(dto);

        assertEquals("ADMIN", service.findByIdentifier("ADMIN").getIdentifier());

        when(repository.findByIdentifier("X")).thenReturn(null);
        when(mapper.map(null, RoleDto.class)).thenReturn(null);

        assertNull(service.findByIdentifier("X"));
    }

    @Test
    void saveTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        Role role = new Role();
        role.setIdentifier("ADMIN");

        when(repository.findByIdentifier("ADMIN")).thenReturn(null);
        when(mapper.map(dto, Role.class)).thenReturn(role);

        RoleDto result = service.save(dto);
        verify(repository).save(role);
        assertEquals("ADMIN", result.getIdentifier());

        when(repository.findByIdentifier("ADMIN")).thenReturn(role);

        RoleDto duplicate = service.save(dto);
        assertFalse(duplicate.isSuccess());
        assertTrue(duplicate.getMessage().contains("already exists"));
    }

    @Test
    void updateTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        Role role = new Role();
        role.setIdentifier("ADMIN");

        when(repository.findByIdentifier("ADMIN")).thenReturn(role);

        service.update(dto);
        verify(mapper).map(dto, role);
        verify(repository).save(role);

        when(repository.findByIdentifier("X")).thenReturn(null);
        dto.setIdentifier("X");

        RoleDto failure = service.update(dto);
        assertFalse(failure.isSuccess());
        assertTrue(failure.getMessage().contains("not found"));
    }

    @Test
    void deleteTest() {
        service.delete("ADMIN");
        verify(repository).deleteByIdentifier("ADMIN");
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Type type = new TypeToken<List<RoleDto>>(){}.getType();

        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Page<Role> page = new PageImpl<>(List.of(role), pageable, 1);
        when(repository.findAll(pageable)).thenReturn(page);
        when(mapper.map(page.getContent(), type)).thenReturn(List.of(dto));

        WsDto<RoleDto> result = service.findAll(pageable);
        assertEquals(1, result.getDtoList().size());

        Page<Role> empty = new PageImpl<>(List.of(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(empty);
        when(mapper.map(empty.getContent(), type)).thenReturn(List.of());

        WsDto<RoleDto> emptyResult = service.findAll(pageable);
        assertTrue(emptyResult.getDtoList().isEmpty());
    }

    @Test
    void findAllActiveTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        when(repository.findByStatusTrue()).thenReturn(List.of(role));
        when(mapper.map(role, RoleDto.class)).thenReturn(dto);

        List<RoleDto> result = service.findAllActive();
        assertEquals(1, result.size());

        when(repository.findByStatusTrue()).thenReturn(List.of());
        assertTrue(service.findAllActive().isEmpty());
    }

    @Test
    void toggleStatusTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setStatus(true);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        when(repository.findByIdentifier("ADMIN")).thenReturn(role);

        // ✅ FIX 1: mock save
        when(repository.save(any(Role.class))).thenReturn(role);

        // ✅ FIX 2: mapper
        when(mapper.map(any(Role.class), eq(RoleDto.class))).thenReturn(dto);

        RoleDto result = service.toggleStatus("ADMIN");

        assertFalse(role.getStatus());
        verify(repository).save(role);
        assertNotNull(result);

        when(repository.findByIdentifier("X")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.toggleStatus("X"));

        assertTrue(ex.getMessage().contains("Product not found"));
    }
}