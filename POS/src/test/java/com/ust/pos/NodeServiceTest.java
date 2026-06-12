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
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class NodeServiceTest {

    @InjectMocks
    private NodeServiceImpl nodeService;

    @Mock
    private NodeRepository nodeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

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
        Assertions.assertFalse(response.isSuccess());
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
        Mockito.doNothing().when(nodeRepository).deleteByIdentifier("Admin");

        nodeService.delete("Admin");

        Mockito.verify(nodeRepository).deleteByIdentifier("Admin");
    }

    @Test
    void findAllWithPageableTest() {
        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        List<Node> nodes = List.of(node);
        List<NodeDto> dtos = List.of(dto);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Node> nodePage = new PageImpl<>(nodes);

        Mockito.when(nodeRepository.findAll(pageable)).thenReturn(nodePage);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(Type.class))).thenReturn(dtos);

        WsDto<NodeDto> response = nodeService.findAll(pageable);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void findAllWithoutPageableTest() {
        Node node = new Node();
        node.setIdentifier("Admin");

        NodeDto dto = new NodeDto();
        dto.setIdentifier("Admin");

        List<Node> nodes = List.of(node);
        List<NodeDto> dtos = List.of(dto);

        Mockito.when(nodeRepository.findAll()).thenReturn(nodes);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(Type.class))).thenReturn(dtos);

        WsDto<NodeDto> response = nodeService.findAll(null);

        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesTest() {
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User("admin", "pass", new ArrayList<>());

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(springUser);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = new User();
        user.setUsername("admin");
        user.setRoles(List.of("ROLE_ADMIN"));

        Mockito.when(userRepository.findByUsername("admin")).thenReturn(user);

        Node node = new Node();
        node.setIdentifier("dashboard");
        node.setRoles(List.of("ROLE_ADMIN"));

        Mockito.when(nodeRepository.findAll()).thenReturn(List.of(node));
        Mockito.when(nodeRepository.findByIdentifier("dashboard")).thenReturn(node);

        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("dashboard");

        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("dashboard", result.get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesAnonymousTest() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<NodeDto> result = nodeService.getNodesForRoles();

        Assertions.assertTrue(result.isEmpty());
    }

}