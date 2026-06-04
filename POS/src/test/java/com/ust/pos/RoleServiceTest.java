package com.ust.pos;

import com.ust.pos.dao.RoleDao;
import com.ust.pos.dto.RoleDto;
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
    private RoleRepository roleRepository;

    @Mock
    private RoleDao roleDao;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifier_success() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        when(roleRepository.findByIdentifier("ADMIN")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(dto);
        RoleDto result = service.findByIdentifier("ADMIN");
        assertNotNull(result);
        assertEquals("ADMIN", result.getIdentifier());
    }

    @Test
    void findByIdentifier_notFound() {
        when(roleRepository.findByIdentifier("X")).thenReturn(null);
        when(modelMapper.map(null, RoleDto.class)).thenReturn(null);
        RoleDto result = service.findByIdentifier("X");
        assertNull(result);
    }

    @Test
    void save_success() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        when(roleDao.findByIdentifier("ADMIN")).thenReturn(null);
        RoleDto result = service.save(dto);
        verify(roleDao).save(dto);
        assertEquals(dto, result);
    }

    @Test
    void save_duplicate() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        Role existing = new Role();
        existing.setIdentifier("ADMIN");
        when(roleDao.findByIdentifier("ADMIN")).thenReturn(existing);
        RoleDto result = service.save(dto);
        verify(roleDao, never()).save(any());
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void update_success() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        Role existing = new Role();
        existing.setIdentifier("ADMIN");
        when(roleDao.findByIdentifier("ADMIN")).thenReturn(existing);
        RoleDto result = service.update(dto);
        verify(roleDao).update(dto);
        assertEquals(dto, result);
    }

    @Test
    void update_notFound() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        when(roleDao.findByIdentifier("ADMIN")).thenReturn(null);
        RoleDto result = service.update(dto);
        verify(roleDao, never()).update(any());
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void delete_success() {
        service.delete("ADMIN");
        verify(roleDao).deleteByIdentifier("ADMIN");
    }

    @Test
    void findAll_withData() {
        Pageable pageable = PageRequest.of(0, 10);
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        Page<Role> page = new PageImpl<>(List.of(role));
        Type targetType = new TypeToken<List<RoleDto>>() {}.getType();
        when(roleRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(page.getContent(), targetType)).thenReturn(List.of(dto));
        List<RoleDto> result = service.findAll(pageable);
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getIdentifier());
    }

    @Test
    void findAll_empty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> emptyPage = new PageImpl<>(List.of());
        Type targetType = new TypeToken<List<RoleDto>>() {}.getType();
        when(roleRepository.findAll(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(List.of(), targetType)).thenReturn(List.of());
        List<RoleDto> result = service.findAll(pageable);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
