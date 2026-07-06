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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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
        Assertions.assertEquals(
                "Role created successfully",
                response.getMessage()
        );
    }

    @Test
    void saveTestFailure() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(role);

        RoleDto response = roleService.save(roleDto);

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

        RoleDto response =
                roleService.findByIdentifier("Admin");

        Assertions.assertEquals(
                "Admin",
                response.getIdentifier()
        );
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

        RoleDto response =
                roleService.update(roleDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Role updated successfully",
                response.getMessage()
        );

        Mockito.verify(roleRepository)
                .save(existingRole);
    }

    @Test
    void updateTestFailure() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        RoleDto response =
                roleService.update(roleDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Role with identifier - Admin not found",
                response.getMessage()
        );
    }

    @Test
    void deleteTest() {

        Role role = new Role();
        role.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(role);

        roleService.delete("Admin");

        Assertions.assertTrue(role.isDeleted());

        Mockito.verify(roleRepository)
                .save(role);
    }

    @Test
    void findAllWithPageableTest() {

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> dtos = List.of(dto);

        Pageable pageable =
                PageRequest.of(0, 5);

        Page<Role> rolePage =
                new PageImpl<>(roles);

        Mockito.when(
                roleRepository.findByDeletedFalse(pageable)
        ).thenReturn(rolePage);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(roles),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<RoleDto> response =
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

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> dtos = List.of(dto);

        Mockito.when(
                roleRepository.findByDeletedFalse(null)
        ).thenReturn(new PageImpl<>(roles));

        Mockito.when(
                modelMapper.map(
                        Mockito.any(),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtos);

        WsDto<RoleDto> response =
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
    void findAllWithSpecificationTest() {

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        List<Role> roles = List.of(role);
        List<RoleDto> roleDtos = List.of(roleDto);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Role> page =
                new PageImpl<>(
                        roles,
                        pageable,
                        1
                );

        Specification<Role> specification =
                Mockito.mock(Specification.class);

        Mockito.when(
                roleRepository.findAll(
                        specification,
                        pageable
                )
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(roles),
                        Mockito.any(Type.class)
                )
        ).thenReturn(roleDtos);

        WsDto<RoleDto> response =
                roleService.findAll(
                        specification,
                        pageable
                );

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                "Admin",
                response.getDtoList()
                        .get(0)
                        .getIdentifier()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );

        Assertions.assertEquals(
                1,
                response.getTotalPages()
        );

        Assertions.assertEquals(
                0,
                response.getPage()
        );

        Assertions.assertEquals(
                5,
                response.getSizePerPage()
        );

        Mockito.verify(roleRepository)
                .findAll(
                        specification,
                        pageable
                );
    }
}