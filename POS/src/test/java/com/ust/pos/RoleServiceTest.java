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

    // FIND BY IDENTIFIER

    @Test
    void findByIdentifier_Success() {

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifierAndIsDeleteFalse("ADMIN"))
                .thenReturn(role);

        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto result = roleService.findByIdentifier("ADMIN");

        Assertions.assertEquals("ADMIN", result.getIdentifier());
    }

    // SAVE

    @Test
    void save_Success() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Role entity = new Role();

        Mockito.when(roleRepository.findByIdentifierAndIsDeleteFalse("ADMIN"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Role.class))
                .thenReturn(entity);

        Mockito.when(roleRepository.save(entity)).thenReturn(entity);

        RoleDto result = roleService.save(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(roleRepository).save(entity);
    }

    @Test
    void save_WhenExists_ShouldFail() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifierAndIsDeleteFalse("ADMIN"))
                .thenReturn(new Role());

        RoleDto result = roleService.save(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(roleRepository, Mockito.never())
                .save(Mockito.any());
    }

    // UPDATE

    @Test
    void update_Success() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Role existing = new Role();
        existing.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifierAndIsDeleteFalse("ADMIN"))
                .thenReturn(existing);

        Mockito.doNothing().when(modelMapper).map(dto, existing);

        Mockito.when(roleRepository.save(existing)).thenReturn(existing);

        RoleDto result = roleService.update(dto);

        Assertions.assertTrue(result.isSuccess());

        Mockito.verify(roleRepository).save(existing);
    }

    @Test
    void update_WhenNotFound_ShouldFail() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifierAndIsDeleteFalse("ADMIN"))
                .thenReturn(null);

        RoleDto result = roleService.update(dto);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());

        Mockito.verify(roleRepository, Mockito.never())
                .save(Mockito.any());
    }

    // DELETE (SOFT DELETE)

    @Test
    void delete_Success() {

        Role role = new Role();
        role.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifierAndIsDeleteFalse("ADMIN"))
                .thenReturn(role);

        Mockito.when(roleRepository.save(role)).thenReturn(role);

        roleService.delete("ADMIN");

        Assertions.assertTrue(role.isDelete());

        Mockito.verify(roleRepository).save(role);
    }

    @Test
    void delete_WhenNotFound_ShouldDoNothing() {

        Mockito.when(roleRepository.findByIdentifierAndIsDeleteFalse("ADMIN"))
                .thenReturn(null);

        roleService.delete("ADMIN");

        Mockito.verify(roleRepository, Mockito.never())
                .save(Mockito.any());
    }

    // FIND ALL (LIST)

    @Test
    void findAll_List_Success() {

        List<Role> roles = List.of(new Role());
        List<RoleDto> dtos = List.of(new RoleDto());

        Type type = new TypeToken<List<RoleDto>>() {
        }.getType();

        Mockito.when(roleRepository.findByIsDeleteFalse())
                .thenReturn(roles);

        Mockito.when(modelMapper.map(roles, type))
                .thenReturn(dtos);

        List<RoleDto> result = roleService.findAll();

        Assertions.assertEquals(1, result.size());
    }

    // PAGINATION

    @Test
    void findAll_WithPagination_NoSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Page<Role> page = new PageImpl<>(List.of(role));

        Mockito.when(roleRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        Page<RoleDto> result = roleService.findAll(pageable, null);

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findAll_WithSearch() {

        Pageable pageable = PageRequest.of(0, 10);

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Page<Role> page = new PageImpl<>(List.of(role));

        Mockito.when(roleRepository
                        .findByIdentifierContainingIgnoreCaseAndIsDeleteFalse("ADM", pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        Page<RoleDto> result = roleService.findAll(pageable, "ADM");

        Assertions.assertEquals(1, result.getContent().size());
    }

    @Test
    void findAll_WithBlankSearch_ShouldFallback() {

        Pageable pageable = PageRequest.of(0, 10);

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Page<Role> page = new PageImpl<>(List.of(role));

        Mockito.when(roleRepository.findByIsDeleteFalse(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        Page<RoleDto> result = roleService.findAll(pageable, " ");

        Assertions.assertEquals(1, result.getContent().size());
    }
}