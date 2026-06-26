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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void findByIdentifierSuccessTest() {

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto response = roleService.findByIdentifier("ADMIN");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ADMIN", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);
        RoleDto response = roleService.findByIdentifier("ADMIN");

        Assertions.assertNull(response);
    }

    @Test
    void saveTestSuccess() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Role.class))
                .thenReturn(role);

        RoleDto response = roleService.save(dto);

        Assertions.assertEquals("ADMIN", response.getIdentifier());
        assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(roleRepository).save(role);
    }

    @Test
    void saveTestFailure() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(new Role());
        RoleDto response = roleService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestSuccess() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);
        RoleDto response = roleService.update(dto);

        Assertions.assertEquals("ADMIN", response.getIdentifier());
        assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(roleRepository).save(role);
    }

    @Test
    void updateTestFailure() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);
        RoleDto response = roleService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTestSuccess() {

        Role role = new Role();
        role.setIdentifier("ADMIN");

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        boolean result = roleService.delete("ADMIN");

        assertTrue(result);
        assertTrue(role.isDeleted());

        verify(roleRepository)
                .findByIdentifier("ADMIN");
    }

    @Test
    void deleteTestWithQuotedIdentifier() {

        Role role = new Role();
        role.setIdentifier("ADMIN");

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        boolean result = roleService.delete("\"ADMIN\"");

        assertTrue(result);
        assertTrue(role.isDeleted());

        verify(roleRepository)
                .findByIdentifier("ADMIN");
    }

    @Test
    void deleteTestNull() {

        boolean result = roleService.delete(null);
        Assertions.assertFalse(result);

        verify(roleRepository, never()).deleteByIdentifier(any());
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Page<Role> page =
                new PageImpl<>(List.of(role), pageable, 1);

        when(roleRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        WsDto<RoleDto> result =
                roleService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("ADMIN",
                result.getContent().getFirst().getIdentifier());

        assertEquals(0, result.getPage());
        assertEquals(10, result.getSizePerPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalRecords());

        verify(roleRepository)
                .findByIsDeletedFalse(pageable);

        verify(modelMapper)
                .map(role, RoleDto.class);
    }

    @Test
    void saveTestFailure_DeletedRole() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Role existing = new Role();
        existing.setDeleted(true);

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(existing);

        RoleDto response = roleService.save(dto);

        assertFalse(response.isSuccess());

        assertTrue(
                response.getMessage()
                        .contains("already exists but was deleted")
        );
    }
}
