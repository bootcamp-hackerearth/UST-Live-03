package com.ust.pos;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.WsDto;
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
    void saveTest() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(null);
        Node entity = new Node();
        Mockito.when(modelMapper.map(dto, Node.class)).thenReturn(entity);
        Mockito.when(nodeRepository.save(entity)).thenReturn(entity);
        NodeDto response = nodeService.save(dto);
        Assertions.assertEquals("N1", response.getIdentifier());
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    void saveTestFailure() {
        NodeDto dto = new NodeDto();
        dto.setIdentifier("N1");
        Node existing = new Node();
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(existing);
        NodeDto response = nodeService.save(dto);
        Assertions.assertFalse(response.isSuccess());
        Assertions.assertNotNull(response.getMessage());
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
        Pageable pageable = PageRequest.of(0, 50, Sort.by(new ArrayList<>()));
        Page<Node> nodePage = new PageImpl<>(nodes, pageable, nodes.size());
        Mockito.when(nodeRepository.findAll(pageable)).thenReturn(nodePage);
        Mockito.when(modelMapper.map(Mockito.eq(nodes), Mockito.any(java.lang.reflect.Type.class))).thenReturn(nodeDtos);
        WsDto<NodeDto> response = nodeService.findAll(pageable);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getDtoList().size());
        Assertions.assertEquals("Admin", response.getDtoList().get(0).getIdentifier());
        Assertions.assertEquals(1, response.getTotalRecords());
        Assertions.assertEquals(1, response.getTotalPage());
        Assertions.assertEquals(50, response.getSizePerPage());
        Assertions.assertEquals(0, response.getPage());
        Mockito.verify(nodeRepository, Mockito.times(1)).findAll(pageable);
        Mockito.verify(modelMapper, Mockito.times(1)).map(Mockito.eq(nodes), Mockito.any(java.lang.reflect.Type.class));
    }

    @Test
    void getNodesForRoles_principalNotNull() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User("user1", "password", List.of());
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        User user = new User();
        user.setUsername("user1");
        List<String> roles = List.of("ADMIN");
        user.setRoles(roles);
        Mockito.when(userRepository.findByUsername("user1")).thenReturn(user);
        Node node = new Node();
        node.setIdentifier("N1");
        node.setRoles(roles);
        List<Node> nodeList = List.of(node);
        Mockito.when(nodeRepository.findAll()).thenReturn(nodeList);
        Mockito.when(nodeRepository.findByIdentifier("N1")).thenReturn(node);
        NodeDto dto = new NodeDto();
        Mockito.when(modelMapper.map(node, NodeDto.class)).thenReturn(dto);
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertEquals(1, response.size());
        Mockito.verify(userRepository).findByUsername("user1");
        Mockito.verify(nodeRepository).findAll();
        Mockito.verify(nodeRepository).findByIdentifier("N1");
    }

    @Test
    void getNodesForRolesFailureTest() {
        SecurityContextHolder.clearContext();
        List<NodeDto> response = nodeService.getNodesForRoles();
        Assertions.assertTrue(response.isEmpty());
    }
}
