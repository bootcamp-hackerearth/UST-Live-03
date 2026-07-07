package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.modell.Node;
import com.ust.pos.modell.NodeRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class NodeServiceImplIntegrationTest {

    @Autowired
    private NodeServiceImpl nodeService;

    @Autowired
    private NodeRepository nodeRepository;

    @BeforeEach
    void setUp() {
        nodeRepository.deleteAll();
    }

    private Node createNode(
            String identifier,
            Boolean status,
            Boolean deleted) {

        Node node = new Node();
        node.setIdentifier(identifier);
        node.setStatus(status);
        node.setDeleted(deleted);

        return nodeRepository.saveAndFlush(node);
    }

    @Test
    void save_ShouldCreateNodeSuccessfully() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");

        NodeDto result = nodeService.save(dto);

        assertNotNull(result);

        Node saved =
                nodeRepository.findByIdentifier("NODE001");

        assertNotNull(saved);
        assertEquals("NODE001", saved.getIdentifier());
        assertTrue(saved.getStatus());
    }

    @Test
    void save_ShouldFail_WhenNodeAlreadyExists() {

        createNode(
                "NODE001",
                true,
                false
        );

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");

        NodeDto result = nodeService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Node with identifier - NODE001 already exists",
                result.getMessage()
        );
    }

    @Test
    void save_ShouldFail_WhenSoftDeletedNodeExists() {

        createNode(
                "NODE001",
                true,
                true
        );

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");

        NodeDto result = nodeService.save(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Node with Identifier NODE001 already exists (Soft-Deleted)",
                result.getMessage()
        );
    }

    @Test
    void update_ShouldUpdateNodeSuccessfully() {

        createNode(
                "NODE001",
                true,
                false
        );

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");

        NodeDto result = nodeService.update(dto);

        assertNotNull(result);

        Node updated =
                nodeRepository.findByIdentifierAndDeletedFalse("NODE001");

        assertNotNull(updated);
        assertEquals("NODE001", updated.getIdentifier());
    }

    @Test
    void update_ShouldReturnError_WhenNodeNotFound() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("INVALID");

        NodeDto result = nodeService.update(dto);

        assertFalse(result.isSuccess());
        assertEquals(
                "Node with identifier - INVALID not found",
                result.getMessage()
        );
    }

    @Test
    void findByIdentifier_ShouldReturnNode() {

        createNode(
                "NODE001",
                true,
                false
        );

        NodeDto result =
                nodeService.findByIdentifier("NODE001");

        assertNotNull(result);
        assertEquals("NODE001", result.getIdentifier());
    }

    @Test
    void findByIdentifier_ShouldThrowException_WhenNotFound() {

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> nodeService.findByIdentifier("INVALID")
                );

        assertEquals(
                "node with identifier 'INVALID' not found",
                exception.getMessage()
        );
    }

    @Test
    void delete_ShouldSoftDeleteNode() {

        createNode(
                "NODE001",
                true,
                false
        );

        nodeService.delete("NODE001");

        Node deleted =
                nodeRepository.findByIdentifier("NODE001");

        assertNotNull(deleted);
        assertTrue(deleted.getDeleted());
    }

    @Test
    void delete_ShouldNotThrow_WhenNodeNotFound() {

        assertDoesNotThrow(
                () -> nodeService.delete("INVALID")
        );
    }

    @Test
    void findAll_ShouldReturnAllNonDeletedNodes() {

        createNode(
                "NODE001",
                true,
                false
        );

        createNode(
                "NODE002",
                true,
                false
        );

        WsDto<NodeDto> result =
                nodeService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getDtoList().size());
    }

    @Test
    void findAll_ShouldIgnoreDeletedNodes() {

        createNode(
                "NODE001",
                true,
                false
        );

        createNode(
                "NODE002",
                true,
                true
        );

        WsDto<NodeDto> result =
                nodeService.findAll(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalRecords());
        assertEquals(1, result.getDtoList().size());
    }
}