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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

        Mockito.when(
                roleRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(null);

        Mockito.when(
                modelMapper.map(roleDto, Role.class)
        ).thenReturn(role);

        RoleDto response = roleService.save(roleDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(roleRepository).save(role);

        Assertions.assertFalse(role.getDeleted());
    }

    @Test
    void saveTestFailure() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(
                roleRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(new Role());

        RoleDto response = roleService.save(roleDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Role with identifier - Admin already exists",
                response.getMessage()
        );

        Mockito.verify(roleRepository,
                Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateTestSuccess() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();

        Mockito.when(
                roleRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(role);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        Mockito.verify(modelMapper)
                .map(roleDto, role);

        Mockito.verify(roleRepository)
                .save(role);
    }

    @Test
    void updateTestFailure() {

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(
                roleRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(null);

        RoleDto response = roleService.update(roleDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals(
                "Role with identifier - Admin not found",
                response.getMessage()
        );

        Mockito.verify(roleRepository,
                Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByIdentifierTest() {

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(
                roleRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(role);

        Mockito.when(
                modelMapper.map(role, RoleDto.class)
        ).thenReturn(roleDto);

        RoleDto response =
                roleService.findByIdentifier("Admin");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "Admin",
                response.getIdentifier()
        );
    }

    @Test
    void findAllTest() {

        List<Role> roleList = new ArrayList<>();
        roleList.add(new Role());
        roleList.add(new Role());

        List<RoleDto> dtoList = new ArrayList<>();
        dtoList.add(new RoleDto());
        dtoList.add(new RoleDto());

        Mockito.when(
                roleRepository.findByDeletedFalse()
        ).thenReturn(roleList);

        Mockito.when(
                modelMapper.map(
                        Mockito.eq(roleList),
                        Mockito.any(Type.class)
                )
        ).thenReturn(dtoList);

        List<RoleDto> response =
                roleService.findAll();

        Assertions.assertEquals(
                2,
                response.size()
        );
    }

    @Test
    void findAllWithPaginationTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        List<Role> roles =
                List.of(new Role());

        Page<Role> page =
                new PageImpl<>(roles, pageable, 1);

        List<RoleDto> dtoList =
                List.of(new RoleDto());

        Type listType =
                new TypeToken<List<RoleDto>>() {}.getType();

        Mockito.when(
                roleRepository.findByDeletedFalse(pageable)
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(roles, listType)
        ).thenReturn(dtoList);

        WsDto<RoleDto> response =
                roleService.findAll(pageable);

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );

        Assertions.assertEquals(
                1,
                response.getTotalRecords()
        );
    }

    @Test
    void findAllSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Role role = new Role();
        role.setIdentifier("Admin");

        Example<Role> example = Example.of(
                role,
                ExampleMatcher.matching()
                        .withMatcher(
                                "identifier",
                                ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase()
                        )
        );

        Page<Role> page =
                new PageImpl<>(List.of(role));

        Mockito.when(
                roleRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        role,
                        RoleDto.class
                )
        ).thenReturn(new RoleDto());

        Page<RoleDto> response =
                roleService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void findAllWithoutSearchTest() {

        Pageable pageable =
                PageRequest.of(0, 10);

        Role role = new Role();

        Example<Role> example = Example.of(new Role());

        Page<Role> page =
                new PageImpl<>(List.of(role));

        Mockito.when(
                roleRepository.findAll(Mockito.any(Example.class), Mockito.eq(pageable))
        ).thenReturn(page);

        Mockito.when(
                modelMapper.map(
                        role,
                        RoleDto.class
                )
        ).thenReturn(new RoleDto());

        Page<RoleDto> response =
                roleService.findAll(example, pageable);

        Assertions.assertEquals(
                1,
                response.getContent().size()
        );
    }

    @Test
    void deleteTest() {

        Role role = new Role();
        role.setDeleted(false);

        Mockito.when(
                roleRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(role);

        roleService.delete("Admin");

        Assertions.assertTrue(
                role.getDeleted()
        );

        Mockito.verify(roleRepository)
                .save(role);
    }

    @Test
    void deleteNotFoundTest() {

        Mockito.when(
                roleRepository.findByIdentifierAndDeletedFalse("Admin")
        ).thenReturn(null);

        roleService.delete("Admin");

        Mockito.verify(
                roleRepository,
                Mockito.never()
        ).save(Mockito.any());
    }
}