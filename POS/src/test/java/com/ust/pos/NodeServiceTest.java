package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginatedResponseDto;
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

import java.util.List;


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
    void getNodesForRolesTest() {

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "Admin",
                        "password",
                        new java.util.ArrayList<>()
                );

        org.springframework.security.core.Authentication authentication =
                Mockito.mock(org.springframework.security.core.Authentication.class);

        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        org.springframework.security.core.context.SecurityContext securityContext =
                Mockito.mock(org.springframework.security.core.context.SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setUsername("Admin");
        user.setRoles(java.util.Arrays.asList("ROLE_ADMIN"));

        Mockito.when(userRepository.findByUsername("Admin")).thenReturn(user);

        Node node1 = new Node();
        node1.setIdentifier("N1");
        node1.setRoles(java.util.Arrays.asList("ROLE_ADMIN"));

        Node node2 = new Node();
        node2.setIdentifier("N2");
        node2.setRoles(java.util.Arrays.asList("ROLE_USER"));

        List<Node> nodes = List.of(node1, node2);

        Mockito.when(nodeRepository.findAll()).thenReturn(nodes);

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("N1");

        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(node1);
        Mockito.when(modelMapper.map(node1, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("N1", response.get(0).getIdentifier());
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
        Assertions.assertNull(response.getMessage());
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    void saveTestFailure() {

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");
        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);
        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertNotNull(response.getMessage(), "Message cannot be null");

        Assertions.assertEquals(false, response.isSuccess());

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
    void updateTest() {

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Node existingNode = new Node();
        existingNode.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(existingNode);
        Mockito.when(nodeRepository.save(existingNode))
                .thenReturn(existingNode);

        NodeDto response = nodeService.update(nodeDto);

        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(null);

        NodeDto response = nodeService.update(nodeDto);

        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {

        Mockito.doNothing().when(nodeRepository)
                .deleteByIdentifier("Admin");

        nodeService.delete("Admin");

        Mockito.verify(nodeRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {

        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        List<Node> nodes = List.of(node);
        List<NodeDto> nodeDtos = List.of(nodeDto);

        Page<Node> nodePage = new PageImpl<>(nodes);

        Mockito.when(nodeRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(nodePage);

        Mockito.when(modelMapper.map(
                Mockito.eq(nodes),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(nodeDtos);

        PaginatedResponseDto<NodeDto> response = nodeService.findAll(PageRequest.of(0, 10));

        Assertions.assertEquals(1, response.getItems().size());
    }

    @Test
    void findAllActiveTest() {

        Node node = new Node();
        node.setIdentifier("Admin");
        node.setStatus(true);

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        List<Node> nodes = List.of(node);
        List<NodeDto> nodeDtos = List.of(nodeDto);

        Mockito.when(nodeRepository.findByStatus(true)).thenReturn(nodes);
        Mockito.when(modelMapper.map(
                Mockito.eq(nodes),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(nodeDtos);

        List<NodeDto> response = nodeService.findAllActive();

        Assertions.assertEquals(1, response.size());
    }

    @Test
    void changeStatusTest() {

        Node node = new Node();
        node.setIdentifier("Admin");
        node.setStatus(false);

        Mockito.when(nodeRepository.findByIdentifier("Admin"))
                .thenReturn(node);

        Mockito.when(nodeRepository.save(node))
                .thenReturn(node);

        nodeService.changeStatus("Admin", true);

        Assertions.assertTrue(node.getStatus());

        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void getNodesForRolesAuthenticationNullTest() {

        org.springframework.security.core.context.SecurityContext securityContext =
                Mockito.mock(org.springframework.security.core.context.SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(null);

        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRolesPrincipalNullTest() {

        org.springframework.security.core.Authentication authentication =
                Mockito.mock(org.springframework.security.core.Authentication.class);

        Mockito.when(authentication.getPrincipal()).thenReturn(null);

        org.springframework.security.core.context.SecurityContext securityContext =
                Mockito.mock(org.springframework.security.core.context.SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRolesNoMatchingRolesTest() {

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "Admin",
                        "password",
                        new java.util.ArrayList<>()
                );

        org.springframework.security.core.Authentication authentication =
                Mockito.mock(org.springframework.security.core.Authentication.class);

        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        org.springframework.security.core.context.SecurityContext securityContext =
                Mockito.mock(org.springframework.security.core.context.SecurityContext.class);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setUsername("Admin");
        user.setRoles(java.util.Arrays.asList("ROLE_ADMIN"));

        Mockito.when(userRepository.findByUsername("Admin"))
                .thenReturn(user);

        Node node1 = new Node();
        node1.setIdentifier("N1");
        node1.setRoles(null);

        Node node2 = new Node();
        node2.setIdentifier("N2");
        node2.setRoles(java.util.Arrays.asList("ROLE_USER"));

        Mockito.when(nodeRepository.findAll())
                .thenReturn(List.of(node1, node2));

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertTrue(response.isEmpty());
    }
}