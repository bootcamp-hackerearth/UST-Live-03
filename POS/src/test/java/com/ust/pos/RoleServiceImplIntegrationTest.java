package com.ust.pos;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.RoleDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Role;
import com.ust.pos.model.RoleRepository;
import com.ust.pos.role.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class RoleServiceImplIntegrationTest {

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();

        Role role = new Role();
        role.setIdentifier("ADMIN");
        role.setDescription("Administrator");
        role.setStatus(true);
        role.setIsDeleted(false);

        roleRepository.save(role);
    }

    @Test
    void testSaveSuccess() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("USER");
        dto.setDescription("Normal User");
        dto.setStatus(true);

        RoleDto response = roleService.save(dto);

        assertTrue(response.isSuccess());

        Role saved = roleRepository.findByIdentifier("USER");

        assertNotNull(saved);
        assertEquals("USER", saved.getIdentifier());
        assertEquals("Normal User", saved.getDescription());
        assertTrue(saved.getStatus());
        assertFalse(saved.getIsDeleted());
    }

    @Test
    void testSaveAlreadyExists() {

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
    void testSaveDeletedRole() {

        Role role = roleRepository.findByIdentifier("ADMIN");
        role.setIsDeleted(true);
        roleRepository.save(role);

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");

        RoleDto response = roleService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Role with identifier - ADMIN was deleted. Contact admin for further support or try with a different identifier.",
                response.getMessage()
        );
    }

    @Test
    void testUpdateSuccess() {

        RoleDto dto = new RoleDto();
        dto.setIdentifier("ADMIN");
        dto.setDescription("Updated Administrator");
        dto.setStatus(false);

        RoleDto response = roleService.update(dto);

        assertTrue(response.isSuccess());

        Role updated = roleRepository.findByIdentifier("ADMIN");

        assertEquals("Updated Administrator", updated.getDescription());
        assertFalse(updated.getStatus());
    }

    @Test
    void testUpdateNotFound() {

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
    void testDeleteSuccess() {

        RoleDto response = roleService.delete("ADMIN");

        assertTrue(response.isSuccess());
        assertEquals("Role deleted successfully", response.getMessage());

        Role deleted = roleRepository.findByIdentifier("ADMIN");

        assertTrue(deleted.getIsDeleted());
        assertFalse(deleted.getStatus());
    }

    @Test
    void testDeleteNotFound() {

        RoleDto response = roleService.delete("UNKNOWN");

        assertFalse(response.isSuccess());
        assertEquals(
                "Role with identifier - UNKNOWN not found",
                response.getMessage()
        );
    }

    @Test
    void testFindByIdentifierSuccess() {

        RoleDto dto = roleService.findByIdentifier("ADMIN");

        assertNotNull(dto);
        assertEquals("ADMIN", dto.getIdentifier());
        assertEquals("Administrator", dto.getDescription());
        assertTrue(dto.getStatus());
    }

    @Test
    void testFindByIdentifierNotFound() {

        assertThrows(
                ResourceNotFoundException.class,
                () -> roleService.findByIdentifier("UNKNOWN")
        );
    }

    @Test
    void testFindAll() {

        Pageable pageable = PageRequest.of(0, 10);

        PaginatedResponseDto<RoleDto> response =
                roleService.findAll(pageable);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(1, response.getTotalRecords());
        assertEquals(1, response.getTotalPages());
        assertEquals(10, response.getSizePerPage());
        assertEquals(0, response.getPage());
    }

    @Test
    void testFindAllActive() {

        List<RoleDto> roles = roleService.findAllActive();

        assertEquals(1, roles.size());

        RoleDto dto = roles.get(0);

        assertEquals("ADMIN", dto.getIdentifier());
        assertTrue(dto.getStatus());
        assertFalse(dto.getIsDeleted());
    }

    @Test
    void testFindAllActiveNoResults() {

        Role role = roleRepository.findByIdentifier("ADMIN");
        role.setStatus(false);
        roleRepository.save(role);

        List<RoleDto> roles = roleService.findAllActive();

        assertTrue(roles.isEmpty());
    }

    @Test
    void testChangeStatusToFalse() {

        roleService.changeStatus("ADMIN", false);

        Role role = roleRepository.findByIdentifier("ADMIN");

        assertFalse(role.getStatus());
    }

    @Test
    void testChangeStatusToTrue() {

        Role role = roleRepository.findByIdentifier("ADMIN");
        role.setStatus(false);
        roleRepository.save(role);

        roleService.changeStatus("ADMIN", true);

        Role updated = roleRepository.findByIdentifier("ADMIN");

        assertTrue(updated.getStatus());
    }

    @Test
    void testFindAllWithSpecification() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Role> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "ADMIN");

        PaginatedResponseDto<RoleDto> response =
                roleService.findAll(specification, pageable);

        assertEquals(1, response.getItems().size());
        assertEquals(
                "ADMIN",
                response.getItems().get(0).getIdentifier()
        );
    }

    @Test
    void testFindAllWithSpecificationNoResults() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Role> specification =
                (root, query, cb) ->
                        cb.equal(root.get("identifier"), "XYZ");

        PaginatedResponseDto<RoleDto> response =
                roleService.findAll(specification, pageable);

        assertEquals(0, response.getItems().size());
        assertEquals(0, response.getTotalRecords());
        assertEquals(0, response.getTotalPages());
    }
}