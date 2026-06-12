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
        roleDto.setIdentifier("Admin");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(roleDto, Role.class))
                .thenReturn(role);

        Mockito.when(roleRepository.save(role))
                .thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void saveTestFailure() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(roleRepository, Mockito.never())
                .save(Mockito.any());
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

        Mockito.doNothing()
                .when(modelMapper)
                .map(roleDto, existingRole);

        Mockito.when(roleRepository.save(existingRole))
                .thenReturn(existingRole);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper)
                .map(roleDto, existingRole);

        Mockito.verify(roleRepository)
                .save(existingRole);
    }

    @Test
    void updateTestFailure() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(roleRepository, Mockito.never())
                .save(Mockito.any());
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
    void findAllTest() {

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();

        Mockito.when(roleRepository.findAll())
                .thenReturn(roles);

        Mockito.when(modelMapper.map(roles, listType))
                .thenReturn(roleDtos);

        List<RoleDto> response = roleService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllWithPaginationAndSearchTest() {

        Pageable pageable = PageRequest.of(0, 5);

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");

        Page<Role> rolePage =
                new PageImpl<>(List.of(role), pageable, 1);

        Mockito.when(
                roleRepository.findByIdentifierContainingIgnoreCase(
                        "ADM",
                        pageable
                )
        ).thenReturn(rolePage);

        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(roleDto);

        Page<RoleDto> response =
                roleService.findAll(pageable, "ADM");

        Assertions.assertEquals(1, response.getTotalElements());

        Assertions.assertEquals(
                "ADMIN",
                response.getContent().get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithPaginationWithoutSearchTest() {

        Pageable pageable = PageRequest.of(0, 5);

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");

        Page<Role> rolePage =
                new PageImpl<>(List.of(role), pageable, 1);

        Mockito.when(roleRepository.findAll(pageable))
                .thenReturn(rolePage);

        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(roleDto);

        Page<RoleDto> response =
                roleService.findAll(pageable, "");

        Assertions.assertEquals(1, response.getTotalElements());

        Assertions.assertEquals(
                "ADMIN",
                response.getContent().get(0).getIdentifier()
        );
    }
}