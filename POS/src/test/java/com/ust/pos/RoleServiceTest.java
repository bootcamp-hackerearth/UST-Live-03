package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
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

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

        role = new Role();
        role.setIdentifier("ADMIN");

        roleDto = new RoleDto();
        roleDto.setIdentifier("ADMIN");
    }

    @Test
    void findByIdentifierTest() {

        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN"))
                .thenReturn(role);

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(roleDto);

        RoleDto response =
                roleService.findByIdentifier("ADMIN");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ADMIN",
                response.getIdentifier());
    }

    @Test
    void saveSuccessTest() {

        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN"))
                .thenReturn(null);

        when(modelMapper.map(roleDto, Role.class))
                .thenReturn(role);

        RoleDto response =
                roleService.save(roleDto);

        Assertions.assertNotNull(response);

        verify(roleRepository).save(role);
    }

    @Test
    void saveAlreadyExistsTest() {

        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN"))
                .thenReturn(role);

        RoleDto response =
                roleService.save(roleDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("already exists")
        );

        verify(roleRepository, never())
                .save(any());
    }

    @Test
    void updateSuccessTest() {

        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN"))
                .thenReturn(role);

        doNothing().when(modelMapper)
                .map(roleDto, role);

        RoleDto response =
                roleService.update(roleDto);

        Assertions.assertTrue(response.isSuccess());

        verify(roleRepository)
                .save(role);
    }

    @Test
    void updateRoleNotFoundTest() {

        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN"))
                .thenReturn(null);

        RoleDto response =
                roleService.update(roleDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertTrue(
                response.getMessage()
                        .contains("not found")
        );

        verify(roleRepository, never())
                .save(any());
    }

    @Test
    void deleteSuccessTest() {

        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN"))
                .thenReturn(role);

        roleService.delete("ADMIN");

        Assertions.assertTrue(role.isDeleted());

        verify(roleRepository)
                .save(role);
    }

    @Test
    void deleteRoleNotFoundTest() {

        when(roleRepository.findByIdentifierAndDeletedFalse("ADMIN"))
                .thenReturn(null);

        roleService.delete("ADMIN");

        verify(roleRepository, never())
                .save(any());
    }

    @Test
    void findAllTest() {

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);

        Type listType =
                new TypeToken<List<RoleDto>>() {
                }.getType();

        when(roleRepository.findByDeletedFalse())
                .thenReturn(roles);

        when(modelMapper.map(roles, listType))
                .thenReturn(roleDtos);

        List<RoleDto> response =
                roleService.findAll();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAllWithSearchTest() {

        Page<Role> rolePage =
                new PageImpl<>(List.of(role));

        when(roleRepository
                .findByIdentifierContainingIgnoreCaseAndDeletedFalse(
                        eq("ADMIN"),
                        any(Pageable.class)
                ))
                .thenReturn(rolePage);

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(roleDto);

        Page<RoleDto> response =
                roleService.findAll(
                        "ADMIN",
                        PageRequest.of(0, 10)
                );

        Assertions.assertEquals(1,
                response.getContent().size());
    }

    @Test
    void findAllWithoutSearchTest() {

        Page<Role> rolePage =
                new PageImpl<>(List.of(role));

        when(roleRepository.findByDeletedFalse(any(Pageable.class)))
                .thenReturn(rolePage);

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(roleDto);

        Page<RoleDto> response =
                roleService.findAll(
                        null,
                        PageRequest.of(0, 10)
                );

        Assertions.assertEquals(1,
                response.getContent().size());
    }
}