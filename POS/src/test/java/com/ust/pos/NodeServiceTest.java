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
import org.modelmapper.TypeToken;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.List;

import static org.mockito.Mockito.times;

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
    void saveTestSuccess() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Node node = new Node();

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(null);
        Mockito.when(modelMapper.map(nodeDto, Node.class)).thenReturn(node);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertEquals("Admin", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Node node = new Node();
        node.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);

        NodeDto response = nodeService.save(nodeDto);

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
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

        Node node = new Node();
        node.setIdentifier("Admin");

        Mockito.when(nodeRepository.findByIdentifier("Admin")).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);

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
        Mockito.doNothing().when(nodeRepository).deleteByIdentifier("Admin");

        nodeService.delete("Admin");

        Mockito.verify(nodeRepository, times(1)).deleteByIdentifier("Admin");
    }

    @Test
    void findAllTest() {
        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");

        Page<Node> page = new PageImpl<>(List.of(node));

        Mockito.when(nodeRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();
        Mockito.when(modelMapper.map(page.getContent(), listType)).thenReturn(List.of(nodeDto));

        Pageable pageable = PageRequest.of(0, 50, Sort.unsorted());
        WsDto<NodeDto> response = nodeService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesTest() {
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        "admin@test.com", "password", List.of()
                );

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        User user = new User();
        user.setRoles(List.of("ADMIN"));

        Mockito.when(userRepository.findByUsername("admin@test.com"))
                .thenReturn(user);

        Node node = new Node();
        node.setIdentifier("dashboard");
        node.setRoles(List.of("ADMIN"));
        node.setStatus(true);

        Mockito.when(nodeRepository.findByStatusTrue(true))
                .thenReturn(List.of(node));

        Mockito.when(nodeRepository.findByIdentifier("dashboard"))
                .thenReturn(node);

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("dashboard");

        Mockito.when(modelMapper.map(node, NodeDto.class))
                .thenReturn(nodeDto);

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("dashboard", response.get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesTest_NoAuth() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        List<NodeDto> response = nodeService.getNodesForRoles();

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void changeNodeStatusTest() {
        Node node = new Node();
        node.setIdentifier("node1");
        node.setStatus(false);

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("node1");
        nodeDto.setStatus(true);

        Mockito.when(nodeRepository.findByIdentifier("node1")).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        NodeDto response = nodeService.changeNodeStatus("node1", true);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isStatus());
        Assertions.assertEquals("node1", response.getIdentifier());

        Mockito.verify(nodeRepository, times(1)).save(node);
    }

    @Test
    void changeNodeStatus_NodeNotFound() {
        Mockito.when(nodeRepository.findByIdentifier("node1")).thenReturn(null);

        NodeDto response = nodeService.changeNodeStatus("node1", true);

        Assertions.assertNull(response);
    }

    @Test
    void findActiveNodeTest() {
        Node node = new Node();
        node.setIdentifier("node1");
        node.setStatus(true);

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("node1");

        Type listType = new TypeToken<List<NodeDto>>() {
        }.getType();

        Mockito.when(nodeRepository.findByStatusTrue(true))
                .thenReturn(List.of(node));

        Mockito.when(modelMapper.map(List.of(node), listType))
                .thenReturn(List.of(nodeDto));

        List<NodeDto> response = nodeService.findActiveNode();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals("node1", response.get(0).getIdentifier());
    }
}