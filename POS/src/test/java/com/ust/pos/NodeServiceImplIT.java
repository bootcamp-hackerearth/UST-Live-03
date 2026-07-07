package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.*;
import com.ust.pos.node.service.NodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class NodeServiceImplIT {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        nodeRepository.deleteAll();
        userRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    @Test
    void getNodesForRoles_shouldReturnNodesMatchingUserRoles() {
        User user = new User();
        user.setUsername("testuser");
        java.util.List<String> roles = new java.util.ArrayList<>();
        roles.add("ROLE_ADMIN");
        org.springframework.test.util.ReflectionTestUtils.setField(user, "roles", roles);
        userRepository.save(user);

        Node node = new Node();
        node.setIdentifier("NODE001");
        node.setStatus(true);
        node.setDeleted(false);
        java.util.List<String> nodeRoles = new java.util.ArrayList<>();
        nodeRoles.add("ROLE_ADMIN");
        org.springframework.test.util.ReflectionTestUtils.setField(node, "roles", nodeRoles);
        nodeRepository.save(node);

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "testuser",
                        "password",
                        true, true, true, true,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("NODE001", result.get(0).getIdentifier());
    }

    @Test
    void save_shouldCreateNode() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        dto.setStatus(true);

        Node saved = nodeRepository.findByIdentifier("NODE001");

        assertNotNull(saved);
        assertEquals("NODE001", saved.getIdentifier());
    }

    @Test
    void save_shouldFailWhenDuplicateExists() {
        Node node = new Node();
        node.setIdentifier("NODE001");
        node.setDeleted(false);

        nodeRepository.save(node);

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");

        NodeDto response = nodeService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Node with identifier - NODE001 already exists",
                response.getMessage()
        );
    }

    @Test
    void save_shouldFailWhenPreviouslyDeleted() {
        Node node = new Node();
        node.setIdentifier("NODE001");
        node.setDeleted(true);

        nodeRepository.save(node);

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");

        NodeDto response = nodeService.save(dto);

        assertFalse(response.isSuccess());
        assertEquals(
                "Node with identifier NODE001 was previously deleted. Please contact backend team to restore.",
                response.getMessage()
        );
    }

    @Test
    void update_shouldUpdateNodeDetails() {
        Node node = new Node();
        node.setIdentifier("NODE001");
        node.setStatus(true);
        node.setDeleted(false);

        nodeRepository.save(node);

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        dto.setStatus(false);

        NodeDto response = nodeService.update(dto);

        assertTrue(response.isSuccess());

        Node updated = nodeRepository.findByIdentifier("NODE001");

        assertFalse(updated.isStatus());
    }

    @Test
    void findByIdentifier_shouldReturnNode() {
        Node node = new Node();
        node.setIdentifier("NODE001");

        nodeRepository.save(node);

        NodeDto result = nodeService.findByIdentifier("NODE001");

        assertEquals("NODE001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_shouldThrowExceptionWhenNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            nodeService.findByIdentifier("NON-EXISTENT");
        });
    }

    @Test
    void toggleStatus_shouldToggleValue() {
        Node node = new Node();
        node.setIdentifier("NODE001");
        node.setStatus(true);

        nodeRepository.save(node);

        nodeService.toggleStatus("NODE001");

        Node updated = nodeRepository.findByIdentifier("NODE001");

        assertFalse(updated.isStatus());
    }

    @Test
    void delete_shouldSoftDelete() {
        Node node = new Node();
        node.setIdentifier("NODE001");
        node.setDeleted(false);

        nodeRepository.save(node);

        boolean isDeleted = nodeService.delete("NODE001");

        assertTrue(isDeleted);

        Node deleted = nodeRepository.findByIdentifier("NODE001");

        assertTrue(deleted.isDeleted());
    }

    @Test
    void findAll_shouldReturnPaginatedData() {
        Node node1 = new Node();
        node1.setIdentifier("NODE001");
        node1.setDeleted(false);
        nodeRepository.save(node1);

        Node node2 = new Node();
        node2.setIdentifier("NODE002");
        node2.setDeleted(false);
        nodeRepository.save(node2);

        Pageable pageable = PageRequest.of(0, 10);

        WsDto<NodeDto> response = nodeService.findAll(pageable);

        assertNotNull(response);
        assertEquals(2, response.getTotalRecords());
        assertEquals(2, response.getDtoList().size());
    }

    @Test
    void findIfTrue_shouldReturnActiveAndNonDeletedRecords() {
        Node activeNode = new Node();
        activeNode.setIdentifier("NODE001");
        activeNode.setStatus(true);
        activeNode.setDeleted(false);
        nodeRepository.save(activeNode);

        Node inactiveNode = new Node();
        inactiveNode.setIdentifier("NODE002");
        inactiveNode.setStatus(false);
        inactiveNode.setDeleted(false);
        nodeRepository.save(inactiveNode);

        List<NodeDto> activeList = nodeService.findIfTrue();

        assertEquals(1, activeList.size());
        assertEquals("NODE001", activeList.get(0).getIdentifier());
    }
}