package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.models.Role;
import com.ust.pos.models.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    void saveTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        Role entity = new Role();
        when(roleRepository.findByIdentifier("ADMIN")).thenReturn(null);
        when(modelMapper.map(dto, Role.class)).thenReturn(entity);
        RoleDto result = roleService.save(dto);
        assertEquals("ADMIN", result.getIdentifier());
        verify(roleRepository).save(entity);
        Role duplicate = new Role();
        duplicate.setDeleted(false);
        when(roleRepository.findByIdentifier("USER")).thenReturn(duplicate);
        RoleDto duplicateDto = new RoleDto();
        duplicateDto.setIdentifier("USER");
        result = roleService.save(duplicateDto);
        assertFalse(result.isSuccess());
        assertEquals("Role with identifier - USER already exists", result.getMessage());
        Role deleted = new Role();
        deleted.setDeleted(true);
        when(roleRepository.findByIdentifier("GUEST")).thenReturn(deleted);
        RoleDto deletedDto = new RoleDto();
        deletedDto.setIdentifier("GUEST");
        result = roleService.save(deletedDto);
        assertFalse(result.isSuccess());
        assertEquals("Role with identifier - GUEST was deleted and cannot be created again.", result.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(dto);
        RoleDto result = roleService.findByIdentifier("ADMIN");
        assertNotNull(result);
        assertEquals("ADMIN", result.getIdentifier());
        when(roleRepository.findByIdentifierAndDeletedFalse("MISSING")).thenReturn(null);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> roleService.findByIdentifier("MISSING"));
        assertEquals("Role with identifier 'MISSING' not found", exception.getMessage());
    }

    @Test
    void updateTest() {
        Role role = new Role();
        role.setIdentifier("ADMIN");
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN")).thenReturn(role);
        RoleDto result = roleService.update(dto);
        verify(modelMapper).map(dto, role);
        verify(roleRepository).save(role);
        assertEquals("ADMIN", result.getIdentifier());
        when(roleRepository.findByIdentifierAndDeletedFalse("USER")).thenReturn(null);
        RoleDto notFound = new RoleDto();
        notFound.setIdentifier("USER");
        result = roleService.update(notFound);
        assertFalse(result.isSuccess());
        assertEquals("Role with identifier - USER not found", result.getMessage());
    }

    @Test
    void deleteTest() {
        Role role = new Role();
        role.setDeleted(false);
        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN")).thenReturn(role);
        roleService.delete("ADMIN");
        assertTrue(role.getDeleted());
        verify(roleRepository).save(role);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        List<Role> roles = List.of(new Role());
        Page<Role> page = new PageImpl<>(roles, pageable, 1);
        when(roleRepository.findAllByDeletedFalse(pageable)).thenReturn(page);
        when(modelMapper.map(eq(roles), any(Type.class))).thenReturn(List.of(new RoleDto()));
        WsDto<RoleDto> result = roleService.findAll(pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(50, result.getSizePerPage());
        assertEquals(0, result.getPage());
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 10);
        @SuppressWarnings("unchecked")
        Specification<Role> specification = mock(Specification.class);
        List<Role> roles = List.of(new Role());
        Page<Role> page = new PageImpl<>(roles, pageable, 1);
        when(roleRepository.findAll(specification, pageable)).thenReturn(page);
        when(modelMapper.map(eq(roles), any(Type.class))).thenReturn(List.of(new RoleDto()));
        WsDto<RoleDto> result = roleService.findAll(specification, pageable);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        verify(roleRepository).findAll(specification, pageable);
    }
}