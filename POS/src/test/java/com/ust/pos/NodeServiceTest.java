package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class NodeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Test
    void getNodesForRolesTest() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User("adminUser", "password", Collections.emptyList());

        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        User currentUser = new User();
        currentUser.setUsername("adminUser");
        currentUser.setRoles(List.of("ROLE_ADMIN"));

        Node node = new Node();
        node.setIdentifier("NODE-001");
        node.setRoles(List.of("ROLE_ADMIN"));

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE-001");

        Mockito.when(userRepository.findByUsername("adminUser")).thenReturn(currentUser);
        Mockito.when(nodeRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(List.of(node));
        Mockito.when(nodeRepository.findByIdentifier("NODE-001")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("NODE-001", response.get(0).getIdentifier());
        SecurityContextHolder.clearContext();
    }

    @Test
    void findByIdentifierTest() {
        Node node = new Node();
        node.setIdentifier("Admin");
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto response = nodeService.findByIdentifier("Admin");

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void saveTest() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(null);
        Node node = new Node();
        Mockito.when(modelMapper.map(nodeDto, Node.class)).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
    }

    @Test
    void saveTestFailure() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Node existingNode = new Node();
        existingNode.setIdentifier("Admin");
        existingNode.setDeleted(false);

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(existingNode);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void saveTestFailurePreviouslyDeleted() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Node existingNode = new Node();
        existingNode.setIdentifier("Admin");
        existingNode.setDeleted(true);

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(existingNode);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void updateTest() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Node existingNode = new Node();
        existingNode.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(existingNode);
        Mockito.when(nodeRepository.save(existingNode)).thenReturn(existingNode);

        NodeDto response = nodeService.update(nodeDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(null);

        NodeDto response = nodeService.update(nodeDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        Node node = new Node();
        node.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);

        boolean response = nodeService.delete("Admin");

        Assertions.assertEquals(true, response);
    }

    @Test
    void deleteTestFailure() {
        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(null);

        boolean response = nodeService.delete("Admin");

        Assertions.assertEquals(false, response);
    }

    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 50);
        Node node = new Node();
        List<Node> nodes = List.of(node);
        Page<Node> nodePage = new PageImpl<>(nodes, pageable, nodes.size());

        NodeDto nodeDto = new NodeDto();
        List<NodeDto> nodeDtos = List.of(nodeDto);

        Mockito.when(nodeRepository.findByDeletedFalse(pageable)).thenReturn(nodePage);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(java.lang.reflect.Type.class))).thenReturn(nodeDtos);

        WsDto<NodeDto> response = nodeService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
    }

    @Test
    void findByStatusTest() {
        Node node = new Node();
        List<Node> nodes = List.of(node);
        NodeDto nodeDto = new NodeDto();
        List<NodeDto> nodeDtos = List.of(nodeDto);

        Mockito.when(nodeRepository.findByStatusIsTrueAndDeletedFalse()).thenReturn(nodes);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(java.lang.reflect.Type.class))).thenReturn(nodeDtos);

        List<NodeDto> response = nodeService.findIfTrue();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void toggleTestActive() {
        Node node = new Node();
        node.setStatus(false);
        NodeDto nodeDto = new NodeDto();
        nodeDto.setStatus(true);

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto response = nodeService.toggleStatus("Admin");

        Assertions.assertTrue(response.isStatus());
    }

    @Test
    void toggleTestInactive() {
        Node node = new Node();
        node.setStatus(true);
        NodeDto nodeDto = new NodeDto();
        nodeDto.setStatus(false);

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto response = nodeService.toggleStatus("Admin");

        Assertions.assertFalse(response.isStatus());
    }
}