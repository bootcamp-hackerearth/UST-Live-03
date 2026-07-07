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
import org.springframework.data.domain.*;
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
    void saveTest_Success() {
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
    }

    @Test
    void saveTest_Failure() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(new Role());
        RoleDto response = roleService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Role with identifier - Admin already exists",
                response.getMessage()
        );
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateTest_Success() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        Role existing = new Role();
        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(existing);
        Mockito.doNothing().when(modelMapper).map(dto, existing);
        Mockito.when(roleRepository.save(existing))
                .thenReturn(existing);
        RoleDto response = roleService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(roleRepository).save(existing);
    }

    @Test
    void updateTest_Failure() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");
        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(null);
        RoleDto response = roleService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Role with identifier - Admin not found",
                response.getMessage()
        );
    }

    @Test
    void deleteTest() {
        Role role = new Role();
        role.setDeleted(false);
        Mockito.when(roleRepository.findByIdentifierAndDeletedFalse("Admin"))
                .thenReturn(role);
        roleService.delete("Admin");
        Assertions.assertTrue(role.isDeleted());
        Mockito.verify(roleRepository).save(role);
    }


    @Test
    void findAllTest() {
        Role role = new Role();
        RoleDto dto = new RoleDto();
        List<Role> roles = List.of(role);
        List<RoleDto> dtos = List.of(dto);
        Type listType = new TypeToken<List<RoleDto>>() {}.getType();
        Mockito.when(roleRepository.findByDeletedFalse())
                .thenReturn(roles);
        Mockito.when(modelMapper.map(roles, listType))
                .thenReturn(dtos);
        List<RoleDto> response = roleService.findAll();
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void findAll_WithPagination_NoSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Role role = new Role();
        RoleDto dto = new RoleDto();
        Page<Role> page = new PageImpl<>(List.of(role));
        Mockito.when(roleRepository.findByDeletedFalse(pageable))
                .thenReturn(page);
        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);
        Page<RoleDto> response = roleService.findAll(pageable, null);
        Assertions.assertEquals(1, response.getContent().size());
        Mockito.verify(roleRepository).findByDeletedFalse(pageable);
    }

    @Test
    void findAll_WithPagination_WithSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        Role role = new Role();
        RoleDto dto = new RoleDto();
        Page<Role> page = new PageImpl<>(List.of(role));
        Mockito.when(roleRepository.findAll(
                        Mockito.<Specification<Role>>any(),
                        Mockito.eq(pageable)))
                .thenReturn(page);
        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);
        Page<RoleDto> response =
                roleService.findAll(pageable, "ABC");
        Assertions.assertEquals(1, response.getContent().size());
        Mockito.verify(roleRepository)
                .findAll(
                        Mockito.<Specification<Role>>any(),
                        Mockito.eq(pageable)
                );
    }
}