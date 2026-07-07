package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourseNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

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
    void findActiveRolesTest() {

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        when(roleRepository.findByStatus(true))
                .thenReturn(List.of(role));

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        List<RoleDto> result =
                roleService.findActiveRoles();

        assertEquals(1, result.size());
        assertEquals("ADMIN",
                result.getFirst().getIdentifier());

        verify(roleRepository).findByStatus(true);
    }

    @Test
    void toggleStatusTrueToFalseTest() {

        Role role = new Role();
        role.setStatus(true);

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        roleService.toggleStatus("ADMIN");

        assertFalse(role.isStatus());

        verify(roleRepository).save(role);
    }

    @Test
    void toggleStatusFalseToTrueTest() {

        Role role = new Role();
        role.setStatus(false);

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        roleService.toggleStatus("ADMIN");

        assertTrue(role.isStatus());

        verify(roleRepository).save(role);
    }

    @Test
    void toggleStatusRoleNotFoundTest() {

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);

        roleService.toggleStatus("ADMIN");

        verify(roleRepository)
                .findByIdentifier("ADMIN");

        verify(roleRepository, never())
                .save(any());
    }

    @Test
    void findByIdentifierFailureTest() {

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);

        assertThrows(
                ResourseNotFoundException.class,
                () -> roleService.findByIdentifier("ADMIN")
        );
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

    @Test
    void findAllWithSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Role> specification = mock(Specification.class);

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");

        List<Role> roleList = List.of(role);

        Page<Role> page = new PageImpl<>(roleList, pageable, 1);

        when(roleRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(roleList), any(Type.class)))
                .thenReturn(List.of(roleDto));

        WsDto<RoleDto> result =
                roleService.findAll(specification, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("ADMIN",
                result.getContent().get(0).getIdentifier());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(roleRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(roleList), any(Type.class));
    }

    @Test
    void findAllWithSpecificationEmptyResultTest() {

        Pageable pageable = PageRequest.of(0, 10);

        @SuppressWarnings("unchecked")
        Specification<Role> specification = mock(Specification.class);

        Page<Role> page =
                new PageImpl<>(List.of(), pageable, 0);

        when(roleRepository.findAll(specification, pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(List.of()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<RoleDto> result =
                roleService.findAll(specification, pageable);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(roleRepository)
                .findAll(specification, pageable);

        verify(modelMapper)
                .map(eq(List.of()), any(Type.class));
    }
}
