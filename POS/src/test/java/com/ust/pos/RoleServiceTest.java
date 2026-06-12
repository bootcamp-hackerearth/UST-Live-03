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
    void save_success() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role role = new Role();
        role.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        when(modelMapper.map(dto, Role.class)).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleDto response = roleService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Role saved successfully", response.getMessage());
        Assertions.assertEquals("Admin", response.getIdentifier());

        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void save_failure_duplicate() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(new Role());

        RoleDto response = roleService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role already exists", response.getMessage());

        verify(roleRepository, never()).save(any());
    }

    @Test
    void update_success() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role existing = new Role();
        existing.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(existing);
        when(roleRepository.save(existing)).thenReturn(existing);

        RoleDto response = roleService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Role updated successfully", response.getMessage());

        verify(roleRepository).save(existing);
    }

    @Test
    void update_failure_not_found() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        RoleDto response = roleService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role not found", response.getMessage());

        verify(roleRepository, never()).save(any());
    }

    @Test
    void find_success() {

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto mapped = new RoleDto();
        mapped.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(mapped);

        RoleDto response = roleService.findByIdentifier("Admin");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void find_failure_not_found() {

        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        RoleDto response = roleService.findByIdentifier("Admin");

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role not found", response.getMessage());
    }

    @Test
    void delete_test() {

        doNothing().when(roleRepository).deleteByIdentifier("Admin");

        roleService.delete("Admin");

        verify(roleRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAll_success() {

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Page<Role> page = new PageImpl<>(List.of(role));

        when(roleRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(eq(page.getContent()), any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<RoleDto> response = roleService.findAll(PageRequest.of(0, 5));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
    }
}