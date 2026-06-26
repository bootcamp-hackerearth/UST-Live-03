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
    void saveTestSuccess() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(dto, Role.class))
                .thenReturn(role);
        Mockito.when(roleRepository.save(role))
                .thenReturn(role);

        RoleDto response = roleService.save(dto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void saveTestFailureWhenExists() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(new Role());

        RoleDto response = roleService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(roleRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto response = roleService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void findByIdentifierNullTest() {
        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);
        Mockito.when(modelMapper.map(null, RoleDto.class))
                .thenReturn(null);

        RoleDto response = roleService.findByIdentifier("Admin");

        Assertions.assertNull(response);
    }

    @Test
    void updateTestSuccess() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role existingRole = new Role();
        existingRole.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(existingRole);

        Mockito.doNothing()
                .when(modelMapper)
                .map(dto, existingRole);

        Mockito.when(roleRepository.save(existingRole))
                .thenReturn(existingRole);

        RoleDto response = roleService.update(dto);

        Assertions.assertTrue(response.isSuccess());

        Mockito.verify(modelMapper).map(dto, existingRole);
        Mockito.verify(roleRepository).save(existingRole);
    }

    @Test
    void updateTestFailureWhenNotFound() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);

        RoleDto response = roleService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());

        Mockito.verify(roleRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void deleteTest() {
        Role role = new Role();
        role.setIdentifier("Admin");
        role.setDeleted(false);

        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(role);

        roleService.delete("Admin");

        Assertions.assertTrue(role.isDeleted());

        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void deleteTestWhenRoleNotFound() {
        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);

        roleService.delete("Admin");

        Mockito.verify(roleRepository, Mockito.never())
                .save(Mockito.any());
    }

    @Test
    void findAllTest() {
        List<Role> roles = List.of(new Role());
        List<RoleDto> roleDtos = List.of(new RoleDto());

        Type listType = new TypeToken<List<RoleDto>>() {
        }.getType();

        Mockito.when(roleRepository.findByDeletedFalse())
                .thenReturn(roles);
        Mockito.when(modelMapper.map(roles, listType))
                .thenReturn(roleDtos);

        List<RoleDto> response = roleService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllWithPaginationTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Page<Role> page = new PageImpl<>(List.of(role));

        Mockito.when(roleRepository.findByDeletedFalse(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        Page<RoleDto> response = roleService.findAll(pageable, null);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals("Admin", response.getContent().get(0).getIdentifier());

        Mockito.verify(roleRepository).findByDeletedFalse(pageable);
    }

    @Test
    void findAllWithSearchTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Page<Role> page = new PageImpl<>(List.of(role));

        Mockito.when(roleRepository.findByIdentifierContainingIgnoreCaseAndDeletedFalse("Admin", pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        Page<RoleDto> response = roleService.findAll(pageable, "Admin");

        Assertions.assertEquals(1, response.getContent().size());

        Mockito.verify(roleRepository)
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse("Admin", pageable);
    }
}