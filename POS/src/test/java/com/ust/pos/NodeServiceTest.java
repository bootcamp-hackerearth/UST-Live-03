package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.exception.ResourceNotFoundException;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @InjectMocks
    @Spy
    private NodeServiceImpl nodeService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private ModelMapper modelMapper;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getNodesForRolesTest() {

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("admin", "password", List.of());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setUsername("admin");
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("NODE1");
        node.setRoles(List.of("ADMIN"));
        node.setDeleted(false);

        NodeDto dto = new NodeDto();

        when(userRepository.findByUsername("admin")).thenReturn(user);

        when(nodeRepository.findAll()).thenReturn(List.of(node));

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);

        when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertEquals(1, result.size());

        verify(nodeRepository).findAll();

    }

    @Test
    void getNodesForRolesAuthenticationNullTest() {

        SecurityContextHolder.clearContext();

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());

    }

    @Test
    void getNodesForRolesUserNullTest() {

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("admin", "password", List.of());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findByUsername("admin")).thenReturn(null);

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());

    }

    @Test
    void getNodesForRolesDeletedNodeTest() {

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("admin", "password", List.of());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setRoles(List.of("ADMIN"));
        node.setDeleted(true);

        when(userRepository.findByUsername(any())).thenReturn(user);

        when(nodeRepository.findAll()).thenReturn(List.of(node));

        List<NodeDto> result = nodeService.getNodesForRoles();

        assertTrue(result.isEmpty());

    }

    @Test
    void findByIdentifierTest() {

        Node node = new Node();
        node.setIdentifier("NODE1");

        NodeDto dto = new NodeDto();

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);

        when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        NodeDto result = nodeService.findByIdentifier("NODE1");

        assertNotNull(result);

    }

    @Test
    void findByIdentifierNotFoundTest() {

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> nodeService.findByIdentifier("NODE1"));

    }

    @Test
    void saveTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Node node = new Node();

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);

        when(modelMapper.map(dto, Node.class)).thenReturn(node);

        NodeDto result = nodeService.save(dto);

        assertTrue(result.isSuccess());

        assertEquals("Node created successfully", result.getMessage());

        verify(nodeRepository).save(node);

    }

    @Test
    void saveAlreadyExistsTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Node existing = new Node();

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(existing);

        NodeDto result = nodeService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Node with identifier 'NODE1' already exists", result.getMessage());

    }

    @Test
    void saveSoftDeletedTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        Node existing = new Node();
        existing.setDeleted(true);

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(existing);

        NodeDto result = nodeService.save(dto);

        assertFalse(result.isSuccess());

        assertEquals("Node with identifier 'NODE1' has been soft deleted. Rollback by changing status.", result.getMessage());

    }

    @Test
    void updateTest() {

        Node node = new Node();

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);

        doNothing().when(modelMapper).map(dto, node);

        NodeDto result = nodeService.update(dto);

        assertTrue(result.isSuccess());

        assertEquals("Node updated successfully", result.getMessage());

        verify(nodeRepository).save(node);

    }

    @Test
    void updateNotFoundTest() {

        NodeDto dto = new NodeDto();
        dto.setIdentifier("NODE1");

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> nodeService.update(dto));

    }

    @Test
    void deleteTest() {

        Node node = new Node();

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);

        boolean result = nodeService.delete("NODE1");

        assertTrue(result);

        assertTrue(node.isDeleted());

        verify(nodeRepository).save(node);

    }

    @Test
    void deleteNotFoundTest() {

        when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> nodeService.delete("NODE1"));

    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Node node = new Node();

        NodeDto dto = new NodeDto();

        Page<Node> page = new PageImpl<>(List.of(node));

        when(nodeRepository.findByDeletedFalse(pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(dto));

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        assertEquals(1, result.getDtoList().size());

    }

    @Test
    void findAllSpecificationTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Node> spec = (root, query, cb) -> null;

        Node node = new Node();

        NodeDto dto = new NodeDto();

        Page<Node> page = new PageImpl<>(List.of(node));

        when(nodeRepository.findAll(spec, pageable)).thenReturn(page);

        when(modelMapper.map(eq(page.getContent()), any(Type.class))).thenReturn(List.of(dto));

        WsDto<NodeDto> result = nodeService.findAll(spec, pageable, "abc");

        assertEquals("abc", result.getKeyword());

    }

}