package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.node.service.NodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class NodeServiceImplIT {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private NodeRepository nodeRepository;

    @BeforeEach
    void cleanUp() {
        nodeRepository.deleteAll();
    }

    @Test
    void save_shouldCreateNode() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        dto.setStatus(true);
        NodeDto response = nodeService.save(dto);
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
                response.getMessage());
    }

    @Test
    void update_shouldUpdatePath() {
        Node node = new Node();
        node.setIdentifier("NODE001");
        node.setDeleted(false);
        node.setPath("/home");
        nodeRepository.save(node);
        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE001");
        dto.setPath("/dashboard");
        NodeDto response = nodeService.update(dto);
        assertTrue(response.isSuccess());
        Node updated = nodeRepository.findByIdentifier("NODE001");
        assertEquals("/dashboard", updated.getPath());
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
    void delete_shouldSoftDelete() {
        Node node = new Node();
        node.setIdentifier("NODE001");
        node.setDeleted(false);
        nodeRepository.save(node);
        nodeService.delete("NODE001");
        Node deleted = nodeRepository.findByIdentifier("NODE001");
        assertTrue(deleted.isDeleted());
    }
}