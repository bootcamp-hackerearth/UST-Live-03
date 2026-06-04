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
        //Request data
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        //Mock data
        Role role = new Role();
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(roleDto, Role.class)).thenReturn(role);

        //Original service call
        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());

        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

    }
    @Test
    void findAll_WithPagination_ShouldReturnRoleDtos() {
        Pageable pageable = PageRequest.of(0, 10);

        List<Role> roles = List.of(new Role());
        Page<Role> page = new PageImpl<>(roles);

        List<RoleDto> roleDtos = List.of(new RoleDto());

        Type listType = new TypeToken<List<RoleDto>>() {}.getType();

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

    @Test
    void saveTestFailure() {
        //Request data
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        //Mock data
        Role role = new Role();
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);

        //Original service call
        RoleDto response = roleService.save(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertEquals("Role with identifier - " + roleDto.getIdentifier() + " already exists", response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTestSuccess()
    {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Role role = new Role();
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(role);

        roleService.update(roleDto);

        Assertions.assertEquals("Admin", roleDto.getIdentifier());
        Assertions.assertNull(roleDto.getMessage());
        Assertions.assertTrue(roleDto.isSuccess());
    }

    @Test
    void updateTestFailure()
    {
        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier("Admin");

        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);

        RoleDto response=roleService.update(roleDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertEquals("Role with identifier - " + roleDto.getIdentifier() + " not found", response.getMessage());
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void findByIdentifierTest()
    {
        String identifier = "Admin";

        Role role = new Role();
        role.setIdentifier(identifier);

        RoleDto roleDto = new RoleDto();
        roleDto.setIdentifier(identifier);

        Mockito.when(roleRepository.findByIdentifier(identifier)).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(roleDto);

        RoleDto response = roleService.findByIdentifier(identifier);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(identifier, response.getIdentifier());
    }

    @Test
    void findAllTest()
    {
        Role role1 = new Role();
        role1.setIdentifier("Admin");

        Role role2 = new Role();
        role2.setIdentifier("User");

        List<Role> roleList = new ArrayList<>();
        roleList.add(role1);
        roleList.add(role2);

        RoleDto roleDto1 = new RoleDto();
        roleDto1.setIdentifier("Admin");

        RoleDto roleDto2 = new RoleDto();
        roleDto2.setIdentifier("User");

        List<RoleDto> dtoList = new ArrayList<>();
        dtoList.add(roleDto1);
        dtoList.add(roleDto2);

        Mockito.when(roleRepository.findAll()).thenReturn(roleList);
        Mockito.when(modelMapper.map(Mockito.eq(roleList), Mockito.any(Type.class))).thenReturn(dtoList);

        List<RoleDto> response = roleService.findAll();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.size());
        Assertions.assertEquals("Admin", response.get(0).getIdentifier());
        Assertions.assertEquals("User", response.get(1).getIdentifier());
    }

    @Test
    void deleteTest()
    {
        String identifier = "Admin";
        Mockito.doNothing().when(roleRepository).deleteByIdentifier(identifier);
        roleService.delete(identifier);
        Mockito.verify(roleRepository, Mockito.times(1)).deleteByIdentifier(identifier);
    }
}