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
    void saveTestSuccess() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();

        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        verify(roleRepository).save(role);
    }

    @Test
    void saveTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();
        role.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void updateTestSuccess() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();
        role.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(role);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        verify(modelMapper).map(roleDto, role);
        verify(roleRepository).save(role);
    }

    @Test
    void updateTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertNotNull(response.getMessage());
        Assertions.assertFalse(response.isSuccess());
        verify(roleRepository, never()).save(any());
    }

    @Test
    void deleteTestSuccess() {
        roleService.delete("Admin");
        verify(roleRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findByIdentifierSuccessTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto mappedDto = new RoleDto();
        mappedDto.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        when(modelMapper.map(role, RoleDto.class)).thenReturn(mappedDto);

        RoleDto result = roleService.findByIdentifier("Admin");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Admin", result.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {
        when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        RoleDto result = roleService.findByIdentifier("Admin");

        Assertions.assertNull(result);
    }

    @Test
    void findAllTest() {

        Pageable pageable = mock(Pageable.class);
        Page<Role> page = mock(Page.class);

        List<Role> roles = List.of(
                new Role(),
                new Role()
        );

        List<RoleDto> dtoList = List.of(
                new RoleDto(),
                new RoleDto()
        );

        when(roleRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(roles);
        when(page.getTotalElements()).thenReturn(2L);
        when(page.getTotalPages()).thenReturn(1);
        when(pageable.getPageSize()).thenReturn(10);
        when(pageable.getPageNumber()).thenReturn(0);
        when(modelMapper.map(eq(roles), any(Type.class))).thenReturn(dtoList);

        WsDto<RoleDto> result = roleService.findAll(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(dtoList, result.getDtoList());
        Assertions.assertEquals(2L, result.getTotalRecords());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals(10, result.getSizePerPage());
        Assertions.assertEquals(0, result.getPage());

        verify(roleRepository).findAll(pageable);
        verify(page).getContent();
        verify(page).getTotalElements();
        verify(page).getTotalPages();
        verify(pageable).getPageSize();
        verify(pageable).getPageNumber();
        verify(modelMapper).map(eq(roles), any(Type.class));
    }
}