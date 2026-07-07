package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
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
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(null);
        Role role = new Role();
        Mockito.when(modelMapper.map(dto, Role.class)).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);
        RoleDto response = roleService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestAlreadyExists() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        Role existing = new Role();
        existing.setDeleted(false);
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existing);
        RoleDto response = roleService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void saveTestDeletedExists() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        Role existing = new Role();
        existing.setDeleted(true);
        Mockito.when(roleRepository.findByIdentifier("Admin")).thenReturn(existing);
        RoleDto response = roleService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        Role existing = new Role();
        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin")).thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(roleRepository.save(existing)).thenReturn(existing);
        RoleDto response = roleService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin")).thenReturn(null);
        RoleDto response = roleService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Role role = new Role();
        RoleDto dto = new RoleDto();
        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin")).thenReturn(role);
        Mockito.when(modelMapper.map(role, RoleDto.class)).thenReturn(dto);
        RoleDto response = roleService.findByIdentifier("Admin");
        Assertions.assertNotNull(response);
    }

    @Test
    void deleteTest() {
        Role role = new Role();
        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin")).thenReturn(role);
        Mockito.when(roleRepository.save(role)).thenReturn(role);
        roleService.delete("Admin");
        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void deleteNullTest() {
        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin")).thenReturn(null);
        roleService.delete("Admin");
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findAllTest() {
        Role role = new Role();
        RoleDto dto = new RoleDto();
        List<Role> list = List.of(role);
        List<RoleDto> dtoList = List.of(dto);
        Pageable pageable = PageRequest.of(0, 1);
        Page<Role> page = new PageImpl<>(list);
        Mockito.when(roleRepository.findByDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(list), Mockito.any(Type.class))).thenReturn(dtoList);
        WsDto<RoleDto> response = roleService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
    }

    @Test
    void findByIdentifierNotFoundTest() {
        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);
        Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.findByIdentifier("Admin")
        );
    }

    @Test
    void findAllWithSpecificationTest() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<Role> page = new PageImpl<>(List.of(new Role()));
        List<RoleDto> dtoList = List.of(new RoleDto());
        Mockito.when(roleRepository.findAll(
                        Mockito.<org.springframework.data.jpa.domain.Specification<Role>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        Mockito.when(modelMapper.map(
                        Mockito.eq(page.getContent()),
                        Mockito.any(Type.class)))
                .thenReturn(dtoList);
        WsDto<RoleDto> response =
                roleService.findAll(
                        Mockito.mock(org.springframework.data.jpa.domain.Specification.class),
                        pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPages());
    }
}