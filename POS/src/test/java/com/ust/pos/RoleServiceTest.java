package com.ust.pos;

import com.ust.pos.dto.PaginationResponseDto;
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
    void saveTestSuccess() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");
        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);
        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertFalse(response.isSuccess());
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
    void findAllWithPageableTest() {

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Role> rolePage = new PageImpl<>(roles);

        Mockito.when(roleRepository.findAll(pageable))
                .thenReturn(rolePage);

        Mockito.when(modelMapper.map(
                Mockito.eq(roles),
                Mockito.any(Type.class)
        )).thenReturn(roleDtos);

        PaginationResponseDto<RoleDto> response =
                roleService.findAll(pageable);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Admin",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void findAllWithoutPageableTest() {

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);

        Mockito.when(roleRepository.findAll())
                .thenReturn(roles);

        Mockito.when(modelMapper.map(
                Mockito.eq(roles),
                Mockito.any(Type.class)
        )).thenReturn(roleDtos);

        PaginationResponseDto<RoleDto> response =
                roleService.findAll(null);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Admin",
                response.getDtoList().get(0).getIdentifier()
        );
    }

    @Test
    void updateStatusSuccessTest() {
        Role role = new Role();
        role.setIdentifier("Admin");
        role.setStatus(false);

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(role);

        RoleDto response = roleService.updateStatus("Admin", true);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
        Assertions.assertTrue(role.isStatus());
    }

    @Test
    void updateStatusFailureTest() {
        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        RoleDto response = roleService.updateStatus("Admin", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Role not found", response.getMessage());
    }

    @Test
    void findByStatusTrueTest() {
        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> dtos = List.of(dto);

        Mockito.when(roleRepository.findByStatusTrue()).thenReturn(roles);
        Mockito.when(modelMapper.map(
                Mockito.eq(roles),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(dtos);

        List<RoleDto> response = roleService.findByStatusTrue();

        Assertions.assertEquals(1, response.size());
    }
}