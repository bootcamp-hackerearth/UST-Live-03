package com.ust.pos;

import com.ust.pos.dto.RoleDto;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
        dto.setIdentifier("ROLE001");
        dto.setStatus(true);

        RoleDto response = roleService.save(dto);

        Role saved = roleRepository.findByIdentifier("ROLE001");

        assertNotNull(saved);
        assertEquals("ROLE001", saved.getIdentifier());
        assertTrue(response.isSuccess());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {

        Role role = new Role();
        role.setIdentifier("ROLE001");
        role.setDeleted(false);

        roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE001");

        RoleDto response = roleService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Role with identifier - ROLE001 already exists",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateStatus() {

        Role role = new Role();
        role.setIdentifier("ROLE001");
        role.setStatus(true);
        role.setDeleted(false);

        roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ROLE001");
        dto.setStatus(false);

        RoleDto response = roleService.update(dto);

        assertTrue(response.isSuccess());

        Role updated = roleRepository.findByIdentifier("ROLE001");
        assertFalse(updated.isStatus());
    }

    @Test
    void update_shouldFailWhenRoleNotFound() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("INVALID");

        RoleDto response = roleService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Role with identifier - INVALID not found",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifier_shouldReturnRole() {

        Role role = new Role();
        role.setIdentifier("ROLE001");

        roleRepository.save(role);

        RoleDto result = roleService.findByIdentifier("ROLE001");

        assertEquals("ROLE001", result.getIdentifier());
    }

    @Test
    void delete_shouldSoftDelete() {

        Role role = new Role();
        role.setIdentifier("ROLE001");
        role.setDeleted(false);

        roleRepository.save(role);

        roleService.delete("ROLE001");

        Role deleted = roleRepository.findByIdentifier("ROLE001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void toggleStatus_shouldUpdateStatus() {

        Role role = new Role();
        role.setIdentifier("ROLE001");
        role.setStatus(false);

        roleRepository.save(role);

        RoleDto response = roleService.toggleStatus("ROLE001", true);

        assertTrue(response.isStatus());

        Role updated = roleRepository.findByIdentifier("ROLE001");

        assertTrue(updated.isStatus());
    }

    @Test
    void findActiveRole_shouldReturnOnlyActiveRoles() {

        Role activeRole = new Role();
        activeRole.setIdentifier("ROLE001");
        activeRole.setStatus(true);

        Role inactiveRole = new Role();
        inactiveRole.setIdentifier("ROLE002");
        inactiveRole.setStatus(false);

        roleRepository.save(activeRole);
        roleRepository.save(inactiveRole);

        List<RoleDto> activeRoles = roleService.findActiveRole();

        assertEquals(1, activeRoles.size());
        assertEquals("ROLE001", activeRoles.get(0).getIdentifier());
    }
}