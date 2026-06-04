package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.modell.Node;
import com.ust.pos.modell.NodeRepository;
import com.ust.pos.modell.User;
import com.ust.pos.modell.UserRepository;
import com.ust.pos.node.service.impl.NodeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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
    void saveTest_success() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Mockito.when(nodeRepository.existsByIdentifier("N1")).thenReturn(false);
        Node node = new Node();
        Mockito.when(nodeRepository.save(Mockito.any(Node.class))).thenReturn(node);
        NodeDto response = nodeService.save(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTest_failure() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Mockito.when(nodeRepository.existsByIdentifier("N1")).thenReturn(true);
        NodeDto response = nodeService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals("Node already exists", response.getMessage());
    }

    @Test
    void findByIdentifierTest() {
        Node entity = new Node();
        entity.setIdentifier("N1");
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(entity);
        Mockito.when(modelMapper.map(entity, NodeDto.class)).thenReturn(dto);
        NodeDto response = nodeService.findByIdentifier("N1");
        Assertions.assertEquals("N1", response.getIdentifier());
    }

    @Test
    void updateTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Node existing = new Node();
        existing.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(existing);
        Mockito.when(nodeRepository.save(existing)).thenReturn(existing);
        NodeDto response = nodeService.update(dto);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void updateTestFailure() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        NodeDto response = nodeService.update(dto);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    void deleteTest() {
        nodeService.delete("N1");
        Mockito.verify(nodeRepository).deleteByIdentifier("N1");
    }

    @Test
    void findAllTest() {
        Node node = new Node();
        node.setIdentifier("Admin");
        NodeDto nodeDto = new NodeDto();
        nodeDto.setIdentifier("Admin");
        List<Node> nodes = List.of(node);
        List<NodeDto> nodeDtos = List.of(nodeDto);
        Page<Node> nodePage = new PageImpl<>(nodes, PageRequest.of(0, 2), nodes.size());
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Mockito.when(nodeRepository.findAll(pageable)).thenReturn(nodePage);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(java.lang.reflect.Type.class))).thenReturn(nodeDtos);
        List<NodeDto> response = nodeService.findAll(pageable);
        Assertions.assertEquals(1, response.size());
    }

    @Test
    void getNodesForRoles_principalNotNull() {

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User("user1", "password", List.of()
        );
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User user = new User();
        user.setUsername("user1");
        user.setRoles(List.of());
        Mockito.when(userRepository.findByUsername("user1")).thenReturn(user);

        Node node = new Node();
        List<Node> nodeList = List.of(node);
        NodeDto dto = new NodeDto();
        List<NodeDto> dtoList = List.of(dto);
        Mockito.when(nodeRepository.findByRoles(user.getRoles())).thenReturn(nodeList);
        Mockito.when(modelMapper.map(Mockito.eq(nodeList), Mockito.any(java.lang.reflect.Type.class)
        )).thenReturn(dtoList);

        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertEquals(1, response.size());
        Mockito.verify(userRepository).findByUsername("user1");
    }

    @Test
    void getNodesForRolesFailureTest() {
        SecurityContextHolder.clearContext();
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertTrue(response.isEmpty());
    }
}
