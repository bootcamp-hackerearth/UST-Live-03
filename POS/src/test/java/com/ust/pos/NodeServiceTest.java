package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.PaginationResponseDto;
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
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    void findByIdentifierTest() {
        Node node = new Node();
        node.setIdentifier("User");
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("User");
        Mockito.when(nodeRepository.findByIdentifier("User")).thenReturn(node);
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(nodeDto);
        NodeDto response = nodeService.findByIdentifier("User");
        Assertions.assertEquals("User", response.getIdentifier());
    }

    @Test
    void saveTest() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("User");
        Node node = new Node();
        node.setIdentifier("User");
        Mockito.when(nodeRepository.findByIdentifier("User")).thenReturn(null);
        Mockito.when(modelMapper.map(nodeDto, Node.class)).thenReturn(node);
        Mockito.when(nodeRepository.save(node)).thenReturn(node);
        NodeDto response = nodeService.save(nodeDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("User");
        Node existingNode = new Node();
        existingNode.setIdentifier("User");
        Mockito.when(nodeRepository.findByIdentifier("User")).thenReturn(existingNode);
        NodeDto response = nodeService.save(nodeDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void updateTest() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("User");
        Node existingNode = new Node();
        existingNode.setIdentifier("User");
        Mockito.when(nodeRepository.findByIdentifier("User")).thenReturn(existingNode);
        Mockito.when(nodeRepository.save(existingNode)).thenReturn(existingNode);
        NodeDto response = nodeService.update(nodeDto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("User");
        Mockito.when(nodeRepository.findByIdentifier("User")).thenReturn(null);
        NodeDto response = nodeService.update(nodeDto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
    }

    @Test
    void deleteTest() {
        Mockito.doNothing().when(nodeRepository).deleteByIdentifier("User");
        nodeService.delete("User");
        Mockito.verify(nodeRepository).deleteByIdentifier("User");
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
        PaginationResponseDto<NodeDto> response = nodeService.findAll(pageable);
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
        PaginationResponseDto<NodeDto> response = nodeService.findAll(null);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
    }

    @Test
    void getNodesForRolesTest() {
        org.springframework.security.core.userdetails.User springUser = new org.springframework.security.core.userdetails.User("admin", "pass", new ArrayList<>());
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
    void getNodesForRoles_Anonymous_ThrowsException() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("anonymousUser");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Assertions.assertThrows(ClassCastException.class, () -> {nodeService.getNodesForRoles();});
    }
}