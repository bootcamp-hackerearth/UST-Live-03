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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void findAll_success() {

        Role role = new Role();
        RoleDto dto = new RoleDto();

        Page<Role> page = new PageImpl<>(List.of(role));

        Mockito.when(roleRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(role)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<RoleDto> result =
                roleService.findAll(Mockito.mock(Pageable.class));

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void save_success() {

        RoleDto input = new RoleDto();
        input.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);

        Role entity = new Role();

        Mockito.when(modelMapper.map(input, Role.class))
                .thenReturn(entity);

        Mockito.when(roleRepository.save(entity))
                .thenReturn(entity);

        RoleDto result = roleService.save(input);

        Assertions.assertEquals("ADMIN", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        RoleDto input = new RoleDto();
        input.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(new Role());

        RoleDto result = roleService.save(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto result = roleService.findByIdentifier("ADMIN");

        Assertions.assertEquals("ADMIN", result.getIdentifier());
    }

    @Test
    void update_success() {

        RoleDto input = new RoleDto();
        input.setIdentifier("ADMIN");

        Role existing = new Role();

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        Mockito.when(roleRepository.save(existing))
                .thenReturn(existing);

        RoleDto result = roleService.update(input);

        Assertions.assertEquals("ADMIN", result.getIdentifier());
    }

    @Test
    void update_failure_notFound() {

        RoleDto input = new RoleDto();
        input.setIdentifier("ADMIN");

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);

        RoleDto result = roleService.update(input);

        Assertions.assertFalse(result.isSuccess());
        Assertions.assertNotNull(result.getMessage());
    }

    @Test
    void delete_success() {

        Mockito.doNothing()
                .when(roleRepository).deleteByIdentifier("ADMIN");

        roleService.delete("ADMIN");

        Mockito.verify(roleRepository)
                .deleteByIdentifier("ADMIN");
    }

    @Test
    void changeToggleStatus_enable() {

        Role role = new Role();
        role.setStatus(false);

        RoleDto dto = new RoleDto();

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        Mockito.when(roleRepository.save(role))
                .thenReturn(role);

        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto result =
                roleService.changeToggleStatus("ADMIN", true);

        Assertions.assertTrue(role.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Role role = new Role();
        role.setStatus(true);

        RoleDto dto = new RoleDto();

        Mockito.when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        Mockito.when(roleRepository.save(role))
                .thenReturn(role);

        Mockito.when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto result =
                roleService.changeToggleStatus("ADMIN", false);

        Assertions.assertFalse(role.isStatus());
        Assertions.assertNotNull(result);
    }

    @Test
    void findActiveStatus_success() {

        Role active = new Role();
        active.setStatus(true);

        Role inactive = new Role();
        inactive.setStatus(false);

        Mockito.when(roleRepository.findAll())
                .thenReturn(List.of(active, inactive));

        RoleDto dto = new RoleDto();

        Mockito.when(modelMapper.map(
                        Mockito.eq(List.of(active)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<RoleDto> result = roleService.findActiveStatus();

        Assertions.assertEquals(1, result.size());
    }
}