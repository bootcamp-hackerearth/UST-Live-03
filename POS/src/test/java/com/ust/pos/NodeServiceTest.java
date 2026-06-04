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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

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
        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        when(modelMapper.map(dto, Node.class)).thenReturn(node);
        when(nodeRepository.save(node)).thenReturn(node);
        NodeDto response = nodeService.save(dto);
        Assertions.assertTrue(response.isSuccess());
        verify(nodeRepository).save(node);
    }

    @Test
    void save_failure_duplicate() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        when(nodeRepository.findByIdentifier("N1")).thenReturn(new Node());
        NodeDto response = nodeService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node with identifier - N1 already exists", response.getMessage());
        verify(nodeRepository, never()).save(any());
    }

    @Test
    void find_success() {
        Node node = new Node();
        NodeDto dto = new NodeDto();
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);
        NodeDto response = nodeService.findByIdentifier("N1");
        Assertions.assertNotNull(response);
    }

    @Test
    void find_null() {
        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        NodeDto response = nodeService.findByIdentifier("N1");
        Assertions.assertNull(response);
    }

    @Test
    void update_success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Node node = new Node();
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        doNothing().when(modelMapper).map(any(NodeDto.class), any(Node.class));
        when(nodeRepository.save(node)).thenReturn(node);
        NodeDto response = nodeService.update(dto);
        Assertions.assertTrue(response.isSuccess());
        verify(nodeRepository).save(node);
    }

    @Test
    void update_failure() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        NodeDto response = nodeService.update(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node with identifier - N1 not found", response.getMessage());
    }

    @Test
    void delete_test() {
        nodeService.delete("N1");
        verify(nodeRepository).deleteByIdentifier("N1");
    }

    @Test
    void find_all() {
        Node node = new Node();
        List<Node> nodes = List.of(node);
        Page<Node> page = new PageImpl<>(nodes);
        List<NodeDto> mappedList = List.of(new NodeDto());
        when(nodeRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(modelMapper.map(eq(nodes), ArgumentMatchers.<Type>any())).thenReturn(mappedList);
        List<NodeDto> result = nodeService.findAll(Pageable.unpaged());
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getNodesForRoles_empty() {
        SecurityContextHolder.clearContext();
        List<NodeDto> result = nodeService.getNodesForRoles();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void getNodesForRoles_success() {
        var springUser = new org.springframework.security.core.userdetails.
                User("john", "pass", List.of());
        SecurityContextHolder.getContext().
                setAuthentication(new UsernamePasswordAuthenticationToken(springUser, null));
        User user = new User();
        user.setRoles(List.of("ADMIN"));
        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(List.of("ADMIN"));
        when(userRepository.findByUsername("john")).thenReturn(user);
        when(nodeRepository.findAll()).thenReturn(List.of(node));
        when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        when(modelMapper.map(node, NodeDto.class)).thenReturn(new NodeDto());
        List<NodeDto> result = nodeService.getNodesForRoles();
        Assertions.assertEquals(1, result.size());
        SecurityContextHolder.clearContext();
    }
}