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
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("R1");

        Mockito.when(roleRepository.findByIdentifier("R1")).thenReturn(null);
        Role role = new Role();
        Mockito.when(modelMapper.map(roleDto, Role.class)).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);
        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("R1", response.getIdentifier());
        Assertions.assertEquals(true, response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("R1");
        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("R1")).thenReturn(role);
        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("R1", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());
    }

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        role.setIdentifier("R1");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("R1");

        Mockito.when(roleRepository.findByIdentifier("R1")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto response = roleService.findByIdentifier("R1");

        Assertions.assertEquals("R1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("R1");

        Role existingRole = new Role();
        existingRole.setIdentifier("R1");

        Mockito.when(roleRepository.findByIdentifier("R1"))
                .thenReturn(existingRole);
        Mockito.when(roleRepository.save(existingRole))
                .thenReturn(existingRole);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("R1");

        Mockito.when(roleRepository.findByIdentifier("R1"))
                .thenReturn(null);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(roleRepository)
                .deleteByIdentifier("R1");

        roleService.delete("R1");

        Mockito.verify(roleRepository).deleteByIdentifier("R1");
    }

    @Test
    void findAllTest() {
        Role role = new Role();
        role.setIdentifier("R1");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("R1");

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
    void findAllWithPaginationShouldReturnRoleDtos() {
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