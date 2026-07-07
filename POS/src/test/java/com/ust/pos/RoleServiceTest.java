package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleDto roleDto;

    @BeforeEach
    void setUp() {
        roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");
        roleDto.setDescription("Administrator Role");
        roleDto.setSuccess(true);

        role = new Role();
        role.setIdentifier("ROLE_ADMIN");
        role.setDescription("Administrator Role");
        role.setDeleted(false);
    }

    @Test
    void testFindByIdentifier() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto result = roleService.findByIdentifier("ROLE_ADMIN");

        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.getIdentifier());
        verify(roleRepository, times(1)).findByIdentifier("ROLE_ADMIN");
    }

    @Test
    void testSave_RoleAlreadyExists_Active() {
        role.setDeleted(false);
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void testSave_RoleAlreadyExists_SoftDeleted() {
        role.setDeleted(true);
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not available"));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void testSave_NewRole() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);
        when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        assertNotNull(result);
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testUpdate_RoleNotFound() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);

        RoleDto result = roleService.update(roleDto);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void testUpdate_RoleFound() {
        when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);

        RoleDto result = roleService.update(roleDto);

        assertNotNull(result);
        verify(modelMapper, times(1)).map(roleDto, role);
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testDelete_Success() {
        String identifier = "ROLE_ADMIN";
        when(roleRepository.findByIdentifierAndDeletedFalse(identifier)).thenReturn(role);

        assertDoesNotThrow(() -> roleService.delete(identifier));

        verify(roleRepository, times(1)).findByIdentifierAndDeletedFalse(identifier);
        assertTrue(role.getDeleted());
    }

    @Test
    void testFindAll_WithData() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> rolePage = new PageImpl<>(Collections.singletonList(role), pageable, 1);
        Type listType = new TypeToken<List<RoleDto>>() {}.getType();

        when(roleRepository.findAllByDeletedFalse(pageable)).thenReturn(rolePage);
        when(modelMapper.map(rolePage.getContent(), listType)).thenReturn(List.of(roleDto));

        WsDto<RoleDto> result = roleService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1L, result.getTotalRecords());
        assertEquals(1, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(roleRepository, times(1)).findAllByDeletedFalse(pageable);
        verify(modelMapper, times(1)).map(rolePage.getContent(), listType);
    }

    @Test
    void testFindAll_EmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        Type listType = new TypeToken<List<RoleDto>>() {}.getType();

        when(roleRepository.findAllByDeletedFalse(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(emptyPage.getContent(), listType)).thenReturn(Collections.emptyList());

        WsDto<RoleDto> result = roleService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.getDtoList().isEmpty());
        assertEquals(0L, result.getTotalRecords());
        assertEquals(0, result.getTotalPages());
        assertEquals(10, result.getSizePerPage());
        assertEquals(0, result.getPage());

        verify(roleRepository, times(1)).findAllByDeletedFalse(pageable);
        verify(modelMapper, times(1)).map(emptyPage.getContent(), listType);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindAll_WithSpecification_Success() {
        Specification<Role> mockSpec = mock(Specification.class);
        Pageable pageable = PageRequest.of(1, 15);
        List<Role> roleList = Collections.singletonList(role);
        List<RoleDto> roleDtoList = Collections.singletonList(roleDto);
        Page<Role> rolePage = new PageImpl<>(roleList, pageable, 45);
        Type listType = new TypeToken<List<RoleDto>>() {}.getType();

        when(roleRepository.findAll(mockSpec, pageable)).thenReturn(rolePage);
        when(modelMapper.map(rolePage.getContent(), listType)).thenReturn(roleDtoList);

        WsDto<RoleDto> result = roleService.findAll(mockSpec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(45L, result.getTotalRecords());
        assertEquals(3, result.getTotalPages());
        assertEquals(15, result.getSizePerPage());
        assertEquals(1, result.getPage());

        verify(roleRepository, times(1)).findAll(mockSpec, pageable);
        verify(modelMapper, times(1)).map(rolePage.getContent(), listType);
    }
}