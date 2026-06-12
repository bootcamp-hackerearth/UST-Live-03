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
import org.springframework.data.domain.Pageable;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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
        Pageable pageable = Mockito.mock(Pageable.class);

        Page<Role> page = new PageImpl<>(List.of(role));

        when(roleRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(page);

        when(modelMapper.map(
                        Mockito.eq(List.of(role)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        WsDto<RoleDto> result =
                roleService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getDtoList().size());
        assertEquals(1, result.getTotalRecords());    }

    @Test
    void save_success() {

        RoleDto input = new RoleDto();
        input.setIdentifier("ADMIN");

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);

        Role entity = new Role();

        when(modelMapper.map(input, Role.class))
                .thenReturn(entity);

        when(roleRepository.save(entity))
                .thenReturn(entity);

        RoleDto result = roleService.save(input);

        assertEquals("ADMIN", result.getIdentifier());
        Assertions.assertTrue(result.isSuccess());
    }

    @Test
    void save_failure_duplicate() {

        RoleDto input = new RoleDto();
        input.setIdentifier("ADMIN");

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(new Role());

        RoleDto result = roleService.save(input);

        Assertions.assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    void findByIdentifier_success() {

        Role role = new Role();
        role.setIdentifier("ADMIN");

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto result = roleService.findByIdentifier("ADMIN");

        assertEquals("ADMIN", result.getIdentifier());
    }

    @Test
    void update_success() {

        RoleDto input = new RoleDto();
        input.setIdentifier("ADMIN");

        Role existing = new Role();

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(existing);

        Mockito.doNothing()
                .when(modelMapper).map(input, existing);

        when(roleRepository.save(existing))
                .thenReturn(existing);

        RoleDto result = roleService.update(input);

        assertEquals("ADMIN", result.getIdentifier());
    }

    @Test
    void update_failure_notFound() {

        RoleDto input = new RoleDto();
        input.setIdentifier("ADMIN");

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(null);

        RoleDto result = roleService.update(input);

        Assertions.assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
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

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        when(roleRepository.save(role))
                .thenReturn(role);

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto result =
                roleService.changeToggleStatus("ADMIN", true);

        Assertions.assertTrue(role.isStatus());
        assertNotNull(result);
    }

    @Test
    void changeToggleStatus_disable() {

        Role role = new Role();
        role.setStatus(true);

        RoleDto dto = new RoleDto();

        when(roleRepository.findByIdentifier("ADMIN"))
                .thenReturn(role);

        when(roleRepository.save(role))
                .thenReturn(role);

        when(modelMapper.map(role, RoleDto.class))
                .thenReturn(dto);

        RoleDto result =
                roleService.changeToggleStatus("ADMIN", false);

        Assertions.assertFalse(role.isStatus());
        assertNotNull(result);
    }

    @Test
    void findActiveStatus_success() {

        Role active = new Role();
        active.setStatus(true);

        Role inactive = new Role();
        inactive.setStatus(false);

        when(roleRepository.findAll())
                .thenReturn(List.of(active, inactive));

        RoleDto dto = new RoleDto();

        when(modelMapper.map(
                        Mockito.eq(List.of(active)),
                        Mockito.any(Type.class)))
                .thenReturn(List.of(dto));

        List<RoleDto> result = roleService.findActiveStatus();

        assertEquals(1, result.size());
    }

    @Test
    void testFindActiveStatus() {
        // 1. Arrange: Create Role data
        Role active = new Role();
        active.setStatus(true);

        Role inactive = new Role();
        inactive.setStatus(false);

        // Stub the repository to return both active and inactive roles
        when(roleRepository.findAll())
                .thenReturn(List.of(active, inactive));

        // Prepare the expected DTO output list
        RoleDto dto = new RoleDto();
        List<RoleDto> expectedDtoList = List.of(dto);

        // FIX: Stub modelMapper to expect the precisely filtered list and ANY generic Type
        when(modelMapper.map(
                Mockito.eq(List.of(active)),
                Mockito.any(java.lang.reflect.Type.class)))
                .thenReturn(expectedDtoList);

        // 2. Act: Call your service layer method
        List<RoleDto> result = roleService.findActiveStatus();

        // 3. Assert: Verify the behavior
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}