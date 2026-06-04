package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private NodeServiceImpl nodeService;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Test
    void save_success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        Mockito.when(modelMapper.map(dto, Node.class)).thenReturn(node);

        NodeDto response = nodeService.save(dto);

        verify(nodeRepository).save(node);
        assertTrue(response.isSuccess());
    }

    @Test
    void save_failure_existingNode() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(new Node());

        NodeDto response = nodeService.save(dto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void update_success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        Node existingNode = new Node();

        when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(existingNode);

        when(nodeRepository.save(existingNode))
                .thenReturn(existingNode);

        NodeDto response = nodeService.update(dto);

        verify(modelMapper).map(dto, existingNode);
        verify(nodeRepository).save(existingNode);

        assertTrue(response.isSuccess());
        assertEquals("Node updated successfully", response.getMessage());
    }

    @Test
    void update_nodeNotFound() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");

        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        NodeDto response = nodeService.update(dto);

        assertFalse(response.isSuccess());
        assertEquals("Node not found", response.getMessage());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(nodeRepository).deleteByIdentifier("N1");

        nodeService.delete("N1");

        verify(nodeRepository).deleteByIdentifier("N1");
    }

    @Test
    void findByIdentifierTest() {
        Node node = new Node();
        NodeDto dto = new NodeDto();

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        NodeDto response = nodeService.findByIdentifier("N1");

        Assertions.assertNotNull(response);
    }

    @Test
    void findAllWithPageableTest() {

        Node node = new Node();
        node.setIdentifier("CUST1");

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("CUST1");

        List<Node> nodes = List.of(node);
        List<NodeDto> nodeDtos = List.of(nodeDto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Node> nodePage = new PageImpl<>(nodes);

        when(nodeRepository.findAll(pageable))
                .thenReturn(nodePage);

        when(modelMapper.map(
                eq(nodes),
                any(Type.class)
        )).thenReturn(nodeDtos);

        List<NodeDto> result = nodeService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CUST1", result.get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {

        Node node = new Node();
        node.setIdentifier("CUST1");

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("CUST1");

        List<Node> nodes = List.of(node);
        List<NodeDto> nodeDtos = List.of(nodeDto);

        when(nodeRepository.findAll())
                .thenReturn(nodes);

        when(modelMapper.map(
                eq(nodes),
                any(Type.class)
        )).thenReturn(nodeDtos);

        List<NodeDto> result = nodeService.findAll(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CUST1", result.get(0).getIdentifier());
    }

    @Test
    void findAllWithPageable_emptyResult() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Node> emptyPage = Page.empty();

        when(nodeRepository.findAll(pageable)).thenReturn(emptyPage);
        when(modelMapper.map(eq(List.of()), any(Type.class)))
                .thenReturn(List.of());

        List<NodeDto> result = nodeService.findAll(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateStatus_success() {
        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);

        NodeDto response = nodeService.updateStatus("N1", true);

        assertTrue(response.isSuccess());
        Assertions.assertEquals("Status updated successfully", response.getMessage());
    }

    @Test
    void updateStatus_failure() {
        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(null);

        NodeDto response = nodeService.updateStatus("N1", true);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node not found", response.getMessage());
    }

    @Test
    void getNodesForRoles_emptyAuth() {
        SecurityContextHolder.clearContext();

        List<NodeDto> response = nodeService.getNodesForRoles();

        assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRoles_noUser() {
        var auth = new UsernamePasswordAuthenticationToken("test", "pass");
        SecurityContextHolder.getContext().setAuthentication(auth);

        List<NodeDto> response = nodeService.getNodesForRoles();

        assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRoles_success() {
        var springUser = new org.springframework.security.core.userdetails.User(
                "john", "pass", List.of()
        );

        var auth = new UsernamePasswordAuthenticationToken(springUser, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ADMIN"));

        NodeDto dto = new NodeDto();

        Mockito.when(userRepository.findByUsername("john")).thenReturn(user);
        Mockito.when(nodeRepository.findAll()).thenReturn(List.of(node));
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void getNodesForRoles_userRolesNull() {
        var springUser = new org.springframework.security.core.userdetails.User(
                "john", "pass", List.of()
        );

        var auth = new UsernamePasswordAuthenticationToken(springUser, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setRoles(null);

        Mockito.when(userRepository.findByUsername("john")).thenReturn(user);

        List<NodeDto> response = nodeService.getNodesForRoles();

        assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRoles_nodeRolesNull() {
        var springUser = new org.springframework.security.core.userdetails.User(
                "john", "pass", List.of()
        );

        var auth = new UsernamePasswordAuthenticationToken(springUser, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(null);

        Mockito.when(userRepository.findByUsername("john")).thenReturn(user);
        Mockito.when(nodeRepository.findAll()).thenReturn(List.of(node));

        List<NodeDto> response = nodeService.getNodesForRoles();

        assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRoles_nodeMissingOnLookup() {
        var springUser = new org.springframework.security.core.userdetails.User(
                "john", "pass", List.of()
        );

        var auth = new UsernamePasswordAuthenticationToken(springUser, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ADMIN"));

        Mockito.when(userRepository.findByUsername("john")).thenReturn(user);
        Mockito.when(nodeRepository.findAll()).thenReturn(List.of(node));
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(null);

        List<NodeDto> response = nodeService.getNodesForRoles();

        assertTrue(response.isEmpty());
    }
}