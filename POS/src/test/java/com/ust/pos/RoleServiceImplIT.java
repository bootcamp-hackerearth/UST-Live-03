package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RoleServiceImplIT {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void cleanUp() {
        roleRepository.deleteAll();
    }

    @Test
    void save_shouldCreateRole() {
        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE_USER");
        dto.setStatus(true);

        Role saved = roleRepository.findByIdentifier("ROLE_USER");

        assertNotNull(saved);
        assertEquals("ROLE_USER", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Role role = new Role();
        role.setIdentifier("ROLE_USER");
        role.setDeleted(false);
        roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE_USER");

        RoleDto response = roleService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Role with identifier - ROLE_USER already exists",
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        Role role = new Role();
        role.setIdentifier("ROLE_USER");
        role.setDeleted(true);
        roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE_USER");

        RoleDto response = roleService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Node with identifier ROLE_USER was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateRoleDetails() {
        Role role = new Role();
        role.setIdentifier("ROLE_USER");
        role.setStatus(true);
        role.setDeleted(false);
        roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE_USER");
        dto.setStatus(false);

        RoleDto response = roleService.update(dto);

        assertTrue(response.isSuccess());

        Role updated = roleRepository.findByIdentifier("ROLE_USER");

        assertFalse(updated.isStatus());
    }

    @Test
    void findByIdentifier_shouldReturnRole() {
        Role role = new Role();
        role.setIdentifier("ROLE_USER");
        roleRepository.save(role);

        RoleDto result = roleService.findByIdentifier("ROLE_USER");

        assertEquals("ROLE_USER", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            roleService.findByIdentifier("NON-EXISTENT");
        });
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        Role role = new Role();
        role.setIdentifier("ROLE_USER");
        role.setStatus(true);
        roleRepository.save(role);

        roleService.toggleStatus("ROLE_USER");

        Role updated = roleRepository.findByIdentifier("ROLE_USER");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {
        Role role = new Role();
        role.setIdentifier("ROLE_USER");
        role.setDeleted(false);
        roleRepository.save(role);

        boolean isDeleted = roleService.delete("ROLE_USER");

        assertTrue(isDeleted);

        Role deleted = roleRepository.findByIdentifier("ROLE_USER");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnPaginatedData() {
        Role role1 = new Role();
        role1.setIdentifier("ROLE_ADMIN");
        role1.setDeleted(false);
        roleRepository.save(role1);

        Role role2 = new Role();
        role2.setIdentifier("ROLE_USER");
        role2.setDeleted(false);
        roleRepository.save(role2);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<RoleDto> response = roleService.findAll(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalRecords());
        assertEquals(2, response.getDtoList().size());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {
        Role activeRole = new Role();
        activeRole.setIdentifier("ROLE_ADMIN");
        activeRole.setStatus(true);
        activeRole.setDeleted(false);
        roleRepository.save(activeRole);

        Role inactiveRole = new Role();
        inactiveRole.setIdentifier("ROLE_USER");
        inactiveRole.setStatus(false);
        inactiveRole.setDeleted(false);
        roleRepository.save(inactiveRole);

        List<RoleDto> activeList = roleService.findIfTrue();

        assertEquals(1, activeList.size());
        assertEquals("ROLE_ADMIN", activeList.get(0).getIdentifier());
    }
}