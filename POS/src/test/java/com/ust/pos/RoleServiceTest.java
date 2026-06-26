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
    void findByIdentifier_Found() {

        Role role = new Role();
        role.setIdentifier("ROLE1");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE1");

        when(roleRepository.findByIdentifier("ROLE1"))
                .thenReturn(role);

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto result = roleService.findByIdentifier("ROLE1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ROLE1", result.getIdentifier());
    }

    @Test
    void save_NewRole() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE1");

        Role role = new Role();

        when(roleRepository.findByIdentifier("ROLE1"))
                .thenReturn(null);

        when(modelMapper.map(dto, Role.class))
                .thenReturn(role);

        when(roleRepository.save(role))
                .thenReturn(role);

        RoleDto result = roleService.save(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ROLE1", result.getIdentifier());
        verify(roleRepository).save(role);
    }

    @Test
    void save_RoleExists() {

        Role existing = new Role();

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE1");

        when(roleRepository.findByIdentifier("ROLE1"))
                .thenReturn(existing);

        RoleDto result = roleService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void update_RoleExists() {

        Role existing = new Role();

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE1");

        when(roleRepository.findByIdentifier("ROLE1"))
                .thenReturn(existing);

        when(roleRepository.save(existing))
                .thenReturn(existing);

        RoleDto result = roleService.update(dto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("ROLE1", result.getIdentifier());
        verify(modelMapper).map(dto, existing);
        verify(roleRepository).save(existing);
    }

    @Test
    void update_RoleNotFound() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE1");

        when(roleRepository.findByIdentifier("ROLE1"))
                .thenReturn(null);

        RoleDto result = roleService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void deleteTest() {

        Role role = new Role();

        when(roleRepository.findByIdentifier("ROLE1"))
                .thenReturn(role);

        roleService.delete("ROLE1");

        verify(roleRepository).findByIdentifier("ROLE1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Role role1 = new Role();
        Role role2 = new Role();

        Page<Role> page = new PageImpl<>(
                List.of(role1, role2),
                pageable,
                2
        );

        List<RoleDto> dtoList = List.of(new RoleDto(), new RoleDto());

        when(roleRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class)))
                .thenReturn(dtoList);

        WsDto<RoleDto> result = roleService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(2, result.getTotalRecords());

        verify(roleRepository).findByIsDeletedFalse(pageable);
    }

    @Test
    void findAllEmptyTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Role> page = new PageImpl<>(List.of(), pageable, 0);

        when(roleRepository.findByIsDeletedFalse(pageable))
                .thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class)))
                .thenReturn(List.of());

        WsDto<RoleDto> result = roleService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getContent().isEmpty());
        Assertions.assertEquals(0, result.getTotalRecords());

        verify(roleRepository).findByIsDeletedFalse(pageable);
    }
}