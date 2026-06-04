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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveSuccessTest() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);
        Mockito.when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("ROLE_ADMIN", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(roleRepository).save(role);
    }

    @Test
    void saveFailureTest() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(new Role());

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("ROLE_ADMIN", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateSuccessTest() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");

        Role existingRole = new Role();
        existingRole.setIdentifier("ROLE_ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(existingRole);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertEquals("ROLE_ADMIN", response.getIdentifier());
        verify(roleRepository).save(existingRole);
    }

    @Test
    void updateFailureTest() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertEquals("ROLE_ADMIN", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteSuccessTest() {

        roleService.delete("ROLE_ADMIN");

        verify(roleRepository).deleteByIdentifier("ROLE_ADMIN");
    }


    @Test
    void findByIdentifierSuccessTest() {

        Role role = new Role();
        role.setIdentifier("ROLE_ADMIN");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ROLE_ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto response = roleService.findByIdentifier("ROLE_ADMIN");

        Assertions.assertEquals("ROLE_ADMIN", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(roleRepository.findByIdentifier("ROLE_ADMIN")).thenReturn(null);

        RoleDto response = roleService.findByIdentifier("ROLE_ADMIN");

        Assertions.assertNull(response);
    }

    @Test
    void findAllSuccessTest() {

        Role r1 = new Role();
        r1.setIdentifier("ROLE_ADMIN");

        Role r2 = new Role();
        r2.setIdentifier("ROLE_USER");

        List<Role> roles = List.of(r1, r2);

        RoleDto d1 = new RoleDto();
        d1.setIdentifier("ROLE_ADMIN");

        RoleDto d2 = new RoleDto();
        d2.setIdentifier("ROLE_USER");

        List<RoleDto> roleDtos = List.of(d1, d2);

        Page<Role> page = new PageImpl<>(roles);
        Pageable pageable = PageRequest.of(0, 20);

        Mockito.when(roleRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(roles), Mockito.any(Type.class))).thenReturn(roleDtos);

        List<RoleDto> result = roleService.findAll(pageable);

        Assertions.assertEquals(2, result.size());
    }
}