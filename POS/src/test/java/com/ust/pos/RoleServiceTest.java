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
        roleDto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        Role role = new Role();
        Mockito.when(modelMapper.map(roleDto, Role.class))
                .thenReturn(role);
        Mockito.when(roleRepository.save(role))
                .thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(new Role());

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(roleDto);

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
        Mockito.doNothing()
                .when(roleRepository)
                .deleteByIdentifier("Admin");

        roleService.delete("Admin");

        Mockito.verify(roleRepository)
                .deleteByIdentifier("Admin");
    }

    @Test
    void toggleStatusTest() {
        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(true);

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(role);
        Mockito.when(roleRepository.save(role))
                .thenReturn(role);

        roleService.toggleStatus("Admin");

        Assertions.assertFalse(role.getStatus());
        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void toggleStatusTestFailure() {
        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        roleService.toggleStatus("Admin");

        Mockito.verify(roleRepository, Mockito.never())
                .save(Mockito.any());
    }


    @Test
    void findAllPaginationTest() {

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> rolePage =
                new PageImpl<>(List.of(role), pageable, 1);

        Mockito.when(roleRepository.findAll(pageable))
                .thenReturn(rolePage);

        Mockito.when(modelMapper.map(
                Mockito.eq(rolePage.getContent()),
                Mockito.any(Type.class)
        )).thenReturn(List.of(roleDto));

        List<RoleDto> response = roleService.findAll(pageable);

        Assertions.assertEquals(1, response.size());
    }
}