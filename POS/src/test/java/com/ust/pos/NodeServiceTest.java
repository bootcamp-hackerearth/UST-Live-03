package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private NodeServiceImpl nodeService;

    private Node node;
    private NodeDto nodeDto;
    private User userEntity;

    @BeforeEach
    void setUp() {
        node = new Node();
        node.setId(1L);
        node.setIdentifier("NODE-01");
        node.setStatus(true);
        node.setDeleted(false);
        node.setRoles(Collections.singletonList("ROLE_ADMIN"));

        nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-01");

        userEntity = new User();
        userEntity.setUsername("testuser");
        userEntity.setRoles(Collections.singletonList("ROLE_ADMIN"));
    }

    @Test
    void testFindByIdentifier_Success() {
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(node);

        NodeDto result = nodeService.findByIdentifier("NODE-01");

        assertNotNull(result);
        assertEquals("NODE-01", result.getIdentifier());
    }

    @Test
    void testFindByIdentifier_ThrowsResourceNotFoundException() {
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> nodeService.findByIdentifier("NODE-01"));
    }

    @Test
    void testSave_WhenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> nodeService.save(null));
    }

    @Test
    void testSave_WhenIdentifierIsNull() {
        nodeDto.setIdentifier(null);
        assertThrows(IllegalArgumentException.class, () -> nodeService.save(nodeDto));
    }

    @Test
    void testSave_WhenNodeAlreadyExistsAndNotDeleted() {
        node.setDeleted(false);
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("already exists"));
    }

    @Test
    void testSave_WhenNodeAlreadyExistsButDeleted() {
        node.setDeleted(true);
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testSave_Success() {
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(null);
        when(nodeRepository.save(any(Node.class))).thenReturn(node);

        NodeDto result = nodeService.save(nodeDto);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Node created successfully", result.getMessage());
    }

    @Test
    void testUpdate_WhenNodeNotFound() {
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(null);

        NodeDto result = nodeService.update(nodeDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("not found"));
    }

    @Test
    void testUpdate_WhenNodeDeleted() {
        node.setDeleted(true);
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(node);

        NodeDto result = nodeService.update(nodeDto);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("previously deleted"));
    }

    @Test
    void testUpdate_Success() {
        node.setDeleted(false);
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(node);
        when(nodeRepository.save(any(Node.class))).thenReturn(node);

        NodeDto result = nodeService.update(nodeDto);

        assertNotNull(result);
        assertEquals("NODE-01", result.getIdentifier());
    }

    @Test
    void testDelete() {
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(node);
        when(nodeRepository.save(any(Node.class))).thenReturn(node);

        nodeService.delete("NODE-01");

        verify(nodeRepository, times(1)).save(any(Node.class));
    }

    @Test
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Node> page = new PageImpl<>(Collections.singletonList(node), pageable, 1);
        when(nodeRepository.findByDeletedFalse(pageable)).thenReturn(page);

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
        assertFalse(result.getDtoList().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testFindAllWithSpecification() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Node> page = new PageImpl<>(Collections.singletonList(node), pageable, 1);
        Specification<Node> spec = mock(Specification.class);
        when(nodeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        WsDto<NodeDto> result = nodeService.findAll(spec, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalRecords());
    }

    @Test
    void testToggleStatus() {
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(node);
        when(nodeRepository.save(any(Node.class))).thenReturn(node);

        NodeDto result = nodeService.toggleStatus("NODE-01");

        assertNotNull(result);
        assertFalse(result.isStatus());
    }

    @Test
    void testFindIfTrue() {
        when(nodeRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(Collections.singletonList(node));

        List<NodeDto> result = nodeService.findIfTrue();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetNodesForRoles_AuthenticationNull() {
        SecurityContextHolder.clearContext();
        List<NodeDto> result = nodeService.getNodesForRoles();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetNodesForRoles_Success() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("testuser", "password", new ArrayList<>());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(userEntity);
        when(nodeRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(Collections.singletonList(node));
        when(nodeRepository.findByIdentifier("NODE-01")).thenReturn(node);

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("NODE-01", result.get(0).getIdentifier());

        SecurityContextHolder.clearContext();
    }
}