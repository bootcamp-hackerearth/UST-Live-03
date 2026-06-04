package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.NodeRepository;
import com.ust.pos.model.User;
import com.ust.pos.model.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.AfterEach;
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

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private ModelMapper modelMapper;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getNodesForRolesTest() {

        org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User("john", "pwd", List.of());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = new User();
        user.setUsername("john");
        user.setRoles(List.of("ADMIN"));

        Node node = new Node();
        node.setIdentifier("NODE_1");
        node.setRoles(List.of("ADMIN"));

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE_1");

        Mockito.when(userRepository.findByUsername("john")).thenReturn(user);

        Mockito.when(nodeRepository.findAll()).thenReturn(List.of(node));

        Mockito.when(nodeRepository.findByIdentifier("NODE_1")).thenReturn(node);

        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, response.size());

        Assertions.assertEquals("NODE_1", response.get(0).getIdentifier());
    }

    @Test
    void findByIdentifierTest() {

        Node node = new Node();
        node.setIdentifier("NODE_1");

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE_1");

        Mockito.when(nodeRepository.findByIdentifier("NODE_1")).thenReturn(node);

        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto response = nodeService.findByIdentifier("NODE_1");

        Assertions.assertEquals("NODE_1", response.getIdentifier());
    }

    @Test
    void saveTest() {

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE_1");

        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("NODE_1")).thenReturn(null);

        Mockito.when(modelMapper.map(nodeDto, Node.class)).thenReturn(node);

        NodeDto response = nodeService.save(nodeDto);

        Mockito.verify(nodeRepository).save(node);

        Assertions.assertTrue(response.isSuccess());

        Assertions.assertEquals("Node created successfully", response.getMessage());
    }

    @Test
    void saveTestFailure() {

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE_1");

        Node existingNode = new Node();

        Mockito.when(nodeRepository.findByIdentifier("NODE_1")).thenReturn(existingNode);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Node with identifier 'NODE_1' already exists", response.getMessage());
    }

    @Test
    void updateTest() {

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE_1");

        Node existingNode = new Node();
        existingNode.setIdentifier("NODE_1");

        Mockito.when(nodeRepository.findByIdentifier("NODE_1")).thenReturn(existingNode);

        Mockito.when(nodeRepository.save(existingNode)).thenReturn(existingNode);

        Mockito.doNothing().when(modelMapper).map(nodeDto, existingNode);

        NodeDto response = nodeService.update(nodeDto);

        Assertions.assertEquals("NODE_1", response.getIdentifier());

        Mockito.verify(nodeRepository).save(existingNode);
    }

    @Test
    void updateTestFailure() {

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE_1");

        Mockito.when(nodeRepository.findByIdentifier("NODE_1")).thenReturn(null);

        NodeDto response = nodeService.update(nodeDto);

        Assertions.assertFalse(response.isSuccess());

        Assertions.assertEquals("Node with identifier - NODE_1 not found", response.getMessage());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(nodeRepository).deleteByIdentifier("NODE_1");

        boolean result = nodeService.delete("NODE_1");

        Assertions.assertTrue(result);

        Mockito.verify(nodeRepository).deleteByIdentifier("NODE_1");
    }

    @Test
    void findAllTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Node node = new Node();
        node.setIdentifier("NODE_1");

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE_1");

        List<Node> nodes = List.of(node);
        List<NodeDto> nodeDtos = List.of(nodeDto);

        Page<Node> nodePage = new PageImpl<>(nodes);

        Mockito.when(nodeRepository.findAll(pageable)).thenReturn(nodePage);

        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(Type.class))).thenReturn(nodeDtos);

        List<NodeDto> response = nodeService.findAll(pageable);

        Assertions.assertEquals(1, response.size());

        Mockito.verify(nodeRepository).findAll(pageable);
    }
}