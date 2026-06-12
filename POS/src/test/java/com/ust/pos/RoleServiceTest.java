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

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void findByIdentifierSuccessTest() {

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto response = roleService.findByIdentifier("ADMIN");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("ADMIN", response.getIdentifier());
    }

    @Test
    void findByIdentifierFailureTest() {

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);

        RoleDto response = roleService.findByIdentifier("ADMIN");

        Assertions.assertNull(response);
    }

    @Test
    void saveTestSuccess() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);

        Mockito.when(modelMapper.map(dto, Role.class))
                .thenReturn(role);

        RoleDto response = roleService.save(dto);

        Assertions.assertEquals("ADMIN", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(roleRepository).save(role);
    }

    @Test
    void saveTestFailure() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(new Role());

        RoleDto response = roleService.save(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTestSuccess() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Role role = new Role();

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        RoleDto response = roleService.update(dto);

        Assertions.assertEquals("ADMIN", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNull(response.getMessage());

        verify(roleRepository).save(role);
    }

    @Test
    void updateTestFailure() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);

        RoleDto response = roleService.update(dto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTestSuccess() {

        roleService.delete("ADMIN");
        verify(roleRepository).deleteByIdentifier("ADMIN");
    }

    public void delete(String identifier) {

        if (identifier == null) {
            return;
        }

        roleRepository.deleteByIdentifier(identifier);
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Role> roles = List.of(new Role());
        Page<Role> page = new PageImpl<>(roles);
        List<RoleDto> dtos = List.of(new RoleDto());

        Mockito.when(roleRepository.findAll(pageable))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                Mockito.any(),
                Mockito.any(Type.class)
        )).thenReturn(dtos);

        WsDto<RoleDto> response = roleService.findAll(pageable);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getContent());
        Assertions.assertEquals(1, response.getContent().size());

        verify(roleRepository).findAll(pageable);
    }
}
