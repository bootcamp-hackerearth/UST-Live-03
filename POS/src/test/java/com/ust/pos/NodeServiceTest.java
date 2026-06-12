package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@ExtendWith(MockitoExtension.class)
 class NodeServiceTest {
    @InjectMocks
    private NodeServiceImpl nodeService;

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Test
    void saveTest() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(null);
        Node node = new Node();
        node.setIdentifier("N1");
        Mockito.when(modelMapper.map(Mockito.any(NodeDto.class), Mockito.eq(Node.class)))
                .thenReturn(node);
        Mockito.when(nodeRepository.save(Mockito.any(Node.class)))
                .thenReturn(node);
        nodeDto.setSuccess(true);
        NodeDto response = nodeService.save(nodeDto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(nodeRepository).save(node);
    }

    @Test
    void saveTestFailure() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("N1");
        Node existingNode = new Node();
        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(existingNode);
        NodeDto response = nodeService.save(nodeDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
        Assertions.assertEquals(
                "Node with identifier - N1 already exists",
                response.getMessage()
        );
        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any(Node.class));
    }

    @Test
    void findByIdentifierTest() {
        Node node = new Node();
        node.setIdentifier("N1");
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);
        NodeDto response = nodeService.findByIdentifier("N1");
        Assertions.assertEquals("N1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("N1");
        Node existingNode = new Node();
        existingNode.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(existingNode);
        Mockito.doNothing().when(modelMapper)
                .map(Mockito.eq(nodeDto), Mockito.eq(existingNode));
        Mockito.when(nodeRepository.save(Mockito.any(Node.class)))
                .thenReturn(existingNode);
        nodeDto.setSuccess(true);
        NodeDto response = nodeService.update(nodeDto);
        Assertions.assertTrue(response.isSuccess());
        Mockito.verify(modelMapper)
                .map(nodeDto, existingNode);
        Mockito.verify(nodeRepository)
                .save(existingNode);
    }

    @Test
    void updateTestFailure() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifier("N1"))
                .thenReturn(null);
        NodeDto response = nodeService.update(nodeDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(
                "Node with identifier - N1 not found",
                response.getMessage()
        );

        Mockito.verify(nodeRepository, Mockito.never())
                .save(Mockito.any(Node.class));
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(nodeRepository)
                .deleteByIdentifier("N1");
        nodeService.delete("N1");
        Mockito.verify(nodeRepository)
                .deleteByIdentifier("N1");
    }

    @Test
    void findAllTest() {
        Node node = new Node();
        node.setIdentifier("N1");
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("N1");
        List<Node> nodeList = List.of(node);
        List<NodeDto> nodeDtoList = List.of(nodeDto);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Node> nodePage = new PageImpl<>(nodeList);
        Mockito.when(nodeRepository.findAll(pageable))
                .thenReturn(nodePage);
        Mockito.when(modelMapper.map(
                Mockito.eq(nodeList),
                Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(nodeDtoList);
        WsDto<NodeDto> response = nodeService.findAll(pageable);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("N1", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesTest() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "admin@test.com",
                        "password",
                        List.of()
                );
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(principal);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User user = new User();
        user.setRoles(List.of("ADMIN"));
        Mockito.when(userRepository.findByUsername("admin@test.com"))
                .thenReturn(user);
        Node node = new Node();
        node.setIdentifier("dashboard");
        node.setRoles(List.of("ADMIN"));
        Mockito.when(nodeRepository.findAll())
                .thenReturn(List.of(node));
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("dashboard");
        Mockito.when(nodeRepository.findByIdentifier("dashboard"))
                .thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("dashboard", response.get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesTest_NoAuth() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRolesTest_NullPrincipal() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(null);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRolesTest_NoMatchingRoles() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "user@test.com",
                        "password",
                        List.of()
                );
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(principal);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User user = new User();
        user.setRoles(List.of("USER"));
        Mockito.when(userRepository.findByUsername("user@test.com"))
                .thenReturn(user);
        Node node = new Node();
        node.setIdentifier("dashboard");
        node.setRoles(List.of("ADMIN"));
        Mockito.when(nodeRepository.findAll())
                .thenReturn(List.of(node));
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void getNodesForRolesTest_NodeRolesNull() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "user@test.com",
                        "password",
                        List.of()
                );

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal())
                .thenReturn(principal);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication())
                .thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User user = new User();
        user.setRoles(List.of("ADMIN"));
        Mockito.when(userRepository.findByUsername("user@test.com"))
                .thenReturn(user);
        Node node = new Node();
        node.setIdentifier("dashboard");
        node.setRoles(null);
        Mockito.when(nodeRepository.findAll())
                .thenReturn(List.of(node));
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertTrue(response.isEmpty());
    }
}