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
        dto.setIdentifier("ADMIN");

        RoleDto response = roleService.save(dto);

        Role saved = roleRepository.findByIdentifier("ADMIN");

        assertNotNull(saved);
        assertEquals("ADMIN", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {

        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setDeleted(false);
        roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        RoleDto response = roleService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Role already exists",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateRole() {

        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setDeleted(false);
        roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        RoleDto response = roleService.update(dto);

        assertTrue(response.isSuccess());

        Role updated = roleRepository.findByIdentifier("ADMIN");

        assertEquals(
                "ADMIN",
                updated.getIdentifier()
        );
    }

    @Test
    void findByIdentifier_shouldReturnRole() {

        Role role = new Role();
        role.setIdentifier("ADMIN");
        roleRepository.save(role);

        RoleDto result = roleService.findByIdentifier("ADMIN");
        assertEquals("ADMIN", result.getIdentifier());
    }

    @Test
    void deleteByIdentifier_shouldSoftDelete() {

        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setDeleted(false);
        roleRepository.save(role);

        roleService.delete("ADMIN");

        Role deleted = roleRepository.findByIdentifier("ADMIN");

        assertTrue(deleted.getDeleted());
    }
}