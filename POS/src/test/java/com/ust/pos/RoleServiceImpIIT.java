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
import org.springframework.data.domain.PageRequest;
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
    void setUp() {
        roleRepository.deleteAll();
    }

    @Test
    void save_ShouldCreateRole() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        RoleDto response = roleService.save(dto);

        assertTrue(response.isSuccess());
        assertEquals("Role created successfully", response.getMessage());

        Role saved = roleRepository.findByIdentifier("ADMIN");
        assertNotNull(saved);
    }

    @Test
    void save_ShouldThrowException_WhenIdentifierIsNull() {

        RoleDto dto = new RoleDto();

        assertThrows(
                IllegalArgumentException.class,
                () -> roleService.save(dto)
        );
    }

    @Test
    void save_ShouldFail_WhenRoleAlreadyExists() {

        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setDeleted(false);

        roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        RoleDto response = roleService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Role with identifier - ADMIN already exists",
                response.getMessage()
        );
    }

    @Test
    void save_ShouldFail_WhenDeletedRoleExists() {

        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setDeleted(true);

        roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        RoleDto response = roleService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Role with identifier - ADMIN was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void findByIdentifier_ShouldReturnRole() {

        Role role = new Role();
        role.setIdentifier("ADMIN");

        roleRepository.save(role);

        RoleDto response =
                roleService.findByIdentifier("ADMIN");

        assertEquals("ADMIN", response.getIdentifier());
    }

    @Test
    void findByIdentifier_ShouldThrowException_WhenNotFound() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.findByIdentifier("UNKNOWN")
        );
    }

    @Test
    void update_ShouldUpdateRole() {

        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setStatus(true);
        role.setDeleted(false);

        roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        dto.setStatus(false);

        RoleDto response = roleService.update(dto);

        assertTrue(response.isSuccess());
        assertEquals("Role updated successfully", response.getMessage());

        Role updated =
                roleRepository.findByIdentifier("ADMIN");

        assertFalse(updated.getStatus());
    }

    @Test
    void update_ShouldFail_WhenRoleNotFound() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("UNKNOWN");

        RoleDto response = roleService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Role with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void update_ShouldFail_WhenRoleDeleted() {

        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setDeleted(true);

        roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        RoleDto response = roleService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Role with identifier - ADMIN was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void delete_ShouldSoftDeleteRole() {

        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setDeleted(false);

        roleRepository.save(role);

        roleService.delete("ADMIN");

        Role deleted =
                roleRepository.findByIdentifier("ADMIN");

        assertTrue(deleted.getDeleted());
    }

    @Test
    void toggleStatus_ShouldToggleRoleStatus() {

        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setStatus(true);

        roleRepository.save(role);

        RoleDto response =
                roleService.toggleStatus("ADMIN");

        assertFalse(response.isStatus());

        Role updated =
                roleRepository.findByIdentifier("ADMIN");

        assertFalse(updated.getStatus());
    }

    @Test
    void toggleStatus_ShouldReturnFailure_WhenRoleNotFound() {

        RoleDto response =
                roleService.toggleStatus("UNKNOWN");

        assertFalse(response.isSuccess());
        assertEquals(
                "Role with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void findIfTrue_ShouldReturnOnlyActiveRoles() {

        Role active = new Role();
        active.setIdentifier("ADMIN");
        active.setStatus(true);
        active.setDeleted(false);

        Role inactive = new Role();
        inactive.setIdentifier("USER");
        inactive.setStatus(false);
        inactive.setDeleted(false);

        roleRepository.save(active);
        roleRepository.save(inactive);

        List<RoleDto> result =
                roleService.findIfTrue();

        assertEquals(1, result.size());
        assertEquals(
                "ADMIN",
                result.get(0).getIdentifier()
        );
    }

    @Test
    void findAll_ShouldReturnPagedRoles() {

        Role role1 = new Role();
        role1.setIdentifier("ADMIN");
        role1.setDeleted(false);

        Role role2 = new Role();
        role2.setIdentifier("USER");
        role2.setDeleted(false);

        roleRepository.save(role1);
        roleRepository.save(role2);

        WsDto<RoleDto> result =
                roleService.findAll(PageRequest.of(0, 10));

        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }
}