package com.ust.pos;

import com.ust.pos.dto.RoleDto;
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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;


    @Test
    void saveTest() {
        //request data
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        Role role = new Role();
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        Mockito.when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        RoleDto result = roleService.save(roleDto);

        Assertions.assertTrue(result.isSuccess());

        Assertions.assertNull(result.getMessage());

    }

    @Test
    void saveTestFailure() {
        //request data
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        RoleDto roleDto1 = roleService.save(roleDto);
        Assertions.assertFalse(roleDto1.isSuccess());
        Assertions.assertNotNull(roleDto1.getMessage());

    }

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto response = roleService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void updateTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role existingRole = new Role();
        existingRole.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(existingRole);
        Mockito.when(roleRepository.save(existingRole))
                .thenReturn(existingRole);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(roleRepository)
                .deleteByIdentifier("Admin");

        roleService.delete("Admin");

        Mockito.verify(roleRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);

        Mockito.when(roleRepository.findAll()).thenReturn(roles);
        Mockito.when(modelMapper.map(
                Mockito.eq(roles),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(roleDtos);

        List<RoleDto> response = roleService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAll_WithPagination_ShouldReturnRoleDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Role> roles = List.of(new Role());
        Page<Role> page = new PageImpl<>(roles);

        List<RoleDto> roleDtos = List.of(new RoleDto());

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();

        Mockito.when(roleRepository.findAll(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(roles, listType))
                .thenReturn(roleDtos);

        List<RoleDto> response = roleService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Mockito.verify(roleRepository).findAll(pageable);
        Mockito.verify(modelMapper).map(roles, listType);
    }
}