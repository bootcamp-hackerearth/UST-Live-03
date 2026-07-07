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
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.mockito.ArgumentMatchers;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void save_success() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role role = new Role();

        when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        when(modelMapper.map(dto, Role.class))
                .thenReturn(role);

        when(roleRepository.save(role))
                .thenReturn(role);

        RoleDto response = roleService.save(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Role saved successfully",
                response.getMessage()
        );

        verify(roleRepository).save(role);
    }

    @Test
    void save_failure_duplicate() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role existing = new Role();
        existing.setDeleted(false);

        when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        RoleDto response = roleService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Role already exists",
                response.getMessage()
        );

        verify(roleRepository, never()).save(any());
    }

    @Test
    void save_success_whenDeletedRecordExists() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role existing = new Role();
        existing.setDeleted(true);

        Role role = new Role();

        when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        when(modelMapper.map(dto, Role.class))
                .thenReturn(role);

        RoleDto response = roleService.save(dto);

        Assertions.assertTrue(response.isSuccess());

        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void find_success() {

        Role role = new Role();
        role.setDeleted(false);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(role);

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto response =
                roleService.findByIdentifier("Admin");

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(
                "Admin",
                response.getIdentifier()
        );
    }

    @Test
    void find_failure_notFound() {

        when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.findByIdentifier("Admin"));
    }

    @Test
    void findAll_withSpecification() {

        Specification<Role> specification = (root, query, cb) -> cb.conjunction();

        Role role = new Role();
        role.setIdentifier("Admin");

        Page<Role> page = new PageImpl<>(List.of(role));

        when(roleRepository.findAll(eq(specification), any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(anyList(), ArgumentMatchers.<Type>any()))
                .thenReturn(List.of(new RoleDto()));

        WsDto<RoleDto> response =
                roleService.findAll(specification, PageRequest.of(0, 5));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void find_failure_deleted() {

        Role role = new Role();
        role.setDeleted(true);

        when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(role);

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.findByIdentifier("Admin"));
    }

    @Test
    void update_success() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role existing = new Role();
        existing.setDeleted(false);

        RoleDto mappedDto = new RoleDto();

        when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(existing);

        doNothing().when(modelMapper).map(dto, existing);

        when(roleRepository.save(existing))
                .thenReturn(existing);

        when(modelMapper.map(existing, RoleDto.class))
                .thenReturn(mappedDto);

        RoleDto response = roleService.update(dto);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Role updated successfully",
                response.getMessage());

        verify(roleRepository).save(existing);
    }

    @Test
    void update_failure_notFound() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.update(dto));

        verify(roleRepository, never()).save(any());
    }

    @Test
    void update_failure_deleted() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Role role = new Role();
        role.setDeleted(true);

        when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(role);

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.update(dto));

        verify(roleRepository, never()).save(any());
    }

    @Test
    void delete_success() {

        Role role = new Role();
        role.setDeleted(false);

        when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(role);

        roleService.delete("Admin");

        Assertions.assertTrue(role.getDeleted());

        verify(roleRepository).save(role);
    }

    @Test
    void delete_notFound() {

        when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.delete("Admin"));

        verify(roleRepository, never()).save(any());
    }

    @Test
    void delete_alreadyDeleted() {

        Role role = new Role();
        role.setDeleted(true);

        when(roleRepository.findByIdentifier("Admin"))
                .thenReturn(role);

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.delete("Admin"));

        verify(roleRepository, never()).save(any());
    }

    @Test
    void findAll_success() {

        Role role = new Role();
        role.setIdentifier("Admin");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("Admin");

        Page<Role> page = new PageImpl<>(List.of(role));

        when(roleRepository.findByDeletedFalse(any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(
                eq(page.getContent()),
                any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<RoleDto> response =
                roleService.findAll(PageRequest.of(0, 5));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                1,
                response.getDtoList().size()
        );
        Assertions.assertEquals(
                "Admin",
                response.getDtoList().get(0).getIdentifier()
        );
    }
}