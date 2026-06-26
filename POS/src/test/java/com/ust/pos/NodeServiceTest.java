package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.*;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Type;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;

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
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getNodesForRolesSuccessTest() {

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        User principal = new User("admin", "password", Collections.emptyList());

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(securityContext);

        com.ust.pos.model.User currentUser = new com.ust.pos.model.User();
        currentUser.setRoles(List.of("ROLE_ADMIN"));

        Node activeNode = new Node();
        activeNode.setIdentifier("NODE1");
        activeNode.setRoles(List.of("ROLE_ADMIN"));

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");

        Mockito.when(userRepository.findByUsername("admin")).thenReturn(currentUser);
        Mockito.when(nodeRepository.findByStatusTrueAndIsDeletedFalse()).thenReturn(List.of(activeNode));
        Mockito.when(nodeRepository.findByIdentifier("NODE1")).thenReturn(activeNode);
        Mockito.when(modelMapper.map(activeNode, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("NODE1", result.get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesNoAuthTest() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findByIdentifierSuccessTest() {
        Node node = new Node();
        node.setIdentifier("NODE1");

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");

        Mockito.when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto response = nodeService.findByIdentifier("NODE1");

        Assertions.assertNotNull(response);
        Assertions.assertEquals("NODE1", response.getIdentifier());
    }

    @Test
    void saveSuccessTest() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");

        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);
        Mockito.when(modelMapper.map(nodeDto, Node.class)).thenReturn(node);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertEquals("NODE1", response.getIdentifier());
        verify(nodeRepository).save(node);
    }

    @Test
    void saveFailureAlreadyExistsTest() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");

        Node existingNode = new Node();
        existingNode.setDeleted(false);

        Mockito.when(nodeRepository.findByIdentifier("NODE1")).thenReturn(existingNode);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertEquals("NODE1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node with identifier - NODE1 already exists", response.getMessage());
        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void saveFailureAlreadyDeletedTest() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");

        Node existingNode = new Node();
        existingNode.setDeleted(true);

        Mockito.when(nodeRepository.findByIdentifier("NODE1")).thenReturn(existingNode);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertEquals("NODE1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node with identifier - NODE1 was deleted , Please Contact the Administrator to add.", response.getMessage());
        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateSuccessTest() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");

        Node existingNode = new Node();
        existingNode.setIdentifier("NODE1");

        Mockito.when(nodeRepository.findByIdentifier("NODE1")).thenReturn(existingNode);

        NodeDto response = nodeService.update(nodeDto);

        Assertions.assertEquals("NODE1", response.getIdentifier());
        verify(modelMapper).map(nodeDto, existingNode);
        verify(nodeRepository).save(existingNode);
    }

    @Test
    void updateFailureTest() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("NODE1");

        Mockito.when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);

        NodeDto response = nodeService.update(nodeDto);

        Assertions.assertEquals("NODE1", response.getIdentifier());
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node with identifier - NODE1 not found", response.getMessage());
        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void deleteSuccessTest() {
        Node node = new Node();
        node.setIdentifier("NODE1");

        Mockito.when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);

        nodeService.delete("NODE1");

        verify(nodeRepository).findByIdentifier("NODE1");
    }

    @Test
    void findAllSuccessTest() {
        Node n1 = new Node();
        n1.setIdentifier("NODE1");
        List<Node> nodeList = List.of(n1);

        NodeDto d1 = new NodeDto();
        d1.setIdentifier("NODE1");
        List<NodeDto> nodeDtos = List.of(d1);

        Page<Node> page = new PageImpl<>(nodeList, PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(nodeRepository.findByIsDeletedFalse(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(Mockito.eq(nodeList), Mockito.any(Type.class))).thenReturn(nodeDtos);

        WsDto<NodeDto> result = nodeService.findAll(pageable);

        Assertions.assertEquals(1, result.getDtoList().size());
        Assertions.assertEquals(1, result.getTotalRecords());
        Assertions.assertEquals(0, result.getPage());
        Assertions.assertEquals(10, result.getSizePerPage());
    }

    @Test
    void toggleStatusSuccessTest() {
        Node node = new Node();
        node.setStatus(true);

        Mockito.when(nodeRepository.findByIdentifier("NODE1")).thenReturn(node);

        nodeService.toggleStatus("NODE1");

        Assertions.assertFalse(node.isStatus());
        verify(nodeRepository).save(node);
    }

    @Test
    void toggleStatusNodeNotFoundTest() {
        Mockito.when(nodeRepository.findByIdentifier("NODE1")).thenReturn(null);

        nodeService.toggleStatus("NODE1");

        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findByPathSuccessTest() {
        Node node = new Node();
        NodeDto dto = new NodeDto();

        Mockito.when(nodeRepository.findByPathAndStatus("/home", true)).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);

        NodeDto response = nodeService.findByPath("/home");

        Assertions.assertNotNull(response);
        verify(nodeRepository).findByPathAndStatus("/home", true);
    }
}